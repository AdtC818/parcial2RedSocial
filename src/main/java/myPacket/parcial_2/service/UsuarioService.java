package myPacket.parcial_2.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import myPacket.parcial_2.model.Usuario;
import myPacket.parcial_2.model.UsuarioDocument;
import myPacket.parcial_2.model.UsuarioHobbies;
import myPacket.parcial_2.model.UsuarioNeo4j;
import myPacket.parcial_2.repository.jpa.UsuarioRepository;
import myPacket.parcial_2.repository.mongodb.UsuarioDocumentRepository;
import myPacket.parcial_2.repository.mongodb.UsuarioHobbiesRepository;
import myPacket.parcial_2.repository.neo4j.UsuarioNeo4jRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioHobbiesRepository usuarioHobbiesRepository;
    private final UsuarioNeo4jRepository usuarioNeo4jRepository;
    private final UsuarioDocumentRepository usuarioDocumentRepository;
    private final Neo4jDirectService neo4jDirectService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            UsuarioHobbiesRepository usuarioHobbiesRepository,
            UsuarioNeo4jRepository usuarioNeo4jRepository,
            UsuarioDocumentRepository usuarioDocumentRepository,
            Neo4jDirectService neo4jDirectService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioHobbiesRepository = usuarioHobbiesRepository;
        this.usuarioNeo4jRepository = usuarioNeo4jRepository;
        this.usuarioDocumentRepository = usuarioDocumentRepository;
        this.neo4jDirectService = neo4jDirectService;
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

    public void inicializarUsuariosYAmistades() {
        System.out.println("Intentando conectar a Neo4j...");
        neo4jDirectService.inicializarDatos();
    }

    // ========================
    // CRUD de UsuarioDocument (MongoDB)
    // ========================

    public List<UsuarioDocument> listarUsuariosDocument() {
        return usuarioDocumentRepository.findAll();
    }

    public Optional<UsuarioDocument> obtenerUsuarioDocumentPorId(String id) {
        return usuarioDocumentRepository.findById(id);
    }

    public UsuarioDocument crearUsuarioDocument(UsuarioDocument usuario) {
        UsuarioDocument saved = usuarioDocumentRepository.save(usuario);

        // Guardar en MySQL
        Usuario usuarioSql = new Usuario();
        usuarioSql.setNombre(usuario.getNombre());
        usuarioSql.setEmail(usuario.getEmail());
        usuarioSql.setPassword("default123"); // O lo que decidas
        usuarioSql.setFotoUrl(null); // o algún valor por defecto
        usuarioRepository.save(usuarioSql);

        return saved;
    }

    public UsuarioDocument actualizarUsuarioDocument(String id, UsuarioDocument usuarioActualizado) {
        return usuarioDocumentRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(usuarioActualizado.getNombre());
                    usuario.setEmail(usuarioActualizado.getEmail());
                    return usuarioDocumentRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("UsuarioDocument no encontrado con id " + id));
    }

    public void eliminarUsuarioDocument(String id) {
        usuarioDocumentRepository.deleteById(id);
    }

   
    public void agregarHobby(Long usuarioId, String hobbyNombre) {
        UsuarioHobbies usuarioHobbies = usuarioHobbiesRepository.findByUsuarioId(usuarioId)
                .orElse(new UsuarioHobbies(usuarioId, new ArrayList<>()));

        boolean existe = usuarioHobbies.getHobbies().stream()
                .anyMatch(h -> h.equalsIgnoreCase(hobbyNombre));

        if (!existe) {
            usuarioHobbies.getHobbies().add(hobbyNombre);
            usuarioHobbiesRepository.save(usuarioHobbies);
        }
    }

    public void eliminarHobby(Long usuarioId, String hobbyNombre) {
        usuarioHobbiesRepository.findByUsuarioId(usuarioId).ifPresent(usuarioHobbies -> {
            boolean removed = usuarioHobbies.getHobbies().removeIf(h -> h.equalsIgnoreCase(hobbyNombre));
            if (removed) {
                usuarioHobbiesRepository.save(usuarioHobbies);
            }
        });
    }

    public List<Usuario> buscarPosiblesAmigos(Long usuarioId, String searchTerm) {
    List<Long> amigoIds = neo4jDirectService.obtenerIdsDeAmigos(usuarioId);
    if (amigoIds.isEmpty()) {
        amigoIds.add(-1L); 
    }
    return usuarioRepository.findByNombreContainingIgnoreCaseAndIdNotAndIdNotIn(searchTerm, usuarioId, amigoIds);
}
}