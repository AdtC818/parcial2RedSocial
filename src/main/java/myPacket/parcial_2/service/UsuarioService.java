package myPacket.parcial_2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import myPacket.parcial_2.model.Usuario;
import myPacket.parcial_2.repository.jpa.UsuarioRepository;
import myPacket.parcial_2.repository.mongodb.UsuarioHobbiesRepository;
import myPacket.parcial_2.model.UsuarioHobbies;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import myPacket.parcial_2.repository.neo4j.UsuarioNeo4jRepository;
import myPacket.parcial_2.model.UsuarioNeo4j;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioHobbiesRepository usuarioHobbiesRepository;
    private final UsuarioNeo4jRepository usuarioNeo4jRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
            UsuarioHobbiesRepository usuarioHobbiesRepository,
            UsuarioNeo4jRepository usuarioNeo4jRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioHobbiesRepository = usuarioHobbiesRepository;
        this.usuarioNeo4jRepository = usuarioNeo4jRepository;
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

    // Métodos para Neo4j - Relaciones de amigos
    public List<UsuarioNeo4j> obtenerAmigos(Long usuarioId) {
    return neo4jDirectService.obtenerAmigos(usuarioId);
}

    public List<UsuarioNeo4j> obtenerAmigosDeAmigos(Long usuarioId) {
        try {
            return usuarioNeo4jRepository.encontrarAmigosDeAmigos(usuarioId);
        } catch (Exception e) {
            System.out.println("Error obteniendo amigos de amigos: " + e.getMessage());
            return Arrays.asList(); // Lista vacía si falla
        }
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

    // En lugar de usar usuarioNeo4jRepository.count()
    // Usa este método alternativo que no requiere transacciones complejas:

    @Autowired
private Neo4jDirectService neo4jDirectService;  // Agregar esta línea al principio de la clase

public void inicializarUsuariosYAmistades() {
    System.out.println("Intentando conectar a Neo4j...");
    neo4jDirectService.inicializarDatos();
}
}