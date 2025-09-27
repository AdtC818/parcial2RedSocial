package myPacket.parcial_2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import myPacket.parcial_2.model.UsuarioDTO;
import myPacket.parcial_2.repository.jpa.UsuarioRepository;
import myPacket.parcial_2.repository.mongodb.UsuarioDocumentRepository;
import myPacket.parcial_2.model.Usuario;
import myPacket.parcial_2.model.UsuarioDocument;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/usuariosDocument")
public class UsuarioDocumentController {

    @Autowired
    private UsuarioDocumentRepository usuarioDocumentRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listar usuarios de SQL y Mongo
    @GetMapping
    public String listarUsuarios(Model model) {
        Map<String, UsuarioDTO> mapaUsuarios = new LinkedHashMap<>();

        // Primero los de SQL
        for (Usuario u : usuarioRepository.findAll()) {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setId(String.valueOf(u.getId()));
            dto.setNombre(u.getNombre());
            dto.setEmail(u.getEmail());
            dto.setOrigen("SQL");
            mapaUsuarios.put(u.getEmail(), dto); // clave = email
        }

        // Luego los de Mongo (solo si no existe en SQL)
        for (UsuarioDocument u : usuarioDocumentRepository.findAll()) {
            if (!mapaUsuarios.containsKey(u.getEmail())) {
                UsuarioDTO dto = new UsuarioDTO();
                dto.setId(u.getId());
                dto.setNombre(u.getNombre());
                dto.setEmail(u.getEmail());
                dto.setOrigen("MONGO");
                mapaUsuarios.put(u.getEmail(), dto);
            }
        }

        model.addAttribute("usuariosDocument", new ArrayList<>(mapaUsuarios.values()));
        model.addAttribute("nuevoUsuarioDocument", new UsuarioDocument());
        return "usuariosDocument/lista";
    }

    // Guardar en Mongo
    @PostMapping
    public String guardarUsuario(@ModelAttribute UsuarioDocument nuevoUsuarioDocument) {
        usuarioDocumentRepository.save(nuevoUsuarioDocument);
        return "redirect:/usuariosDocument";
    }

    // Eliminar tanto de Mongo como SQL
    @GetMapping("/eliminar/{origen}/{id}")
    public String eliminarUsuario(@PathVariable String origen, @PathVariable String id) {
        if ("MONGO".equals(origen)) {
            usuarioDocumentRepository.deleteById(id);
        } else if ("SQL".equals(origen)) {
            usuarioRepository.deleteById(Long.parseLong(id));
        }
        return "redirect:/usuariosDocument";
    }

    // Editar usuario
    @GetMapping("/editar/{origen}/{id}")
    public String editarUsuario(@PathVariable String origen,
            @PathVariable String id,
            Model model) {
        UsuarioDTO dto = new UsuarioDTO();

        if ("MONGO".equalsIgnoreCase(origen)) {
            UsuarioDocument usuario = usuarioDocumentRepository.findById(id).orElse(null);
            if (usuario != null) {
                dto.setId(usuario.getId());
                dto.setNombre(usuario.getNombre());
                dto.setEmail(usuario.getEmail());
                dto.setOrigen("MONGO");
            }
        } else if ("SQL".equalsIgnoreCase(origen)) {
            Usuario usuario = usuarioRepository.findById(Long.parseLong(id)).orElse(null);
            if (usuario != null) {
                dto.setId(String.valueOf(usuario.getId()));
                dto.setNombre(usuario.getNombre());
                dto.setEmail(usuario.getEmail());
                dto.setOrigen("SQL");
            }
        }

        model.addAttribute("usuario", dto); // se llamará igual que en formulario.html
        return "usuariosDocument/formulario";
    }

    // Actualizar usuario (POST desde formulario)
    @PostMapping("/actualizar")
    public String actualizarUsuario(@ModelAttribute("usuario") UsuarioDTO usuario) {
        if ("MONGO".equalsIgnoreCase(usuario.getOrigen())) {
            UsuarioDocument doc = usuarioDocumentRepository.findById(usuario.getId()).orElse(null);
            if (doc != null) {
                doc.setNombre(usuario.getNombre());
                doc.setEmail(usuario.getEmail());
                usuarioDocumentRepository.save(doc);
            }
        } else if ("SQL".equalsIgnoreCase(usuario.getOrigen())) {
            Usuario entity = usuarioRepository.findById(Long.parseLong(usuario.getId())).orElse(null);
            if (entity != null) {
                entity.setNombre(usuario.getNombre());
                entity.setEmail(usuario.getEmail());
                usuarioRepository.save(entity);
            }
        }
        return "redirect:/usuariosDocument";
    }

    // Crear nuevo usuario en Mongo
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuarioEditar", new UsuarioDocument());
        model.addAttribute("origen", "MONGO");
        return "usuariosDocument/formulario"; // apunta a formulario.html
    }
}
