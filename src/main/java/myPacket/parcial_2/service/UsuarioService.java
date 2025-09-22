package myPacket.parcial_2.service;

import org.springframework.stereotype.Service;
import myPacket.parcial_2.model.Usuario;
import myPacket.parcial_2.repository.jpa.UsuarioRepository;
import myPacket.parcial_2.repository.mongodb.UsuarioHobbiesRepository;
import myPacket.parcial_2.model.UsuarioHobbies;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioHobbiesRepository usuarioHobbiesRepository;
    
    // Constructor actualizado con ambos repositories
    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioHobbiesRepository usuarioHobbiesRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioHobbiesRepository = usuarioHobbiesRepository;
    }
    
    // Métodos existentes para usuarios
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    public Optional<Usuario> verificarLogin(String email, String password) {
        return usuarioRepository.findByEmailAndPassword(email, password);
    }
    
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    public boolean emailYaExiste(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    // NUEVOS métodos para hobbies
    public Optional<UsuarioHobbies> obtenerHobbiesUsuario(Long usuarioId) {
        return usuarioHobbiesRepository.findByUsuarioId(usuarioId);
    }
    
    public UsuarioHobbies guardarHobbiesUsuario(Long usuarioId, List<String> hobbies) {
        UsuarioHobbies usuarioHobbies = usuarioHobbiesRepository.findByUsuarioId(usuarioId)
            .orElse(new UsuarioHobbies());
        
        usuarioHobbies.setUsuarioId(usuarioId);
        usuarioHobbies.setHobbies(hobbies);
        
        return usuarioHobbiesRepository.save(usuarioHobbies);
    }
    
    // Inicializar hobbies de prueba
    public void inicializarHobbiesPrueba() {
        // Hobbies para Pedro (usuarioId = 1)
        if (!usuarioHobbiesRepository.existsByUsuarioId(1L)) {
            guardarHobbiesUsuario(1L, Arrays.asList("futbol", "programacion", "musica"));
        }
        
        // Hobbies para Nicolas (usuarioId = 2) 
        if (!usuarioHobbiesRepository.existsByUsuarioId(2L)) {
            guardarHobbiesUsuario(2L, Arrays.asList("programacion", "videojuegos", "lectura"));
        }
        
        // Hobbies para Sergio (usuarioId = 3)
        if (!usuarioHobbiesRepository.existsByUsuarioId(3L)) {
            guardarHobbiesUsuario(3L, Arrays.asList("futbol", "musica", "cocina"));
        }
        
        // Hobbies para Santiago (usuarioId = 4)
        if (!usuarioHobbiesRepository.existsByUsuarioId(4L)) {
            guardarHobbiesUsuario(4L, Arrays.asList("programacion", "futbol", "arte"));
        }
    }
}