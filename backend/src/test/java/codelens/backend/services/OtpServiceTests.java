package codelens.backend.services;

import codelens.backend.Auth.entity.Otp;
import codelens.backend.Auth.repository.OtpRepository;
import codelens.backend.Auth.service.OtpServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OtpServiceTests {
    @Mock
    private OtpRepository otpRepository; //Mocked Dependency

    @InjectMocks
    private OtpServiceImpl otpServiceImpl; //Service under Test

    private ObjectId userId;

    private static final long EXPIRATION_DURATION = 1000L * 60 * 10;

    /**
     * Sets up test data before each test.
     * Creates a test OTP object.
     */
    private Otp testOtp;
    @BeforeEach
    public void setUp() {
        userId = new ObjectId();
        testOtp = Otp.builder()
                .otp("123456")
                .expiryDate(Instant.now().plusMillis(EXPIRATION_DURATION)) // Valid for 10 min
                .userId(userId)
                .build();
    }

    /**
     * Tests OTP generation.
     * Should delete an existing OTP if found and then generate a new one.
     */
    @Test
    public void GenerateOtp_ShouldDeleteExistingAndCreateNewOtp() {
        when(otpRepository.findByUserId(userId)).thenReturn(Optional.of(testOtp));

        when(otpRepository.save(any(Otp.class))).thenAnswer(invocation -> {
            Otp savedOtp = invocation.getArgument(0);
            savedOtp.setId(new ObjectId());
            return savedOtp;
        });

        Otp createdOtp = otpServiceImpl.generateOtp(userId);

        assertThat(createdOtp).isNotNull();
        assertThat(createdOtp.getId()).isNotNull();
        assertThat(createdOtp.getOtp()).isNotEmpty();
        Mockito.verify(otpRepository, times(1)).deleteByUserId(userId);
        Mockito.verify(otpRepository, times(1)).save(any(Otp.class));
    }

    /**
     * Tests OTP validation for a valid OTP.
     * Should return true if OTP is valid.
     */
    @Test
    public void isOtpValid_ShouldReturnTrue() {
        Boolean isValid =  otpServiceImpl.isOtpValid(testOtp);

        assertThat(isValid).isTrue();
    }

    /**
     * Tests OTP validation for an expired OTP.
     * Should throw an error when OTP is expired.
     */
    @Test
    public void isOtpValid_WhenOtpIsExpired_ShouldThrowError()
    {
        testOtp.setExpiryDate(Instant.now().minusSeconds(1)); //expired Otp

        assertThatThrownBy(()->otpServiceImpl.isOtpValid(testOtp))
                .isInstanceOf(Error.class)
                .hasMessageContaining("otp has expired.");
    }

    /**
     * Tests OTP validation for a null OTP.
     * Should return false when OTP is null.
     */
    @Test
    public void isOtpValid_WhenOtpIsNull_ShouldReturnFalse()
    {
        Boolean isValid = otpServiceImpl.isOtpValid(null);
        assertThat(isValid).isFalse();
    }

    /**
     * Tests deleting an OTP.
     * Ensures that the repository delete method is called.
     */
    @Test
    public void DeleteOtp_WhenOtpExists_ShouldDeleteOtp()
    {
        doNothing().when(otpRepository).deleteByUserId(userId);

        otpServiceImpl.deleteOtp(testOtp);

        Mockito.verify(otpRepository , times(1)).deleteByUserId(userId);
    }

    /**
     * Tests finding an OTP by its value.
     * Should return the correct OTP if found.
     */
    @Test
    public void findByOtp_WhenOtpExists_ShouldReturnOtp()
    {
        when(otpRepository.findByOtp(testOtp.getOtp())).thenReturn(Optional.of(testOtp));

        Optional<Otp> existingOtp = otpServiceImpl.findByOtp(testOtp.getOtp());

        assertThat(existingOtp.isPresent()).isTrue();
        assertThat(existingOtp.get().getOtp()).isEqualTo(testOtp.getOtp());

        Mockito.verify(otpRepository , times(1)).findByOtp(testOtp.getOtp());
    }

    /**
     * Tests resending an OTP.
     * Should delete an existing OTP and generate a new one.
     */
    @Test
    public void resendOtp_GenerateNewOtp_ShouldDeleteExisting()
    {
        when(otpRepository.findByUserId(userId)).thenReturn(Optional.of(testOtp));

        when(otpRepository.save(any(Otp.class))).thenAnswer(invocation ->
        {
            Otp newOtp = invocation.getArgument(0);
            newOtp.setId(new ObjectId());
            return newOtp;
        });

        Otp resendOtp = otpServiceImpl.resendOtp(userId);

        assertThat(resendOtp).isNotNull();
        assertThat(resendOtp.getId()).isNotNull();
        assertThat(resendOtp.getOtp()).isNotEmpty();

        Mockito.verify(otpRepository ,times(2)).findByUserId(userId);
        Mockito.verify(otpRepository , times(2)).deleteByUserId(userId);
        Mockito.verify(otpRepository , times(1)).save(any(Otp.class));
    }

}
