package myPacket.parcial_2.repository.mongodb;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import myPacket.parcial_2.model.UsuarioHobbies;

@Repository
public interface UsuarioHobbiesRepository extends MongoRepository<UsuarioHobbies, String> {
    
    Optional<UsuarioHobbies> findByUsuarioId(Long usuarioId);
    
    boolean existsByUsuarioId(Long usuarioId);
}