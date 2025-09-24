package myPacket.parcial_2.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evento")
public class Evento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "lugar", nullable = false, length = 255)
    private String lugar;
    
    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;
    
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;
    
    @Column(name = "precio")
    private Double precio;
    
    @Column(name = "categoria", length = 100)
    private String categoria;
    
    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;
    
    @Column(name = "organizador", length = 255)
    private String organizador;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Constructores
    public Evento() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.activo = true;
    }
    
    public Evento(String titulo, String descripcion, String lugar, LocalDateTime fechaEvento, 
                  String imagenUrl, Double precio, String categoria, Integer capacidadMaxima, String organizador) {
        this();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fechaEvento = fechaEvento;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
        this.categoria = categoria;
        this.capacidadMaxima = capacidadMaxima;
        this.organizador = organizador;
    }
    
    // Métodos del ciclo de vida
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getLugar() {
        return lugar;
    }
    
    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
    
    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }
    
    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }
    
    public String getImagenUrl() {
        return imagenUrl;
    }
    
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
    
    public Double getPrecio() {
        return precio;
    }
    
    public void setPrecio(Double precio) {
        this.precio = precio;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }
    
    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }
    
    public String getOrganizador() {
        return organizador;
    }
    
    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    // Métodos de utilidad
    public boolean esPago() {
        return precio != null && precio > 0;
    }
    
    public boolean tieneCapacidadLimitada() {
        return capacidadMaxima != null && capacidadMaxima > 0;
    }
    
    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", lugar='" + lugar + '\'' +
                ", fechaEvento=" + fechaEvento +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                '}';
    }
}