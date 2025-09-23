package myPacket.parcial_2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import myPacket.parcial_2.service.UsuarioService;
import myPacket.parcial_2.model.Usuario;
import myPacket.parcial_2.model.UsuarioHobbies;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.Arrays;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
}