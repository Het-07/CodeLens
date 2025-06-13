package codelens.backend.Exceptions;

import com.mongodb.MongoException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.UnsupportedEncodingException;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerServerError extends ResponseEntityExceptionHandler {

	private final ExceptionService exceptionService;
	private final HttpStatus serverStatus = HttpStatus.INTERNAL_SERVER_ERROR;

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request,serverStatus, "Invalid arguments provided.");
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ExceptionResponse> handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, serverStatus, "Unexpected null reference encountered.");
	}

	@ExceptionHandler(MongoException.class)
	public ResponseEntity<ExceptionResponse> handleMongoException(MongoException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, serverStatus, "Database operation failed.");
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "Database constraint violation.");
	}

	@ExceptionHandler(UnsupportedEncodingException.class)
	public ResponseEntity<ExceptionResponse> handleUnsupportedEncodingException(UnsupportedEncodingException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "Unsupported encoding.");
	}

	@ExceptionHandler(MessagingException.class)
	public ResponseEntity<ExceptionResponse> handleMessagingException(MessagingException ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request, HttpStatus.CONFLICT, "Messaging operation failed.");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex, HttpServletRequest request) {
		return exceptionService.errorCreation(ex, request,serverStatus, ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
	}
}
