package codelens.backend.Auth.service;

import codelens.backend.Auth.entity.Otp;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface OtpService {
    Otp generateOtp(ObjectId user);
    boolean isOtpValid(Otp otp);
    void deleteOtp(Otp otp);
    Optional<Otp> findByOtp(String otpValue);
    Otp resendOtp(ObjectId userId);
}