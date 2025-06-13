package codelens.backend.Exceptions;

import com.mongodb.MongoWriteException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerClientError {

	private final ExceptionService exceptionService;

	@ExceptionHandler({MongoWriteException.class,DataIntegrityViolationException.class})
	public ResponseEntity<ExceptionResponse> handleDuplicateKeyException(Throwable ex, HttpServletRequest request) {
		if (ex.getCause() != null && ex.getCause().getMessage().contains("11000")) {
			return exceptionService.errorCreation(ex, request, HttpStatus.CONFLICT, "Duplicate entry error");
		}
		return exceptionService.errorCreation(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "Database constraint violation.");
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.UNAUTHORIZED, "Invalid username or password.");
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.UNAUTHORIZED, "Authentication failed.");
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.FORBIDDEN, "Access denied.");
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ExceptionResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason());
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + " " + error.getDefaultMessage())
				.findFirst()
				.orElse("Invalid request data");
		return exceptionService.errorCreation(ex, request, HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ExceptionResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ExceptionResponse> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.BAD_REQUEST, "Missing parameter: " + ex.getParameterName());
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ExceptionResponse> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.NOT_FOUND, "Endpoint not found");
	}

	@ExceptionHandler(IllegalAccessException.class)
	public ResponseEntity<ExceptionResponse> handleExpiredLink(IllegalAccessException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ExceptionResponse> handleBadLink(NoSuchElementException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.NOT_FOUND, ex.getMessage());
	}

}
