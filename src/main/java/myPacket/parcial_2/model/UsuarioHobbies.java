package myPacket.parcial_2.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "usuario_hobbies")
public class UsuarioHobbies {
    
    @Id
    private String id;
    
    private Long usuarioId; // Referencia al usuario en MariaDB
    private List<String> hobbies;
    
    public UsuarioHobbies() {
    }
    
    public UsuarioHobbies(Long usuarioId, List<String> hobbies) {
        this.usuarioId = usuarioId;
        this.hobbies = hobbies;
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public List<String> getHobbies() {
        return hobbies;
    }
    
    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }
}