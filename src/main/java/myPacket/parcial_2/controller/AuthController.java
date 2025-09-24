package myPacket.parcial_2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import myPacket.parcial_2.service.UsuarioService;
import myPacket.parcial_2.service.Neo4jDirectService;
import myPacket.parcial_2.service.EventoService;
import myPacket.parcial_2.model.Usuario;
import myPacket.parcial_2.model.UsuarioHobbies;
import myPacket.parcial_2.model.UsuarioNeo4j;
import myPacket.parcial_2.model.Evento;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Controller
public class AuthController {
    
    private final UsuarioService usuarioService;
    private final Neo4jDirectService neo4jDirectService;
    private final EventoService eventoService;
    
    public AuthController(UsuarioService usuarioService, Neo4jDirectService neo4jDirectService, EventoService eventoService) {
        this.usuarioService = usuarioService;
        this.neo4jDirectService = neo4jDirectService;
        this.eventoService = eventoService;
    }
    
    // Mostrar página de login
    @GetMapping("/")
    public String mostrarLogin() {
        return "login";
    }
    
    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        Optional<Usuario> usuarioOpt = usuarioService.verificarLogin(email, password);
        if (usuarioOpt.isPresent()) {
            // Login exitoso
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("usuario", usuario);
            return "redirect:/dashboard";
        } else {
            // Login fallido
            model.addAttribute("error", "Email o contraseña incorrectos");
            return "login";
        }
    }
    
    // Mostrar dashboard después del login
    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/";
        }
        // Obtener hobbies del usuario actual
        Optional<UsuarioHobbies> hobbiesOpt = usuarioService.obtenerHobbiesUsuario(usuario.getId());
        model.addAttribute("usuario", usuario);
        if (hobbiesOpt.isPresent()) {
            model.addAttribute("hobbies", hobbiesOpt.get().getHobbies());
        } else {
            model.addAttribute("hobbies", Arrays.asList("Sin hobbies"));
        }
        return "dashboard";
    }
    
    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ===== NUEVAS FUNCIONALIDADES PARA NEO4J =====

    // API para obtener amigos del usuario logueado
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
                "email", usuario.getEmail()
            ));
            response.put("success", true);
        } catch (Exception e) {
            response.put("error", "Error obteniendo amigos: " + e.getMessage());
            response.put("success", false);
        }
        
        return response;
    }

    // API para obtener amigos con sus hobbies filtrados
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
                    
                    // Aplicar filtro si se especifica
                    boolean incluir = true;
                    if (filtroHobby != null && !filtroHobby.isEmpty()) {
                        incluir = hobbies.stream().anyMatch(hobby -> 
                            hobby.toLowerCase().contains(filtroHobby.toLowerCase()));
                    }
                    
                    if (incluir) {
                        amigosConHobbies.add(amigoData);
                    }
                } else {
                    amigoData.put("hobbies", Arrays.asList("Sin hobbies"));
                    // Si no hay filtro, incluir amigos sin hobbies
                    if (filtroHobby == null || filtroHobby.isEmpty()) {
                        amigosConHobbies.add(amigoData);
                    }
                }
            }
            
            // Agregar información del usuario actual
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

    // API para obtener todos los hobbies únicos
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
            
            // Agregar hobbies del usuario actual
            Optional<UsuarioHobbies> hobbiesUsuario = usuarioService.obtenerHobbiesUsuario(usuario.getId());
            if (hobbiesUsuario.isPresent()) {
                todosHobbies.addAll(hobbiesUsuario.get().getHobbies());
            }
            
            // Agregar hobbies de los amigos
            for (UsuarioNeo4j amigo : amigos) {
                Optional<UsuarioHobbies> hobbiesOpt = usuarioService.obtenerHobbiesUsuario(amigo.getId());
                if (hobbiesOpt.isPresent()) {
                    todosHobbies.addAll(hobbiesOpt.get().getHobbies());
                }
            }
            
            // Remover duplicados y ordenar
            List<String> hobbiesUnicos = todosHobbies.stream().distinct().sorted().toList();
            
            response.put("hobbies", hobbiesUnicos);
            response.put("success", true);
            
        } catch (Exception e) {
            response.put("error", "Error obteniendo hobbies: " + e.getMessage());
            response.put("success", false);
        }
        
        return response;
    }
    
    // ===== NUEVAS FUNCIONALIDADES PARA EVENTOS =====
    
    // API para obtener todos los eventos
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
    
    // API para buscar y filtrar eventos
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
            
            // Aplicar filtros en orden de prioridad
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
                "precio", precio != null ? precio : ""
            ));
            response.put("success", true);
            
        } catch (Exception e) {
            response.put("error", "Error filtrando eventos: " + e.getMessage());
            response.put("success", false);
        }
        
        return response;
    }
    // API para obtener categorías disponibles
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
}