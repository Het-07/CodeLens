package codelens.backend.Session.repository;

import codelens.backend.Session.entity.Session;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends MongoRepository<Session, ObjectId> {
    List<Session> findByUserIdOrderByCreatedAtDesc(String userId);
}
