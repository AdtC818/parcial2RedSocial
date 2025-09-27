package myPacket.parcial_2.model;

public class UsuarioDTO {
    private String id;      
    private String nombre;
    private String email;
    private String origen;  // "SQL" o "MONGO"

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
}
