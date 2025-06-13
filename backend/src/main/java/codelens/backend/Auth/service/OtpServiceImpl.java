package codelens.backend.Auth.service;

import codelens.backend.Auth.entity.Otp;
import codelens.backend.Auth.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final long EXPIRATION_DURATION = 1000L * 60 * 10;
    private final int OTP_MIN = 100000;
    private final int OTP_MAX = 900000;

    /**
     * Generates a new OTP for the given user. If an existing OTP is found, it is deleted first.
     *
     * @param userId The ID of the user requesting the OTP.
     * @return The newly generated OTP entity.
     */
    @Override
    public Otp generateOtp(ObjectId userId) {
        Optional<Otp> existingOtp = otpRepository.findByUserId(userId);
        existingOtp.ifPresent(this::deleteOtp);

        String otpValue = String.valueOf(OTP_MIN + new Random().nextInt(OTP_MAX));
        Otp otp = Otp.builder()
                .otp(otpValue)
                .expiryDate(Instant.now().plusMillis(EXPIRATION_DURATION))
                .userId(userId)
                .build();

        return otpRepository.save(otp);
    }


    /**
     * Validates whether the given OTP is still valid.
     *
     * @param otp The OTP entity to validate.
     * @return true if the OTP is valid, false otherwise.
     * @throws Error if the OTP has expired.
     */
    @Override
    public boolean isOtpValid(Otp otp) {
        if (otp == null || otp.getExpiryDate() == null) {
            return false;
        }
        if (otp.getExpiryDate().isBefore(Instant.now())) {
            throw new Error("otp has expired.");
        }

        return true;
    }

    /**
     * Deletes the OTP associated with the given user.
     *
     * @param otp The OTP entity to delete.
     */
    @Override
    public void deleteOtp(Otp otp) {
        if (otp != null) {
            otpRepository.deleteByUserId(otp.getUserId());
        }
    }

    /**
     * Finds an OTP entity by its OTP value.
     *
     * @param otpValue The OTP code to search for.
     * @return An Optional containing the OTP entity if found.
     */
    @Override
    public Optional<Otp> findByOtp(String otpValue) {
        return otpRepository.findByOtp(otpValue);
    }

    /**
     * Resends a new OTP to the user by first deleting any existing OTP.
     *
     * @param userId The ID of the user requesting a new OTP.
     * @return The newly generated OTP entity.
     */
    @Override
    public Otp resendOtp(ObjectId userId) {
        Optional<Otp> existingOtp = otpRepository.findByUserId(userId);
        existingOtp.ifPresent(this::deleteOtp);
        return generateOtp(userId);
    }
}
