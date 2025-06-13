package codelens.backend.Config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTUtility extends OncePerRequestFilter {

	private final JWTService jwtService;
	private final UserDetailsService userDetailsService;

	/**
	 * Filters incoming requests to check for a valid JWT token in the Authorization header.
	 * If the token is valid, it sets the authenticated user in the security context.
	 *
	 * @param request     The HTTP request.
	 * @param response    The HTTP response.
	 * @param filterChain The filter chain to pass the request along.
	 * @throws ServletException If a servlet-specific error occurs.
	 * @throws IOException      If an I/O error occurs.
	 */
	@Override
	protected void doFilterInternal(
			@NonNull
			HttpServletRequest request,
			@NonNull
			HttpServletResponse response,
			@NonNull
			FilterChain filterChain
	) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");
		String token = null;
		String email = null;

		if(authHeader != null && authHeader.startsWith("Bearer ")){
			token = authHeader.split(" ")[1];
			email = jwtService.extractEmail(token);
		}

		try{
			if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
				if(jwtService.isTokenValid(token, userDetails)){
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails,
							null,
							userDetails.getAuthorities()
					);
					authToken.setDetails(
							new WebAuthenticationDetailsSource().buildDetails(request)
					);

					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
			filterChain.doFilter(request,response);
		}catch (ExpiredJwtException e) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().write("Token has expired");
		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().write("Invalid token");
		}

	}
}
