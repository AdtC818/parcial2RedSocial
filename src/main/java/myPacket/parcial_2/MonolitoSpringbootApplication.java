package myPacket.parcial_2;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication; // <-- 1. Importa la clase Evento

import myPacket.parcial_2.model.Evento;
import myPacket.parcial_2.service.EventoService;
import myPacket.parcial_2.service.Neo4jDirectService; // <-- 2. Importa el servicio de Neo4j
import myPacket.parcial_2.service.UsuarioService; // <-- 3. Importa la clase List

@SpringBootApplication
public class MonolitoSpringbootApplication implements CommandLineRunner {
    
    private final UsuarioService usuarioService;
    private final EventoService eventoService;
    private final Neo4jDirectService neo4jDirectService; // <-- 4. AÑADE ESTA LÍNEA

    // 5. AÑADE Neo4jDirectService AL CONSTRUCTOR
    public MonolitoSpringbootApplication(UsuarioService usuarioService, EventoService eventoService, Neo4jDirectService neo4jDirectService) {
        this.usuarioService = usuarioService;
        this.eventoService = eventoService;
        this.neo4jDirectService = neo4jDirectService; // <-- 6. ASÍGNALO AQUÍ
    }
    
    public static void main(String[] args) {
        SpringApplication.run(MonolitoSpringbootApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Inicializar datos de usuarios y hobbies
        usuarioService.inicializarHobbiesPrueba();
        usuarioService.inicializarUsuariosYAmistades();
        
        // Inicializar eventos de prueba en MariaDB
        eventoService.inicializarEventosPrueba();
        
        // ======================================================================
        // 7. AÑADE EL NUEVO BLOQUE DE SINCRONIZACIÓN AQUÍ 🔄
        // ======================================================================
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