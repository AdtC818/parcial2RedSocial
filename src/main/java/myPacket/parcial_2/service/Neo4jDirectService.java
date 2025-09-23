package myPacket.parcial_2.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import myPacket.parcial_2.model.UsuarioNeo4j;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Service
public class Neo4jDirectService {

    @Autowired(required = false)
    private Driver driver;

    public boolean verificarConexion() {
        try {
            if (driver == null) {
                System.out.println("Driver de Neo4j es null");
                return false;
            }
            
            try (Session session = driver.session()) {
                Result result = session.run("RETURN 1 as test");
                boolean conectado = result.hasNext();
                System.out.println("Neo4j conectado exitosamente");
                return conectado;
            }
        } catch (Exception e) {
            System.out.println("Error conectando a Neo4j: " + e.getMessage());
            return false;
        }
    }

    public void inicializarDatos() {
        try {
            if (!verificarConexion()) {
                return;
            }

            try (Session session = driver.session()) {
                // Verificar si ya hay datos
                Result countResult = session.run("MATCH (n:Usuario) RETURN count(n) as cantidad");
                long cantidad = countResult.single().get("cantidad").asLong();
                
                if (cantidad == 0) {
                    System.out.println("Creando usuarios en Neo4j...");
                    
                    // Crear usuarios
                    session.run("CREATE (u:Usuario {id: 1, nombre: 'Pedro', email: 'pedro@email.com'})");
                    session.run("CREATE (u:Usuario {id: 2, nombre: 'Nicolas', email: 'nicolas@email.com'})");
                    session.run("CREATE (u:Usuario {id: 3, nombre: 'Sergio', email: 'sergio@email.com'})");
                    session.run("CREATE (u:Usuario {id: 4, nombre: 'Santiago', email: 'santiago@email.com'})");
                    
                    System.out.println("Creando relaciones de amistad...");
                    
                    // Crear relaciones de amistad
                    session.run(
                        "MATCH (pedro:Usuario {id: 1}), (nicolas:Usuario {id: 2}) " +
                        "CREATE (pedro)-[:AMIGO_DE]->(nicolas), (nicolas)-[:AMIGO_DE]->(pedro)"
                    );
                    
                    session.run(
                        "MATCH (pedro:Usuario {id: 1}), (sergio:Usuario {id: 3}) " +
                        "CREATE (pedro)-[:AMIGO_DE]->(sergio), (sergio)-[:AMIGO_DE]->(pedro)"
                    );
                    
                    session.run(
                        "MATCH (nicolas:Usuario {id: 2}), (santiago:Usuario {id: 4}) " +
                        "CREATE (nicolas)-[:AMIGO_DE]->(santiago), (santiago)-[:AMIGO_DE]->(nicolas)"
                    );
                    
                    session.run(
                        "MATCH (sergio:Usuario {id: 3}), (santiago:Usuario {id: 4}) " +
                        "CREATE (sergio)-[:AMIGO_DE]->(santiago), (santiago)-[:AMIGO_DE]->(sergio)"
                    );
                    
                    System.out.println("Datos de Neo4j creados exitosamente");
                } else {
                    System.out.println("Neo4j ya tiene datos (" + cantidad + " usuarios)");
                }
            }
        } catch (Exception e) {
            System.out.println("Error inicializando datos en Neo4j: " + e.getMessage());
        }
    }

    public List<UsuarioNeo4j> obtenerAmigos(Long usuarioId) {
        List<UsuarioNeo4j> amigos = new ArrayList<>();
        
        try {
            if (!verificarConexion()) {
                return amigos;
            }

            try (Session session = driver.session()) {
                Result result = session.run(
                    "MATCH (u:Usuario {id: $usuarioId})-[:AMIGO_DE]->(amigo:Usuario) RETURN amigo",
                    Map.of("usuarioId", usuarioId)
                );
                
                while (result.hasNext()) {
                    var record = result.next();
                    var amigoNode = record.get("amigo").asNode();
                    
                    UsuarioNeo4j amigo = new UsuarioNeo4j(
                        amigoNode.get("id").asLong(),
                        amigoNode.get("nombre").asString(),
                        amigoNode.get("email").asString()
                    );
                    amigos.add(amigo);
                }
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo amigos: " + e.getMessage());
        }
        
        return amigos;
    }

    public List<UsuarioNeo4j> obtenerTodosLosUsuarios() {
        List<UsuarioNeo4j> usuarios = new ArrayList<>();
        
        try {
            if (!verificarConexion()) {
                return usuarios;
            }

            try (Session session = driver.session()) {
                Result result = session.run("MATCH (u:Usuario) RETURN u");
                
                while (result.hasNext()) {
                    var record = result.next();
                    var usuarioNode = record.get("u").asNode();
                    
                    UsuarioNeo4j usuario = new UsuarioNeo4j(
                        usuarioNode.get("id").asLong(),
                        usuarioNode.get("nombre").asString(),
                        usuarioNode.get("email").asString()
                    );
                    usuarios.add(usuario);
                }
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
}