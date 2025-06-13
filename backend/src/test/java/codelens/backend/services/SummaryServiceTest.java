package codelens.backend.services;


import codelens.backend.Messages.service.MessageService;
import codelens.backend.Session.entity.Session;
import codelens.backend.Session.repository.SessionRepository;
import codelens.backend.Summary.requestEntity.GeneratePromptRequest;
import codelens.backend.Summary.responseEntity.GeneratePromptResponse;
import codelens.backend.Summary.responseEntity.SummaryResponse;
import codelens.backend.Summary.service.SummaryServiceImpl;
import codelens.backend.User.User;
import codelens.backend.User.UserRepository;
import codelens.backend.util.HttpStatusCode;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private MessageService messageService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private SessionRepository sessionRepository;

	@InjectMocks
	private SummaryServiceImpl summaryService;

	private final String userId = "64a7e5d2e3b38d1c9cf2a43a";
	private final String sessionId = "64a7e5d2e3b38d1c9cf2a43b";
	private final String userPrompt = "Test prompt";
	private final String generatedResponse = "Generated response from LLM";
	private static final int SHORT_PREDICTS = 30;
	private static final int LONG_PREDICTS = 50;
	private static final int CHARACTER_LENGTH = 501;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(summaryService, "llama", "http://localhost:8080/ollama");
	}

	@Test
	void sendPromptShort_Success() throws IOException {
		GeneratePromptResponse promptResponse = GeneratePromptResponse.builder()
				.response(generatedResponse)
				.build();


		ResponseEntity<GeneratePromptResponse> responseEntity = ResponseEntity.ok(promptResponse);

		when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.POST),
				any(HttpEntity.class),
				eq(GeneratePromptResponse.class)
		)).thenReturn(responseEntity);

		SummaryResponse result = summaryService.sendPromptShort(userPrompt, userId, sessionId);

		assertNotNull(result);
		assertEquals(HttpStatusCode.OK, result.getStatusCode());

		assertInstanceOf(Map.class, result.getBody());

		Map<String, String> bodyMap = (Map<String, String>) result.getBody();
		assertEquals(generatedResponse, bodyMap.get("message"));

		assertEquals("/ollama/generate/short", result.getUrl());

		ArgumentCaptor<HttpEntity<GeneratePromptRequest>> requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), requestCaptor.capture(), eq(GeneratePromptResponse.class));

		GeneratePromptRequest capturedRequest = requestCaptor.getValue().getBody();
		assert capturedRequest != null;
		assertEquals("qwen2.5-coder:0.5b", capturedRequest.getModel());
		assertEquals(userPrompt, capturedRequest.getPrompt());
		assertEquals(0.0, capturedRequest.getTemperature());
		assertFalse(capturedRequest.isStream());
		assertEquals(SHORT_PREDICTS, capturedRequest.getNum_predicts());

		// Verify message creation was not called for short prompt
		verify(messageService, never()).createMessage(any(), any(), any(), anyBoolean());
	}

	@Test
	void sendPromptShort_CharacterLimitExceeded() {

		String longPrompt = "a".repeat(CHARACTER_LENGTH); // Create a string longer than 500 characters

		RuntimeException exception = assertThrows(RuntimeException.class, () -> summaryService.sendPromptShort(longPrompt, userId, sessionId));

		assertEquals("Character Limit of 500 exceeded", exception.getMessage());

		verify(restTemplate, never()).exchange(
				anyString(),
				any(HttpMethod.class),
				any(HttpEntity.class),
				any(Class.class)
		);
	}

	@Test
	void sendPromptDescriptive_Success() throws IOException {
		// Arrange
		GeneratePromptResponse promptResponse = new GeneratePromptResponse();
		promptResponse.setResponse(generatedResponse);

		ResponseEntity<GeneratePromptResponse> responseEntity = ResponseEntity.ok(promptResponse);

		when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.POST),
				any(HttpEntity.class),
				eq(GeneratePromptResponse.class)
		)).thenReturn(responseEntity);

		when(userRepository.findById(new ObjectId(userId))).thenReturn(Optional.of(new User()));
		when(sessionRepository.findById(new ObjectId(sessionId))).thenReturn(Optional.of(new Session()));

		SummaryResponse result = summaryService.sendPromptDescriptive(userPrompt, userId, sessionId);

		assertNotNull(result);
		assertEquals(HttpStatusCode.OK, result.getStatusCode());

		Map<String, String> bodyMap = (Map<String, String>) result.getBody();
		assertEquals(generatedResponse, bodyMap.get("message"));

		assertEquals("/ollama/generate/descriptive", result.getUrl());

		verify(messageService).createMessage(userId, sessionId, generatedResponse, true);

		ArgumentCaptor<HttpEntity<GeneratePromptRequest>> requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), requestCaptor.capture(), eq(GeneratePromptResponse.class));

		GeneratePromptRequest capturedRequest = requestCaptor.getValue().getBody();
		assert capturedRequest != null;
		assertEquals("qwen2.5-coder:0.5b", capturedRequest.getModel());
		assertEquals(userPrompt, capturedRequest.getPrompt());
		assertEquals(0.0, capturedRequest.getTemperature());
		assertFalse(capturedRequest.isStream());
		assertEquals(LONG_PREDICTS, capturedRequest.getNum_predicts());
	}

	@Test
	void generateChat_NullResponse_ThrowsTimeoutException() {
		when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.POST),
				any(HttpEntity.class),
				eq(GeneratePromptResponse.class)
		)).thenReturn(ResponseEntity.ok(null));

		TimeoutException exception = assertThrows(TimeoutException.class, () -> summaryService.sendPromptShort(userPrompt, userId, sessionId));

		assertEquals("Ollama API response timed out or returned an empty body.", exception.getMessage());
	}

	@Test
	void createMessageEntry_UserNotFound() {
		when(userRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

		Error exception = assertThrows(Error.class, () -> {
			GeneratePromptResponse promptResponse = new GeneratePromptResponse();
			promptResponse.setResponse(generatedResponse);
			ResponseEntity<GeneratePromptResponse> responseEntity = ResponseEntity.ok(promptResponse);

			when(restTemplate.exchange(
					anyString(),
					eq(HttpMethod.POST),
					any(HttpEntity.class),
					eq(GeneratePromptResponse.class)
			)).thenReturn(responseEntity);

			summaryService.sendPromptDescriptive(userPrompt, userId, sessionId);
		});

		assertEquals("User Not Found", exception.getMessage());
	}

	@Test
	void createMessageEntry_SessionNotFound() {
		when(userRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(new User()));
		when(sessionRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

		Error exception = assertThrows(Error.class, () -> {
			GeneratePromptResponse promptResponse = new GeneratePromptResponse();
			promptResponse.setResponse(generatedResponse);
			ResponseEntity<GeneratePromptResponse> responseEntity = ResponseEntity.ok(promptResponse);

			when(restTemplate.exchange(
					anyString(),
					eq(HttpMethod.POST),
					any(HttpEntity.class),
					eq(GeneratePromptResponse.class)
			)).thenReturn(responseEntity);

			summaryService.sendPromptDescriptive(userPrompt, userId, sessionId);
		});

		assertEquals("Session Not Found", exception.getMessage());
	}

	@Test
	void generateChat_ResponseWithNullBody_ThrowsTimeoutException() {
		GeneratePromptResponse promptResponse = new GeneratePromptResponse();
		promptResponse.setResponse(null);

		ResponseEntity<GeneratePromptResponse> responseEntity = ResponseEntity.ok(promptResponse);

		when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.POST),
				any(HttpEntity.class),
				eq(GeneratePromptResponse.class)
		)).thenReturn(responseEntity);

		TimeoutException exception = assertThrows(TimeoutException.class, () -> summaryService.sendPromptShort(userPrompt, userId, sessionId));

		assertEquals("Ollama API response timed out or returned an empty body.", exception.getMessage());
	}
}

