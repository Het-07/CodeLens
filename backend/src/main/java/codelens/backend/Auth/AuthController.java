package codelens.backend.Auth;

import codelens.backend.Auth.requestEntity.*;
import codelens.backend.Auth.responseEntity.*;
import codelens.backend.Auth.service.AuthServices;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthServices authService;

	@Value("${domain}")
	private String DOMAIN;

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest req, HttpServletResponse response) {
		AuthenticationResponse resp = authService.register(req);
		Map<String, Object> body = (Map<String, Object>) resp.getBody();
		String token = (String) body.get("token");
		addTokenToCookie(token, response);
		return ResponseEntity.ok(resp);

	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest req, HttpServletResponse response) {
		AuthenticationResponse resp = authService.authenticate(req);
		Map<String, Object> body = (Map<String, Object>) resp.getBody();
		String token = (String) body.get("token");
		addTokenToCookie(token, response);
		return ResponseEntity.ok(resp);
	}

	@PostMapping("/forgotPassword")
	public ResponseEntity<AuthenticationResponse> forgetPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) throws MessagingException, UnsupportedEncodingException {
		return ResponseEntity.ok(authService.handleForgotPassword(forgotPasswordRequest));
	}

	@PostMapping("/verifyOtp")
	public ResponseEntity<AuthenticationResponse> verifyOtp(@RequestBody OtpVerificationRequest otpVerificationRequest) {
		return ResponseEntity.ok(authService.verifyOtp(otpVerificationRequest));
	}

	@PostMapping("/resendOtp")
	public ResponseEntity<AuthenticationResponse> resendOtp(@RequestBody ResendOtpRequest resendOtpRequest) throws MessagingException, UnsupportedEncodingException {
		return ResponseEntity.ok(authService.resendOtp(resendOtpRequest));
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<AuthenticationResponse> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
		return ResponseEntity.ok(authService.resetPassword(resetPasswordRequest));
	}

	private void addTokenToCookie(String token, HttpServletResponse response) {
		Cookie cookie = new Cookie("jwtToken", token);
		cookie.setHttpOnly(true);  // Prevent client-side JavaScript access
		cookie.setPath("/"); // Make cookie accessible across the entire application
		cookie.setSecure(true);
		cookie.setDomain(DOMAIN);
		response.addCookie(cookie);
	}
}
