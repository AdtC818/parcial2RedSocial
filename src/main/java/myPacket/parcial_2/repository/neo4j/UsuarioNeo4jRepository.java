package myPacket.parcial_2.repository.neo4j;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import myPacket.parcial_2.model.UsuarioNeo4j;

@Repository("usuarioNeo4jRepository")  
public interface UsuarioNeo4jRepository extends Neo4jRepository<UsuarioNeo4j, Long> {
    
    @Query("MATCH (u:Usuario)-[:AMIGO_DE]->(amigo:Usuario) WHERE u.id = $usuarioId RETURN amigo")
    List<UsuarioNeo4j> encontrarAmigos(@Param("usuarioId") Long usuarioId);
    
    @Query("MATCH (u:Usuario)-[:AMIGO_DE*2]->(amigoDeAmigo:Usuario) " +
           "WHERE u.id = $usuarioId AND amigoDeAmigo.id <> u.id " +
           "RETURN DISTINCT amigoDeAmigo")
    List<UsuarioNeo4j> encontrarAmigosDeAmigos(@Param("usuarioId") Long usuarioId);
    
    @Query("MATCH (a:Usuario), (b:Usuario) " +
           "WHERE a.id = $usuarioId1 AND b.id = $usuarioId2 " +
           "CREATE (a)-[:AMIGO_DE]->(b), (b)-[:AMIGO_DE]->(a)")
    void crearAmistad(@Param("usuarioId1") Long usuarioId1, @Param("usuarioId2") Long usuarioId2);
}