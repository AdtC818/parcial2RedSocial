package myPacket.parcial_2;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication; 

import myPacket.parcial_2.model.Evento;
import myPacket.parcial_2.service.EventoService;
import myPacket.parcial_2.service.Neo4jDirectService; 
import myPacket.parcial_2.service.UsuarioService; 

@SpringBootApplication
public class MonolitoSpringbootApplication implements CommandLineRunner {
    
    private final UsuarioService usuarioService;
    private final EventoService eventoService;
    private final Neo4jDirectService neo4jDirectService; 

    public MonolitoSpringbootApplication(UsuarioService usuarioService, EventoService eventoService, Neo4jDirectService neo4jDirectService) {
        this.usuarioService = usuarioService;
        this.eventoService = eventoService;
        this.neo4jDirectService = neo4jDirectService;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(MonolitoSpringbootApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        usuarioService.inicializarHobbiesPrueba();
        usuarioService.inicializarUsuariosYAmistades();
        
        eventoService.inicializarEventosPrueba();
        
      
        System.out.println("--- Sincronizando eventos existentes de MariaDB a Neo4j ---");
        List<Evento> todosLosEventos = eventoService.obtenerEventosActivos();
        for (Evento evento : todosLosEventos) {
            neo4jDirectService.crearOActualizarNodoEvento(evento);
        }
        System.out.println("--- Sincronización de eventos completada ---");
        // ======================================================================

        System.out.println("=== APLICACIÓN INICIADA CORRECTAMENTE ===");
        System.out.println("- Usuarios y hobbies inicializados");
        System.out.println("- Relaciones de amistad en Neo4j configuradas");
        System.out.println("- Eventos de prueba creados en MariaDB");
        System.out.println("- Nodos de Evento sincronizados en Neo4j"); // <-- Mensaje actualizado
        System.out.println("=========================================");
    }
}