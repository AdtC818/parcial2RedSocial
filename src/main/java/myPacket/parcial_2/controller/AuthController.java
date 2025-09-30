package myPacket.parcial_2.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import myPacket.parcial_2.model.Evento;
import myPacket.parcial_2.model.Usuario;
import myPacket.parcial_2.model.UsuarioHobbies;
import myPacket.parcial_2.model.UsuarioNeo4j;
import myPacket.parcial_2.service.EventoService;
import myPacket.parcial_2.service.Neo4jDirectService;
import myPacket.parcial_2.service.UsuarioService;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private final Neo4jDirectService neo4jDirectService;
    private final EventoService eventoService;

    public AuthController(UsuarioService usuarioService, Neo4jDirectService neo4jDirectService,
            EventoService eventoService) {
        this.usuarioService = usuarioService;
        this.neo4jDirectService = neo4jDirectService;
        this.eventoService = eventoService;
    }

    @GetMapping("/")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        Optional<Usuario> usuarioOpt = usuarioService.verificarLogin(email, password);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("usuario", usuario);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Email o contraseña incorrectos");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/";
        }

        // Obtener hobbies (tu lógica actual)
        Optional<UsuarioHobbies> hobbiesOpt = usuarioService.obtenerHobbiesUsuario(usuario.getId());
        List<String> hobbiesList = hobbiesOpt.map(UsuarioHobbies::getHobbies).orElse(Arrays.asList("Sin hobbies"));

        // --- CAMBIO: Añadir datos al modelo para Thymeleaf ---
        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("hobbiesUsuario", hobbiesList);

        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/hobbies/agregar")
    public String agregarHobby(@RequestParam String hobby, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/";
        }

        if (hobby != null && !hobby.trim().isEmpty()) {
            usuarioService.agregarHobby(usuario.getId(), hobby.trim());
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/hobbies/eliminar")
    public String eliminarHobby(@RequestParam String hobby, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/";
        }

        usuarioService.eliminarHobby(usuario.getId(), hobby);

        return "redirect:/dashboard";
    }

    @GetMapping("/api/amigos")
    @ResponseBody
    public Map<String, Object> obtenerAmigos(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            List<UsuarioNeo4j> amigos = neo4jDirectService.obtenerAmigos(usuario.getId());
            response.put("amigos", amigos);
            response.put("usuarioActual", Map.of(
                    "id", usuario.getId(),
                    "nombre", usuario.getNombre(),
                    "email", usuario.getEmail()));
            response.put("success", true);
        } catch (Exception e) {
            response.put("error", "Error obteniendo amigos: " + e.getMessage());
            response.put("success", false);
        }

        return response;
    }

    @GetMapping("/api/amigos-hobbies")
    @ResponseBody
    public Map<String, Object> obtenerAmigosConHobbies(
            HttpSession session,
            @RequestParam(required = false) String filtroHobby) {

        Map<String, Object> response = new HashMap<>();

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            List<UsuarioNeo4j> amigos = neo4jDirectService.obtenerAmigos(usuario.getId());
            List<Map<String, Object>> amigosConHobbies = new ArrayList<>();

            for (UsuarioNeo4j amigo : amigos) {
                Optional<UsuarioHobbies> hobbiesOpt = usuarioService.obtenerHobbiesUsuario(amigo.getId());

                Map<String, Object> amigoData = new HashMap<>();
                amigoData.put("id", amigo.getId());
                amigoData.put("nombre", amigo.getNombre());
                amigoData.put("email", amigo.getEmail());

                if (hobbiesOpt.isPresent()) {
                    List<String> hobbies = hobbiesOpt.get().getHobbies();
                    amigoData.put("hobbies", hobbies);

                    boolean incluir = true;
                    if (filtroHobby != null && !filtroHobby.isEmpty()) {
                        incluir = hobbies.stream()
                                .anyMatch(hobby -> hobby.toLowerCase().contains(filtroHobby.toLowerCase()));
                    }

                    if (incluir) {
                        amigosConHobbies.add(amigoData);
                    }
                } else {
                    amigoData.put("hobbies", Arrays.asList("Sin hobbies"));
                    if (filtroHobby == null || filtroHobby.isEmpty()) {
                        amigosConHobbies.add(amigoData);
                    }
                }
            }

            Optional<UsuarioHobbies> hobbiesUsuario = usuarioService.obtenerHobbiesUsuario(usuario.getId());
            Map<String, Object> usuarioActual = new HashMap<>();
            usuarioActual.put("id", usuario.getId());
            usuarioActual.put("nombre", usuario.getNombre());
            usuarioActual.put("email", usuario.getEmail());
            if (hobbiesUsuario.isPresent()) {
                usuarioActual.put("hobbies", hobbiesUsuario.get().getHobbies());
            } else {
                usuarioActual.put("hobbies", Arrays.asList("Sin hobbies"));
            }

            response.put("amigosConHobbies", amigosConHobbies);
            response.put("usuarioActual", usuarioActual);
            response.put("filtroAplicado", filtroHobby);
            response.put("success", true);

        } catch (Exception e) {
            response.put("error", "Error obteniendo datos: " + e.getMessage());
            response.put("success", false);
        }

        return response;
    }

    @GetMapping("/api/hobbies-disponibles")
    @ResponseBody
    public Map<String, Object> obtenerHobbiesDisponibles(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            List<UsuarioNeo4j> amigos = neo4jDirectService.obtenerAmigos(usuario.getId());
            List<String> todosHobbies = new ArrayList<>();

            Optional<UsuarioHobbies> hobbiesUsuario = usuarioService.obtenerHobbiesUsuario(usuario.getId());
            if (hobbiesUsuario.isPresent()) {
                todosHobbies.addAll(hobbiesUsuario.get().getHobbies());
            }

            for (UsuarioNeo4j amigo : amigos) {
                Optional<UsuarioHobbies> hobbiesOpt = usuarioService.obtenerHobbiesUsuario(amigo.getId());
                if (hobbiesOpt.isPresent()) {
                    todosHobbies.addAll(hobbiesOpt.get().getHobbies());
                }
            }

            List<String> hobbiesUnicos = todosHobbies.stream().distinct().sorted().toList();

            response.put("hobbies", hobbiesUnicos);
            response.put("success", true);

        } catch (Exception e) {
            response.put("error", "Error obteniendo hobbies: " + e.getMessage());
            response.put("success", false);
        }

        return response;
    }

    @GetMapping("/api/eventos")
    @ResponseBody
    public Map<String, Object> obtenerEventos(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            List<Evento> eventos = eventoService.obtenerEventosProximos();
            response.put("eventos", eventos);
            response.put("totalEventos", eventos.size());
            response.put("success", true);
        } catch (Exception e) {
            response.put("error", "Error obteniendo eventos: " + e.getMessage());
            response.put("success", false);
        }

        return response;
    }

    @GetMapping("/api/eventos/filtrar")
    @ResponseBody
    public Map<String, Object> filtrarEventos(
            HttpSession session,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String precio) {

        Map<String, Object> response = new HashMap<>();

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            List<Evento> eventos;

            if (busqueda != null && !busqueda.trim().isEmpty()) {
                eventos = eventoService.buscarEventos(busqueda);
            } else if (categoria != null && !categoria.trim().isEmpty()) {
                eventos = eventoService.filtrarPorCategoria(categoria);
            } else if (precio != null && !precio.trim().isEmpty()) {
                eventos = eventoService.filtrarPorPrecio(precio);
            } else {
                eventos = eventoService.obtenerEventosProximos();
            }

            response.put("eventos", eventos);
            response.put("totalEventos", eventos.size());
            response.put("filtrosAplicados", Map.of(
                    "busqueda", busqueda != null ? busqueda : "",
                    "categoria", categoria != null ? categoria : "",
                    "precio", precio != null ? precio : ""));
            response.put("success", true);

        } catch (Exception e) {
            response.put("error", "Error filtrando eventos: " + e.getMessage());
            response.put("success", false);
        }

        return response;
    }

    @GetMapping("/api/eventos/categorias")
    @ResponseBody
    public Map<String, Object> obtenerCategorias(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            List<String> categorias = eventoService.obtenerCategorias();
            response.put("categorias", categorias);
            response.put("success", true);

        } catch (Exception e) {
            response.put("error", "Error obteniendo categorías: " + e.getMessage());
            response.put("success", false);
        }

        return response;
    }

    @GetMapping("/eventos/nuevo")
    public String mostrarFormularioNuevoEvento(Model model) {
        model.addAttribute("evento", new Evento());
        model.addAttribute("pageTitle", "Crear Nuevo Evento"); // Añadido pageTitle
        return "formulario-evento";
    }

    @PostMapping("/eventos/guardar")
    public String guardarEvento(@ModelAttribute("evento") Evento evento) {
        eventoService.guardarEvento(evento);
        return "redirect:/dashboard";
    }

    @GetMapping("/eventos/editar/{id}")
    public String mostrarFormularioEditarEvento(@PathVariable Long id, Model model) {
        Evento evento = eventoService.obtenerEventoPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Evento inválido: " + id));
        model.addAttribute("evento", evento);
        model.addAttribute("pageTitle", "Editar Evento"); // Añadido pageTitle
        return "formulario-evento";
    }

    @GetMapping("/eventos/eliminar/{id}")
    public String eliminarEvento(@PathVariable Long id) {
        eventoService.desactivarEvento(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("usuario") Usuario usuario, Model model) {
        if (usuarioService.emailYaExiste(usuario.getEmail())) {
            model.addAttribute("error", "El correo electrónico ya está registrado.");
            return "registro";
        }

        usuarioService.guardarUsuario(usuario);

        neo4jDirectService.crearOActualizarNodoUsuario(usuario);

        return "redirect:/?exito=true";
    }

    @GetMapping("/api/usuarios/buscar")
    @ResponseBody
    public List<Usuario> buscarUsuarios(@RequestParam("q") String q, HttpSession session) {
        Usuario usuarioActual = (Usuario) session.getAttribute("usuario");
        if (usuarioActual == null || q == null || q.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return usuarioService.buscarPosiblesAmigos(usuarioActual.getId(), q.trim());
    }

    @PostMapping("/api/amigos/agregar/{amigoId}")
    @ResponseBody
    public Map<String, Object> agregarAmigo(@PathVariable Long amigoId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioActual = (Usuario) session.getAttribute("usuario");

        if (usuarioActual == null) {
            response.put("success", false);
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            neo4jDirectService.crearAmistad(usuarioActual.getId(), amigoId);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }

    @PostMapping("/api/eventos/{eventoId}/asistir")
    @ResponseBody
    public Map<String, Object> asistirAEvento(@PathVariable Long eventoId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            response.put("success", false);
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            // Llama al nuevo método del servicio de Neo4j
            neo4jDirectService.crearRelacionAsistencia(usuario.getId(), eventoId);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Error al procesar la solicitud: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/api/amigos/filtrar-por-evento")
    @ResponseBody
    public Map<String, Object> filtrarAmigosPorEvento(@RequestParam Long eventoId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            List<UsuarioNeo4j> amigosFiltrados = neo4jDirectService.obtenerAmigosQueAsistenAEvento(usuario.getId(),
                    eventoId);

            response.put("amigos", amigosFiltrados);
            response.put("usuarioActual", Map.of(
                    "id", usuario.getId(),
                    "nombre", usuario.getNombre(),
                    "email", usuario.getEmail()));
            response.put("success", true);

        } catch (Exception e) {
            response.put("error", "Error filtrando amigos: " + e.getMessage());
            response.put("success", false);
        }
        return response;
    }

    @GetMapping("/api/mis-eventos-ids")
    @ResponseBody
    public Map<String, Object> obtenerMisEventosIds(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            response.put("success", false);
            return response;
        }

        List<Long> misEventosIds = neo4jDirectService.obtenerIdsDeEventosAsistidos(usuario.getId());
        response.put("success", true);
        response.put("eventoIds", misEventosIds);
        return response;
    }

    @PostMapping("/api/eventos/{eventoId}/cancelar")
    @ResponseBody
    public Map<String, Object> cancelarAsistenciaAEvento(@PathVariable Long eventoId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            response.put("success", false);
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            neo4jDirectService.eliminarRelacionAsistencia(usuario.getId(), eventoId);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Error al procesar la cancelación: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/api/amigos/eliminar/{amigoId}")
    @ResponseBody
    public Map<String, Object> eliminarAmigo(@PathVariable Long amigoId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioActual = (Usuario) session.getAttribute("usuario");

        if (usuarioActual == null) {
            response.put("success", false);
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            neo4jDirectService.eliminarAmistad(usuarioActual.getId(), amigoId);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }
}