package myPacket.parcial_2.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import myPacket.parcial_2.model.UsuarioHobbies;
import java.util.Optional;

@Repository
public interface UsuarioHobbiesRepository extends MongoRepository<UsuarioHobbies, String> {
    
    // Buscar hobbies por ID de usuario de MariaDB
    Optional<UsuarioHobbies> findByUsuarioId(Long usuarioId);
    
    // Verificar si un usuario ya tiene hobbies registrados
    boolean existsByUsuarioId(Long usuarioId);
}