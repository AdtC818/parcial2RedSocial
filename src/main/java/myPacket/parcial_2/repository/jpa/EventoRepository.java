package myPacket.parcial_2.repository.jpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import myPacket.parcial_2.model.Evento;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    // Buscar eventos activos
    List<Evento> findByActivoTrueOrderByFechaEventoAsc();
    
    // Buscar eventos por categoría
    List<Evento> findByActivoTrueAndCategoriaOrderByFechaEventoAsc(String categoria);
    
    // Buscar eventos próximos (fecha futura)
    @Query("SELECT e FROM Evento e WHERE e.activo = true AND e.fechaEvento > :fechaActual ORDER BY e.fechaEvento ASC")
    List<Evento> findEventosProximos(@Param("fechaActual") LocalDateTime fechaActual);
    
    // Buscar eventos por organizador
    List<Evento> findByActivoTrueAndOrganizadorContainingIgnoreCaseOrderByFechaEventoAsc(String organizador);
    
    // Buscar eventos por título o lugar
    @Query("SELECT e FROM Evento e WHERE e.activo = true AND " +
           "(LOWER(e.titulo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(e.lugar) LIKE LOWER(CONCAT('%', :busqueda, '%'))) " +
           "ORDER BY e.fechaEvento ASC")
    List<Evento> buscarEventos(@Param("busqueda") String busqueda);
    
    // Buscar eventos gratuitos
    @Query("SELECT e FROM Evento e WHERE e.activo = true AND (e.precio = 0 OR e.precio IS NULL) ORDER BY e.fechaEvento ASC")
    List<Evento> findEventosGratuitos();
    
    // Buscar eventos de pago
    @Query("SELECT e FROM Evento e WHERE e.activo = true AND e.precio > 0 ORDER BY e.fechaEvento ASC")
    List<Evento> findEventosDePago();
    
    // Contar eventos activos
    long countByActivoTrue();
    
    // Obtener todas las categorías únicas
    @Query("SELECT DISTINCT e.categoria FROM Evento e WHERE e.activo = true AND e.categoria IS NOT NULL ORDER BY e.categoria")
    List<String> findCategorias();
}