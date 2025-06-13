package codelens.backend.Config;

import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

/**
 * SecurityConfig class configures Spring Security for the application,
 * defining authentication, authorization, and session management.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JWTUtility jwtAuthFiler;
	private final AuthenticationProvider authenticationProvider;

	private final AuthenticationEntryPoint authenticationEntryPoint;
	private final AccessDeniedHandler accessDeniedHandler;

	/**
	 * Defines the security filter chain, setting up CORS, authentication, session management,
	 * exception handling, and JWT token filtering.
	 *
	 * @param http The HttpSecurity object used to configure security settings.
	 * @return The configured SecurityFilterChain.
	 * @throws Exception If an error occurs during configuration.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		 http
		 		 .cors(cors -> cors.configurationSource(request -> {
					CorsConfiguration configuration = new CorsConfiguration();
					configuration.setAllowedOrigins(Arrays.asList("*"));
					configuration.setAllowedMethods(Arrays.asList("*"));
					configuration.setAllowedHeaders(Arrays.asList("*"));
					return configuration;
				}))
				 .csrf(AbstractHttpConfigurer::disable)
				 .authorizeHttpRequests(
						 req ->
						 req
								 .requestMatchers(WHITELIST)
								 .permitAll()
								 .anyRequest()
								 .authenticated()

				 )
				 .sessionManagement(ses -> ses.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				 .authenticationProvider(authenticationProvider)
				 .exceptionHandling(
						 ex ->
								 ex
										 .accessDeniedHandler(accessDeniedHandler)
										 .authenticationEntryPoint(authenticationEntryPoint)
				 )
				 .addFilterBefore(jwtAuthFiler, UsernamePasswordAuthenticationFilter.class);

		 return http.build();


	}

	/**
	 * Defines a list of endpoints that are accessible without authentication.
	 */
	private static final String[] WHITELIST = {
			"/api/v1/auth/**",
			"/v3/api-docs/**",
			"/api/v1/link/access/**",
			"/api/v1/download",
			"/swagger-ui/**",
			"swagger-ui.html",
	};
}
