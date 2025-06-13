package codelens.backend.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
	private final int MILLI_SECONDS = 1000;
	private final int MINUTES= 60;
	private final int SECONDS = 60;
	private final int HOURS = 24;

	@Value("${app.secret.key}")
	private String SECRET_KEY;

	/**
	 * Extracts the email (subject) from the JWT token.
	 *
	 * @param token The JWT token.
	 * @return The email (subject) from the token.
	 */
	public String extractEmail(String token){
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extracts the expiration date from the JWT token.
	 *
	 * @param token The JWT token.
	 * @return The expiration date of the token.
	 */
	public Date extractExpiration(String token){
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Extracts a specific claim from the JWT token using a claims resolver function.
	 *
	 * @param token          The JWT token.
	 * @param claimsResolver Function to resolve the claim.
	 * @param <T>            The type of the claim.
	 * @return The extracted claim.
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Extracts all claims from the JWT token.
	 *
	 * @param token The JWT token.
	 * @return The claims extracted from the token.
	 */
	private Claims extractAllClaims(String token){
		return Jwts
				.parser()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	/**
	 * Generates a JWT token for the given user without extra claims.
	 *
	 * @param userDetails The user details.
	 * @return The generated JWT token.
	 */
	public String generateToken(
			UserDetails userDetails
	){
		return generateToken(new HashMap<>(), userDetails);
	}

	/**
	 * Generates a JWT token with additional claims for the given user.
	 *
	 * @param extraClaims Additional claims to include in the token.
	 * @param userDetails The user details.
	 * @return The generated JWT token.
	 */
	public String generateToken(
			Map<String, Object> extraClaims,
			UserDetails userDetails
	){
		return Jwts
				.builder()
				.claims(extraClaims)
				.subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + MILLI_SECONDS * MINUTES * HOURS * SECONDS))
				.signWith(getSignInKey())
				.compact();
	}

	/**
	 * Checks if a given JWT token is valid for the provided user.
	 *
	 * @param token       The JWT token.
	 * @param userDetails The user details.
	 * @return true if the token is valid, false otherwise.
	 */
	public boolean isTokenValid(String token, UserDetails userDetails){
		final String email = extractEmail(token);
		return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	/**
	 * Checks if the JWT token has expired.
	 *
	 * @param token The JWT token.
	 * @return true if the token has expired, false otherwise.
	 */

	private boolean isTokenExpired(String token) {
		try {
			return extractExpiration(token).before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	/**
	 * Retrieves the signing key used for generating JWTs.
	 *
	 * @return The secret key as a Key object.
	 */
	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
