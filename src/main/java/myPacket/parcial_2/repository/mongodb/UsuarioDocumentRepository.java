package myPacket.parcial_2.repository.mongodb;
import org.springframework.data.mongodb.repository.MongoRepository;

import myPacket.parcial_2.model.UsuarioDocument;

public interface UsuarioDocumentRepository extends MongoRepository<UsuarioDocument, String> {
    
}
