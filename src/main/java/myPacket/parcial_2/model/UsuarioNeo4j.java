package myPacket.parcial_2.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.Set;

@Node("Usuario")
public class UsuarioNeo4j {
    
    @Id
    private Long id; // Mismo ID que en MariaDB
    
    private String nombre;
    private String email;
    
    @Relationship(type = "AMIGO_DE", direction = Relationship.Direction.OUTGOING)
    private Set<UsuarioNeo4j> amigos;
    
    public UsuarioNeo4j() {
    }
    
    public UsuarioNeo4j(Long id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Set<UsuarioNeo4j> getAmigos() {
        return amigos;
    }
    
    public void setAmigos(Set<UsuarioNeo4j> amigos) {
        this.amigos = amigos;
    }
}