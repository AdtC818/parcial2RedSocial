package myPacket.parcial_2.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import myPacket.parcial_2.model.UsuarioDocument;
import myPacket.parcial_2.service.UsuarioService;

@Controller
@RequestMapping("/usuariosDocument")
public class UsuarioDocumentController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuariosDocument(Model model) {
        model.addAttribute("usuariosDocument", usuarioService.listarUsuariosDocument());
        model.addAttribute("nuevoUsuarioDocument", new UsuarioDocument());
        return "usuariosDocument/lista"; 
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new UsuarioDocument());
        return "usuariosDocument/formulario"; 
    }

    @PostMapping
    public String guardarUsuarioDocument(@ModelAttribute UsuarioDocument usuario) {
        usuarioService.crearUsuarioDocument(usuario);
        return "redirect:/usuariosDocument";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable String id, Model model) {
        Optional<UsuarioDocument> usuario = usuarioService.obtenerUsuarioDocumentPorId(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            return "usuariosDocument/formulario"; 
        } else {
            return "redirect:/usuariosDocument";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarUsuarioDocument(@PathVariable String id, @ModelAttribute UsuarioDocument usuario) {
        usuarioService.actualizarUsuarioDocument(id, usuario);
        return "redirect:/usuariosDocument";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuarioDocument(@PathVariable String id) {
        usuarioService.eliminarUsuarioDocument(id);
        return "redirect:/usuariosDocument";
    }
}
