package myPacket.parcial_2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import myPacket.parcial_2.service.UsuarioService;
import myPacket.parcial_2.service.EventoService;

@SpringBootApplication
public class MonolitoSpringbootApplication implements CommandLineRunner {
    
    private final UsuarioService usuarioService;
    private final EventoService eventoService;
    
    public MonolitoSpringbootApplication(UsuarioService usuarioService, EventoService eventoService) {
        this.usuarioService = usuarioService;
        this.eventoService = eventoService;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(MonolitoSpringbootApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Inicializar datos de usuarios y hobbies
        usuarioService.inicializarHobbiesPrueba();
        usuarioService.inicializarUsuariosYAmistades();
        
        // Inicializar eventos de prueba
        eventoService.inicializarEventosPrueba();
        
        System.out.println("=== APLICACIÓN INICIADA CORRECTAMENTE ===");
        System.out.println("- Usuarios y hobbies inicializados");
        System.out.println("- Relaciones de amistad en Neo4j configuradas");
        System.out.println("- Eventos de prueba creados");
        System.out.println("=========================================");
    }
}