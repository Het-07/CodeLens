package codelens.backend.User;

import codelens.backend.User.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository <User, ObjectId>{
	Optional<User> findByEmail(String email);
}
