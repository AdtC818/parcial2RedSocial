package myPacket.parcial_2.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import myPacket.parcial_2.model.Usuario;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar usuario por email
    Optional<Usuario> findByEmail(String email);
    
    // Verificar si existe un usuario con ese email
    boolean existsByEmail(String email);
    
    // Buscar usuario por email y password (para login)
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.password = :password")
    Optional<Usuario> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}