package myPacket.parcial_2.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myPacket.parcial_2.model.Evento;
import myPacket.parcial_2.repository.jpa.EventoRepository;

@Service
public class EventoService {
    
    private final EventoRepository eventoRepository;
    private final Neo4jDirectService neo4jDirectService;
    
    @Autowired
    public EventoService(EventoRepository eventoRepository,  Neo4jDirectService neo4jDirectService) {
        this.eventoRepository = eventoRepository;
        this.neo4jDirectService = neo4jDirectService;
    }
    
    // Obtener todos los eventos activos
    public List<Evento> obtenerEventosActivos() {
        return eventoRepository.findByActivoTrueOrderByFechaEventoAsc();
    }
    
    // Obtener eventos próximos (fecha futura)
    public List<Evento> obtenerEventosProximos() {
        return eventoRepository.findEventosProximos(LocalDateTime.now());
    }
    
    // Buscar eventos por texto
    public List<Evento> buscarEventos(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return obtenerEventosActivos();
        }
        return eventoRepository.buscarEventos(busqueda.trim());
    }
    
    // Filtrar eventos por categoría
    public List<Evento> filtrarPorCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return obtenerEventosActivos();
        }
        return eventoRepository.findByActivoTrueAndCategoriaOrderByFechaEventoAsc(categoria);
    }
    
    // Filtrar eventos por tipo de precio
    public List<Evento> filtrarPorPrecio(String tipo) {
        switch (tipo.toLowerCase()) {
            case "gratis":
                return eventoRepository.findEventosGratuitos();
            case "pago":
                return eventoRepository.findEventosDePago();
            default:
                return obtenerEventosActivos();
        }
    }
    
    // Obtener evento por ID
    public Optional<Evento> obtenerEventoPorId(Long id) {
        return eventoRepository.findById(id);
    }
    
    // Guardar evento
    public Evento guardarEvento(Evento evento) {
        Evento eventoGuardado = eventoRepository.save(evento);
        neo4jDirectService.crearOActualizarNodoEvento(eventoGuardado);
        
        return eventoGuardado;

    }
    
    // Obtener todas las categorías disponibles
    public List<String> obtenerCategorias() {
        return eventoRepository.findCategorias();
    }
    
    // Contar total de eventos activos
    public long contarEventosActivos() {
        return eventoRepository.countByActivoTrue();
    }
    
    // Desactivar evento (soft delete)
    public void desactivarEvento(Long id) {
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            evento.setActivo(false);
            eventoRepository.save(evento);
        }
    }

    
    // Inicializar eventos de prueba
    public void inicializarEventosPrueba() {
        if (eventoRepository.count() == 0) {
            System.out.println("Creando eventos de prueba...");
            
            List<Evento> eventosEjemplo = Arrays.asList(
                new Evento(
                    "Concierto de Rock Nacional", 
                    "Una noche inolvidable con las mejores bandas de rock nacional. Ven y disfruta de música en vivo, buena comida y un ambiente increíble.",
                    "Teatro Jorge Eliécer Gaitán - Bogotá", 
                    LocalDateTime.of(2025, 10, 15, 20, 0),
                    "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500",
                    45000.0, 
                    "Música", 
                    500, 
                    "Rock Colombia Productions"
                ),
                new Evento(
                    "Festival de Comida Colombiana", 
                    "Descubre los sabores auténticos de Colombia. Más de 50 restaurantes locales presentando sus mejores platos típicos.",
                    "Parque Simón Bolívar - Bogotá", 
                    LocalDateTime.of(2025, 10, 22, 12, 0),
                    "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=500",
                    0.0, 
                    "Gastronomía", 
                    1000, 
                    "Alcaldía de Bogotá"
                ),
                new Evento(
                    "Conferencia de Tecnología e Innovación", 
                    "Los líderes tecnológicos más importantes del país compartirán sus experiencias y visiones sobre el futuro digital de Colombia.",
                    "Centro de Convenciones CORFERIAS", 
                    LocalDateTime.of(2025, 11, 5, 9, 0),
                    "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=500",
                    80000.0, 
                    "Tecnología", 
                    300, 
                    "TechColombia"
                ),
                new Evento(
                    "Maratón de Bogotá 2025", 
                    "Participa en la carrera más importante de la ciudad. Categorías de 5K, 10K, 21K y 42K. ¡Inscripciones abiertas!",
                    "Recorrido por el centro de Bogotá", 
                    LocalDateTime.of(2025, 12, 8, 6, 0),
                    "https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?w=500",
                    25000.0, 
                    "Deportes", 
                    5000, 
                    "Instituto Distrital de Recreación y Deporte"
                ),
                new Evento(
                    "Feria de Arte y Artesanías", 
                    "Expositores de todo el país presentan sus obras de arte, artesanías y productos únicos. Entrada libre para toda la familia.",
                    "Plaza de Bolívar - Tunja", 
                    LocalDateTime.of(2025, 11, 12, 10, 0),
                    "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=500",
                    0.0, 
                    "Arte", 
                    800, 
                    "Casa de la Cultura de Boyacá"
                ),
                new Evento(
                    "Workshop de Fotografía Digital", 
                    "Aprende técnicas avanzadas de fotografía digital con profesionales reconocidos. Incluye almuerzo y certificado.",
                    "Universidad Pedagógica y Tecnológica de Colombia - UPTC", 
                    LocalDateTime.of(2025, 10, 28, 8, 0),
                    "https://images.unsplash.com/photo-1452587925148-ce544e77e70d?w=500",
                    120000.0, 
                    "Educación", 
                    50, 
                    "Facultad de Artes - UPTC"
                )
            );
            
            eventoRepository.saveAll(eventosEjemplo);
            System.out.println("Eventos de prueba creados exitosamente: " + eventosEjemplo.size() + " eventos");
        } else {
            System.out.println("Ya existen eventos en la base de datos (" + eventoRepository.count() + " eventos)");
        }
    }
}