package codelens.backend.ShareableLink.repository;

import codelens.backend.ShareableLink.linkEntity.ShareableLink;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LinkRepository extends MongoRepository<ShareableLink, String> {
    Optional<ShareableLink> findByToken(String id);
}
