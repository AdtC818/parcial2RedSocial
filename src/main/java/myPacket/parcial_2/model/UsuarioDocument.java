package myPacket.parcial_2.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document(collection = "usuarios")
public class UsuarioDocument {
    @Id
    private String id;
    private String nombre;
    private String email;

    // Constructores
    public UsuarioDocument() {}
    
    public UsuarioDocument(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

}
