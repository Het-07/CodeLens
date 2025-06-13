package codelens.backend.DocxFile.repository;

import codelens.backend.DocxFile.entity.Docx;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocxRepository extends MongoRepository<Docx , String> {
    Docx findByMessageId(String messageId);
}
