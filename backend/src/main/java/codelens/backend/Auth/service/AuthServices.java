package codelens.backend.Auth.service;
import codelens.backend.Auth.entity.Otp;
import codelens.backend.Auth.requestEntity.*;
import codelens.backend.Auth.responseEntity.*;
import codelens.backend.util.HttpStatusCode;
import jakarta.mail.MessagingException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import codelens.backend.Config.JWTService;
import codelens.backend.User.User;
import codelens.backend.User.UserRepository;
import codelens.backend.util.EmailTemplates;
import lombok.RequiredArgsConstructor;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServices {
	private final UserRepository userRepository;
	@Autowired
	private final PasswordEncoder passwordEncoder;
	@Autowired
	private final JWTService jwtService;
	@Autowired
	private final AuthenticationManager authenticationManager;
	private final JavaMailSender javaMailSender;
	private final OtpService otpService;

	/**
	 * Registers a new user.
	 * @param req The registration request containing user details.
	 * @return AuthenticationResponse containing the generated token and user details.
	 */
	public AuthenticationResponse register(RegisterRequest req) {
		var user = User.builder()
				.first_name(req.getFirst_name())
				.last_name(req.getLast_name())
				.email(req.getEmail())
				.password(passwordEncoder.encode(req.getPassword()))
				.build();

		userRepository.save(user);
		var token = jwtService.generateToken(user);

		return AuthenticationResponse.builder()
				.statusCode(HttpStatusCode.OK)
				.body(Map.of(
						"token", token,
						"message","User Registered Successfully",
						"user_details", Map.of(
								"token", token,
								"message","User Authenticated Successfully",
								"user_details", Map.of(
										"first_name", user.getFirst_name(),
										"last_name", user.getLast_name(),
										"email", user.getEmail()
								)
						)
				))
				.url("/api/auth/register")
				.timestamp(new Date().toString())
				.build();
	}

	/**
	 * Authenticates a user and generates a JWT token.
	 * @param req The authentication request containing email and password.
	 * @return AuthenticationResponse containing the JWT token and user details.
	 */
	public AuthenticationResponse authenticate(AuthenticationRequest req) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						req.getEmail(),
						req.getPassword()
				)
		);
		var user = userRepository.findByEmail(req.getEmail())
				.orElseThrow();

		var token = jwtService.generateToken(user);

		return AuthenticationResponse.builder()
				.statusCode(HttpStatusCode.OK)
				.body(Map.of(
						"token", token,
						"message","User Authenticated Successfully",
						"user_details", Map.of(
								"first_name", user.getFirst_name(),
								"last_name", user.getLast_name(),
								"email", user.getEmail(),
								"userId", user.getId().toString()
						)
				))
				.url("/api/auth/authenticate")
				.timestamp(new Date().toString())
				.build();
	}

	/**
	 * Handles the forgot password request by generating and sending an OTP.
	 * @param forgotPasswordRequest The request containing the user's email.
	 * @return AuthenticationResponse indicating OTP has been sent.
	 */
	public AuthenticationResponse handleForgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws MessagingException, UnsupportedEncodingException {
		Optional<User> user = userRepository.findByEmail(forgotPasswordRequest.getEmail());
		ObjectId userId = user.map(User::getId).orElseThrow(() -> new Error("User not found"));

		Otp otp = otpService.generateOtp(userId);
		sendForgotPasswordMail(forgotPasswordRequest.getEmail(), otp.getOtp());

		return AuthenticationResponse.builder()
				.statusCode(HttpStatusCode.OK)
				.body(Map.of(
						"message", "Otp send successfully"
				))
				.url("/api/v1/auth/forgot-password")
				.timestamp(new Date().toString())
				.build();
	}

	/**
	 * Sends a forgot password email with an OTP.
	 * @param email The recipient's email.
	 * @param otp The OTP to be sent.
	 */
	public void sendForgotPasswordMail(String email, String otp) throws MessagingException, UnsupportedEncodingException {
		String subject = "Forgot Password Request";
		String content = String.format(EmailTemplates.getOtpEmailTemplate(), otp);
		sendMail(subject, content, email);
	}

	/**
	 * Sends an email.
	 * @param subject Email subject.
	 * @param content Email content.
	 * @param email Recipient's email.
	 */
	private void sendMail(String subject,String content, String email) throws MessagingException, UnsupportedEncodingException {

			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom("csci5308.group01@gmail.com", "Code Lens");
			helper.setTo(email);
			helper.setSubject(subject);
			helper.setText(content, true);

			javaMailSender.send(message);  // Ensure this line execute
	}

	// Helper method to get OTP by code
	private Otp getOtpByCode(String otpCode) {
		return otpService.findByOtp(otpCode)
				.orElseThrow(() -> new Error("Invalid OTP. Please try again."));
	}

	/**
	 * Verifies an OTP and returns a JWT token upon success.
	 * @param otpVerificationRequest The request containing the OTP.
	 * @return AuthenticationResponse containing the JWT token.
	 */
	public AuthenticationResponse verifyOtp(OtpVerificationRequest otpVerificationRequest) {
		Otp otp = getOtpByCode(otpVerificationRequest.getOtp());

		if (!otpService.isOtpValid(otp)) {
			throw new Error("OTP has expired. Please request a new one.");
		}

		User user = getUserById(otp.getUserId());

		String jwtToken = jwtService.generateToken(user);

		otpService.deleteOtp(otp);

		return AuthenticationResponse.builder()
				.statusCode(HttpStatusCode.OK)
				.body(Map.of(
						"token", jwtToken
				))
				.url("/api/v1/auth/verifyOtp")
				.timestamp(new Date().toString())
				.build();
	}

	// Helper method to get user by ID
	private User getUserById(ObjectId userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new Error("User not found with ID: " + userId));
	}

	/**
	 * Resends an OTP to the user's email.
	 *
	 * @param resendOtpRequest The request containing the user's email.
	 * @return AuthenticationResponse indicating success.
	 * @throws MessagingException If an error occurs while sending the email.
	 * @throws UnsupportedEncodingException If encoding is not supported.
	 */
	public AuthenticationResponse resendOtp(ResendOtpRequest resendOtpRequest) throws MessagingException, UnsupportedEncodingException {
		Optional<User> user = userRepository.findByEmail(resendOtpRequest.getEmail());

		if (user.isEmpty()) {
			throw new Error("User not found with ID: " + resendOtpRequest.getEmail());
		}

		Otp otp = otpService.resendOtp(user.get().getId());
		sendForgotPasswordMail(user.get().getEmail(), otp.getOtp());

		return AuthenticationResponse.builder()
				.statusCode(HttpStatusCode.OK)
				.body(Map.of(
						"message", "OTP send successfully"
				))
				.url("/api/v1/auth/resendOtp")
				.timestamp(new Date().toString())
				.build();
	}

	/**
	 * Resets the user's password after validating the JWT token.
	 *
	 * @param resetPasswordRequest The request containing the new password and token.
	 * @return AuthenticationResponse indicating success.
	 */
	public AuthenticationResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
		User user = getUserByEmail(resetPasswordRequest.getEmail());

		if(!jwtService.isTokenValid(resetPasswordRequest.getToken(), user)) {
			throw new IllegalArgumentException("Invalid token. Please try again.");
		}

		if(!isValidPassword(resetPasswordRequest.getPassword())) {
			throw new Error("Invalid password. Please try again.");
		}

		user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
		userRepository.save(user);

		return AuthenticationResponse.builder()
				.statusCode(HttpStatusCode.OK)
				.body(Map.of(
						"message", "Password reset successfully"
				))
				.url("/api/v1/auth/resetPassword")
				.timestamp(new Date().toString())
				.build();
	}


	private User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new Error("User not found with email: " + email));
	}

	private boolean isValidPassword(String password) {
		// Regular expression to check if password contains at least one number, one special character, and one uppercase letter
		String regex = "^(?=.*[0-9])(?=.*[@#$%^&+=])(?=.*[A-Z]).*$";
		return password.matches(regex);
	}

}
