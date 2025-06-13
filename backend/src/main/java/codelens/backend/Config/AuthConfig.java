package codelens.backend.Config;

import codelens.backend.User.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for authentication-related beans.
 * Configures user authentication and authorization settings using custom authentication providers and entry points.
 */
@Component
@Configuration
@RequiredArgsConstructor
public class AuthConfig {

	@Autowired
	private final UserRepository userRepository;

	/**
	 * Creates and returns a UserDetailsService bean that loads user details
	 * from the UserRepository using email as the unique identifier.
	 *
	 * @return A UserDetailsService instance.
	 */
	@Bean
	public UserDetailsService userDetailsService(){
		return username -> userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
	}

	/**
	 * Creates and returns an AuthenticationProvider bean.
	 * Configures authentication using DaoAuthenticationProvider with custom UserDetailsService
	 * and BCrypt password encoding.
	 *
	 * @return An AuthenticationProvider instance.
	 */
	@Bean
	public AuthenticationProvider authenticationProvider(){
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passEncoder());
		return authProvider;
	}

	/**
	 * Creates and returns an AuthenticationManager bean.
	 * Configures AuthenticationManager using Spring's AuthenticationConfiguration.
	 *
	 * @param config AuthenticationConfiguration instance.
	 * @return An AuthenticationManager instance.
	 * @throws Exception if there is an issue during AuthenticationManager creation.
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * Creates and returns an AuthenticationEntryPoint bean.
	 * Handles authentication errors by providing a custom response (currently empty).
	 *
	 * @return An AuthenticationEntryPoint instance.
	 */
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint(){
		return new AuthenticationEntryPoint() {
			@Override
			public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);

				PrintWriter writer = response.getWriter();
				Map<String, Object> errorDetails = new HashMap<>();
				Map<String, Object> body = new HashMap<>();

				body.put("message", "Unauthorized: " + authException.getMessage());

				errorDetails.put("statusCode", HttpStatus.UNAUTHORIZED.value());
				errorDetails.put("body", body);
				errorDetails.put("url", request.getRequestURI());
				errorDetails.put("timestamp", new Date().toString());

				writer.print(errorDetails);
			}
		};
	}

	/**
	 * Creates and returns an AccessDeniedHandler bean.
	 * Handles access-denied scenarios and provides a custom response (currently empty).
	 *
	 * @return An AccessDeniedHandler instance.
	 */
	@Bean
	public AccessDeniedHandler accessDeniedHandler(){
		return new AccessDeniedHandler() {
			@Override
			public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
				response.setStatus(HttpStatus.FORBIDDEN.value());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);

				PrintWriter writer = response.getWriter();
				Map<String, Object> errorDetails = new HashMap<>();
				Map<String, Object> body = new HashMap<>();

				body.put("message", "Forbidden: " + accessDeniedException.getMessage());

				errorDetails.put("statusCode", HttpStatus.FORBIDDEN.value());
				errorDetails.put("body", body);
				errorDetails.put("url", request.getRequestURI());
				errorDetails.put("timestamp", new Date().toString());

				writer.print(errorDetails);
			}
		};
	}

	/**
	 * Creates and returns a PasswordEncoder bean.
	 * Configures BCryptPasswordEncoder to hash passwords securely.
	 *
	 * @return A PasswordEncoder instance.
	 */
	@Bean
	public PasswordEncoder passEncoder() {
		return new BCryptPasswordEncoder();
	}
}
