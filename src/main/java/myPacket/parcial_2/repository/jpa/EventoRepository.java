package myPacket.parcial_2.repository.jpa;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import myPacket.parcial_2.model.Evento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    List<Evento> findByActivoTrueOrderByFechaEventoAsc();
    
    List<Evento> findByActivoTrueAndCategoriaOrderByFechaEventoAsc(String categoria);
    
    @Query("SELECT e FROM Evento e WHERE e.activo = true AND e.fechaEvento > :fechaActual ORDER BY e.fechaEvento ASC")
    List<Evento> findEventosProximos(@Param("fechaActual") LocalDateTime fechaActual);
    
    List<Evento> findByActivoTrueAndOrganizadorContainingIgnoreCaseOrderByFechaEventoAsc(String organizador);
    
    @Query("SELECT e FROM Evento e WHERE e.activo = true AND " +
           "(LOWER(e.titulo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(e.lugar) LIKE LOWER(CONCAT('%', :busqueda, '%'))) " +
           "ORDER BY e.fechaEvento ASC")
    List<Evento> buscarEventos(@Param("busqueda") String busqueda);
    
    @Query("SELECT e FROM Evento e WHERE e.activo = true AND (e.precio = 0 OR e.precio IS NULL) ORDER BY e.fechaEvento ASC")
    List<Evento> findEventosGratuitos();
    
    @Query("SELECT e FROM Evento e WHERE e.activo = true AND e.precio > 0 ORDER BY e.fechaEvento ASC")
    List<Evento> findEventosDePago();
    
    long countByActivoTrue();
    
    @Query("SELECT DISTINCT e.categoria FROM Evento e WHERE e.activo = true AND e.categoria IS NOT NULL ORDER BY e.categoria")
    List<String> findCategorias();
}