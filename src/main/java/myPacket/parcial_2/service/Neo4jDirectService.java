package myPacket.parcial_2.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myPacket.parcial_2.model.Evento;
import myPacket.parcial_2.model.UsuarioNeo4j;

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
                                    "CREATE (pedro)-[:AMIGO_DE]->(nicolas), (nicolas)-[:AMIGO_DE]->(pedro)");

                    session.run(
                            "MATCH (pedro:Usuario {id: 1}), (sergio:Usuario {id: 3}) " +
                                    "CREATE (pedro)-[:AMIGO_DE]->(sergio), (sergio)-[:AMIGO_DE]->(pedro)");

                    session.run(
                            "MATCH (nicolas:Usuario {id: 2}), (santiago:Usuario {id: 4}) " +
                                    "CREATE (nicolas)-[:AMIGO_DE]->(santiago), (santiago)-[:AMIGO_DE]->(nicolas)");

                    session.run(
                            "MATCH (sergio:Usuario {id: 3}), (santiago:Usuario {id: 4}) " +
                                    "CREATE (sergio)-[:AMIGO_DE]->(santiago), (santiago)-[:AMIGO_DE]->(sergio)");

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
                        Map.of("usuarioId", usuarioId));

                while (result.hasNext()) {
                    var record = result.next();
                    var amigoNode = record.get("amigo").asNode();

                    UsuarioNeo4j amigo = new UsuarioNeo4j(
                            amigoNode.get("id").asLong(),
                            amigoNode.get("nombre").asString(),
                            amigoNode.get("email").asString());
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
                            usuarioNode.get("email").asString());
                    usuarios.add(usuario);
                }
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    public void crearOActualizarNodoUsuario(myPacket.parcial_2.model.Usuario usuario) {
        if (driver == null || !verificarConexion()) {
            System.out.println("No se pudo crear el nodo de usuario en Neo4j, no hay conexión.");
            return;
        }

        String cypherQuery = "MERGE (u:Usuario {id: $id}) " +
                "ON CREATE SET u.nombre = $nombre, u.email = $email " +
                "ON MATCH SET u.nombre = $nombre, u.email = $email";

        try (Session session = driver.session()) {
            Map<String, Object> params = Map.of(
                    "id", usuario.getId(),
                    "nombre", usuario.getNombre(),
                    "email", usuario.getEmail());

            // Ejecutamos la consulta
            session.run(cypherQuery, params);

            System.out.println("Nodo de Usuario creado/actualizado en Neo4j con ID: " + usuario.getId());
        } catch (Exception e) {
            System.out.println("Error al crear/actualizar nodo de usuario en Neo4j: " + e.getMessage());
        }
    }

    public List<Long> obtenerIdsDeAmigos(Long usuarioId) {
        List<Long> amigoIds = new ArrayList<>();
        String cypherQuery = "MATCH (u:Usuario {id: $usuarioId})-[:AMIGO_DE]->(amigo:Usuario) RETURN amigo.id as id";

        try (Session session = driver.session()) {
            Result result = session.run(cypherQuery, Map.of("usuarioId", usuarioId));
            while (result.hasNext()) {
                amigoIds.add(result.next().get("id").asLong());
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo IDs de amigos: " + e.getMessage());
        }
        return amigoIds;
    }

    public void crearAmistad(Long idUsuario1, Long idUsuario2) {
        String cypherQuery = "MATCH (u1:Usuario {id: $id1}), (u2:Usuario {id: $id2}) " +
                "MERGE (u1)-[r:AMIGO_DE]->(u2) " +
                "MERGE (u2)-[:AMIGO_DE]->(u1)";

        try (Session session = driver.session()) {
            session.run(cypherQuery, Map.of("id1", idUsuario1, "id2", idUsuario2));
            System.out.println("Amistad creada entre usuario " + idUsuario1 + " y " + idUsuario2);
        } catch (Exception e) {
            System.out.println("Error creando amistad: " + e.getMessage());
        }
    }

    public void crearRelacionAsistencia(Long usuarioId, Long eventoId) {
        if (driver == null || !verificarConexion()) {
            throw new IllegalStateException("No se puede conectar a Neo4j.");
        }

        String cypherQuery = "MATCH (u:Usuario {id: $usuarioId}), (e:Evento {id: $eventoId}) " +
                "MERGE (u)-[r:ASISTE_A]->(e)";

        try (Session session = driver.session()) {
            Map<String, Object> params = Map.of("usuarioId", usuarioId, "eventoId", eventoId);
            session.run(cypherQuery, params);
            System.out.println("Relación de asistencia creada para usuario " + usuarioId + " al evento " + eventoId);
        } catch (Exception e) {
            System.out.println("Error creando relación de asistencia: " + e.getMessage());
            throw e;
        }
    }

    public List<UsuarioNeo4j> obtenerAmigosQueAsistenAEvento(Long usuarioId, Long eventoId) {
        List<UsuarioNeo4j> amigosQueAsisten = new ArrayList<>();
        String cypherQuery = "MATCH (u:Usuario {id: $usuarioId})-[:AMIGO_DE]->(amigo:Usuario), " +
                "      (amigo)-[:ASISTE_A]->(e:Evento {id: $eventoId}) " +
                "RETURN amigo";

        try (Session session = driver.session()) {
            Result result = session.run(cypherQuery, Map.of("usuarioId", usuarioId, "eventoId", eventoId));

            while (result.hasNext()) {
                var record = result.next();
                var amigoNode = record.get("amigo").asNode();

                amigosQueAsisten.add(new UsuarioNeo4j(
                        amigoNode.get("id").asLong(),
                        amigoNode.get("nombre").asString(),
                        amigoNode.get("email").asString()));
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo amigos que asisten al evento: " + e.getMessage());
        }
        return amigosQueAsisten;
    }

    public void crearOActualizarNodoEvento(Evento evento) {
        if (driver == null || !verificarConexion()) {
            System.out.println("No se pudo crear el nodo de evento en Neo4j, no hay conexión.");
            return;
        }

        String cypherQuery = "MERGE (e:Evento {id: $id}) " +
                "SET e.titulo = $titulo, e.categoria = $categoria";

        try (Session session = driver.session()) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", evento.getId());
            params.put("titulo", evento.getTitulo());
            params.put("categoria", evento.getCategoria());

            session.run(cypherQuery, params);
            System.out.println("Nodo de Evento creado/actualizado en Neo4j con ID: " + evento.getId());
        } catch (Exception e) {
            System.out.println("Error al crear/actualizar nodo de evento en Neo4j: " + e.getMessage());
        }
    }

    public List<Long> obtenerIdsDeEventosAsistidos(Long usuarioId) {
        List<Long> eventoIds = new ArrayList<>();
        String cypherQuery = "MATCH (:Usuario {id: $usuarioId})-[:ASISTE_A]->(e:Evento) RETURN e.id as id";

        try (Session session = driver.session()) {
            Result result = session.run(cypherQuery, Map.of("usuarioId", usuarioId));
            while (result.hasNext()) {
                eventoIds.add(result.next().get("id").asLong());
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo IDs de eventos asistidos: " + e.getMessage());
        }
        return eventoIds;
    }

    public void eliminarRelacionAsistencia(Long usuarioId, Long eventoId) {
        if (driver == null || !verificarConexion()) {
            throw new IllegalStateException("No se puede conectar a Neo4j.");
        }

        String cypherQuery = "MATCH (u:Usuario {id: $usuarioId})-[r:ASISTE_A]->(e:Evento {id: $eventoId}) " +
                "DELETE r";

        try (Session session = driver.session()) {
            Map<String, Object> params = Map.of("usuarioId", usuarioId, "eventoId", eventoId);
            session.run(cypherQuery, params);
            System.out.println("Relación de asistencia eliminada para usuario " + usuarioId + " al evento " + eventoId);
        } catch (Exception e) {
            System.out.println("Error eliminando relación de asistencia: " + e.getMessage());
            throw e;
        }
    }
}