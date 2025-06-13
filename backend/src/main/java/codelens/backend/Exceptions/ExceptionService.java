package codelens.backend.Exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExceptionService {

	/**
	 * Creates an error response containing details about the exception.
	 *
	 * @param ex The exception that occurred.
	 * @param request The HttpServletRequest, which provides details about the incoming request.
	 * @param status The HTTP status code associated with the error.
	 * @param errorMessage A custom error message to describe the error.
	 * @return A ResponseEntity containing the error details, formatted as a response body.
	 */
	public ResponseEntity<ExceptionResponse> errorCreation (Throwable ex, HttpServletRequest request, HttpStatus status, String errorMessage){
		return ResponseEntity.status(status).body(
				ExceptionResponse.builder()
						.statusCode(status.value())
						.body(Map.of(
								"message", errorMessage,
								"method", request.getMethod(),
								"query_params", request.getParameterMap(),
								"exception_type",ex.getClass().getSimpleName()
						))
						.url(request.getRequestURI())
						.timestamp(new Date().toString())
						.build()
		);
	}
}
