package myPacket.parcial_2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import myPacket.parcial_2.service.UsuarioService;

@SpringBootApplication
public class MonolitoSpringbootApplication implements CommandLineRunner {
    
    private final UsuarioService usuarioService;
    
    public MonolitoSpringbootApplication(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(MonolitoSpringbootApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        usuarioService.inicializarHobbiesPrueba();
        usuarioService.inicializarUsuariosYAmistades();
    }
}