package codelens.backend.services;

import codelens.backend.Auth.entity.Otp;
import codelens.backend.Auth.requestEntity.*;
import codelens.backend.Auth.responseEntity.*;
import codelens.backend.Auth.service.AuthServices;
import codelens.backend.Auth.service.OtpService;
import codelens.backend.Config.JWTService;
import codelens.backend.User.User;
import codelens.backend.User.UserRepository;
import codelens.backend.util.HttpStatusCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension .class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JWTService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private MimeMessage mimeMessage;
    @Mock
    private OtpService otpService;

    @InjectMocks
    AuthServices authServices;


    private User testUser;
    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResendOtpRequest resendOtpRequest;
    private Otp otp;
    private ResetPasswordRequest resetPasswordRequest;
    private ObjectId userId;

    @BeforeEach
    void setUp() {
        userId = new ObjectId();
        testUser = User.builder()
                .id(userId)
                .first_name("Dhruva")
                .last_name("Patil")
                .email("dp@gmail.com")
                .password("Password@12345")
                .build();

        registerRequest = new RegisterRequest("Dhruva", "Patil", "dp@gmail.com", "Password@12345");
        authenticationRequest = new AuthenticationRequest("dp@gmail.com", "Password@12345");
        forgotPasswordRequest = new ForgotPasswordRequest("dp@gmail.com");
        resetPasswordRequest = new ResetPasswordRequest("dp@gmail.com", "Password@12345", "validToken");
        resendOtpRequest = new ResendOtpRequest("dp@gmail.com");
        otp = Otp.builder()
                .otp("123456")
                .userId(userId)
                .build();

    }

    /**
     * Test case for user registration.
     * Should register a new user and return a response with a token.
     */
    @Test
    public void register_ShouldRegisterUserAndReturnToken()
    {
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        AuthenticationResponse response = authServices.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.OK);
        assertThat(response.getBody()).isNotNull();

        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    /**
     * Test case for verifying OTP.
     * Should return a JWT token if OTP is valid.
     */
    @Test
    void verifyOtp_ShouldReturnToken_WhenOtpIsValid() {

        when(otpService.findByOtp(otp.getOtp())).thenReturn(Optional.of(otp));
        when(otpService.isOtpValid(otp)).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));


        AuthenticationResponse response = authServices.verifyOtp(new OtpVerificationRequest(otp.getOtp()));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.OK);
        assertThat(response.getBody()).isNotNull();
        Mockito.verify(otpService).deleteOtp(otp);
    }

    @Test
    void authenticate_ShouldReturnToken()  {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new UsernamePasswordAuthenticationToken("Dhruva", "12345678", Collections.emptyList()));
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser)).thenReturn("TestJwtToken");

        AuthenticationResponse response = authServices.authenticate(authenticationRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.OK);
        assertThat(response.getBody()).isNotNull();

        Mockito.verify(authenticationManager , times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        Mockito.verify(userRepository , times(1)).findByEmail(registerRequest.getEmail());
        Mockito.verify(jwtService , times(1)).generateToken(testUser);

    }

    @Test
    void handleForgotPasswordRequest_ShouldReturnOtp() throws MessagingException, UnsupportedEncodingException {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(otpService.generateOtp(testUser.getId())).thenReturn(otp);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        AuthenticationResponse response = authServices.handleForgotPassword(forgotPasswordRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.OK);
        assertThat(response.getBody()).isNotNull();

        Mockito.verify(otpService , times(1)).generateOtp(testUser.getId());
        Mockito.verify(userRepository , times(1)).findByEmail(registerRequest.getEmail());

    }

    @Test
    void resendOtp_ShouldResendOtp() throws MessagingException, UnsupportedEncodingException {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(otpService.resendOtp(testUser.getId())).thenReturn(otp);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        AuthenticationResponse response = authServices.resendOtp(resendOtpRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.OK);
        assertThat(response.getBody()).isNotNull();

        Mockito.verify(otpService , times(1)).resendOtp(testUser.getId());
        Mockito.verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());

    }

    @Test
    void test_NoUser_ShouldThrowException(){
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(Error.class , ()->authServices.resendOtp(resendOtpRequest));
    }


    @Test
    void resetPasswordResponse_ShouldResetPassword(){
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("newpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.isTokenValid(anyString(), eq(testUser))).thenReturn(true);

        AuthenticationResponse response = authServices.resetPassword(resetPasswordRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void resetPasswordResponse_InvalidPassword_ShouldThrowException(){
        resetPasswordRequest = new ResetPasswordRequest("dp@gmail.com", "password", "validToken");
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(testUser));

        when(jwtService.isTokenValid(anyString(), eq(testUser))).thenReturn(true);

        assertThrows(Error.class , ()->authServices.resetPassword(resetPasswordRequest));
    }

}


