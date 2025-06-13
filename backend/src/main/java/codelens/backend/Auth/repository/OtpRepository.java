package codelens.backend.Auth.repository;

import codelens.backend.Auth.entity.Otp;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends MongoRepository<Otp, ObjectId> {
    Optional<Otp> findByOtp(String otp);
    Optional<Otp> findByUserId(ObjectId userId);  // Added this to find OTP by userId
    void deleteByUserId(ObjectId userId);
}
