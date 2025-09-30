package myPacket.parcial_2.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuario_hobbies")
public class UsuarioHobbies {
    
    @Id
    private String id;
    
    private Long usuarioId; 
    private List<String> hobbies;
    
    public UsuarioHobbies() {
    }
    
    public UsuarioHobbies(Long usuarioId, List<String> hobbies) {
        this.usuarioId = usuarioId;
        this.hobbies = hobbies;
    }
    
  
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