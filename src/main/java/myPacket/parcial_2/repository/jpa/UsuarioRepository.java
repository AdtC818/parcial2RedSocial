package myPacket.parcial_2.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import myPacket.parcial_2.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.password = :password")
    Optional<Usuario> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
    List<Usuario> findByNombreContainingIgnoreCaseAndIdNotAndIdNotIn(String nombre, Long usuarioId, List<Long> amigoIds);
}