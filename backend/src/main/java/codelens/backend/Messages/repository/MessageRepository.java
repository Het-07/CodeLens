package codelens.backend.Messages.repository;

import codelens.backend.Messages.entity.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    List<Message> getMessagesByUserIdAndSessionIdOrderByCreatedAt (String userId, String sessionId);

    Optional<Message> findById(String id);
}
