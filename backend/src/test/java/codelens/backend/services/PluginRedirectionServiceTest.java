package codelens.backend.services;

import codelens.backend.Messages.entity.Message;
import codelens.backend.Messages.service.MessageService;
import codelens.backend.PluginRedirection.responseEntity.PluginRedirectionResponse;
import codelens.backend.PluginRedirection.service.PluginRedirectionServiceImpl;
import codelens.backend.Session.entity.Session;
import codelens.backend.Session.service.SessionService;
import codelens.backend.Summary.service.SummaryService;
import codelens.backend.User.User;
import codelens.backend.User.UserRepository;
import codelens.backend.util.HttpStatusCode;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PluginRedirectionServiceTest {

	@Mock
	private SessionService sessionService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MessageService messageService;

	@Mock
	private SummaryService summaryService;

	@InjectMocks
	private PluginRedirectionServiceImpl pluginRedirectionService;

	private final String userId = "507f1f77bcf86cd799439011";
	private final String userPrompt = "Test prompt";
	private final String sessionId = "507f1f77bcf86cd799439022";
	private User mockUser;
	private Session mockSession;

	@BeforeEach
	void setUp() {
		mockUser = new User();
		mockSession = new Session();
		mockSession.setId(sessionId);
	}

	@Test
	void createPluginSessionWithMessage_Success() throws IOException {
		Message mockMessage = Message
				.builder()
				.message(userPrompt)
				.userId(userId)
				.sessionId(sessionId)
				.isAIGenerated(false)
				.build();
		when(userRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(mockUser));
		when(sessionService.createSession(anyString(), anyString())).thenReturn(mockSession);
		when(messageService.createMessage(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(mockMessage);

		PluginRedirectionResponse response = pluginRedirectionService.createPluginSessionwithMessage(userId, userPrompt);

		assertNotNull(response);
		assertEquals(HttpStatusCode.OK, response.getStatusCode());
		assertEquals("/api/v1/redirect/codelens", response.getUrl());

		Map<String, String> responseBody = (Map<String, String>) response.getBody();
		assertEquals(sessionId, responseBody.get("sessionId"));
		assertEquals(userId, responseBody.get("userId"));
		assertEquals("Redirected Successfully", responseBody.get("message"));

		verify(userRepository, times(2)).findById(any(ObjectId.class));
		verify(sessionService, times(1)).createSession(userId, "Plugin New Chat");
		verify(messageService, times(1)).createMessage(userId, sessionId, userPrompt, false);
	}

	@Test
	void createPluginSessionWithMessage_UserNotFound_InGenerateSession() throws IOException {
		when(userRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

		Error exception = assertThrows(Error.class, () ->
				pluginRedirectionService.createPluginSessionwithMessage(userId, userPrompt)
		);

		assertEquals("User not found", exception.getMessage());
		verify(userRepository, times(1)).findById(any(ObjectId.class));
		verify(sessionService, never()).createSession(anyString(), anyString());
		verify(messageService, never()).createMessage(anyString(), anyString(), anyString(), anyBoolean());
	}

	@Test
	void createPluginSessionWithMessage_UserNotFound_InGenerateMessage() throws IOException {

		when(userRepository.findById(any(ObjectId.class)))
				.thenReturn(Optional.of(mockUser))
				.thenReturn(Optional.empty());

		when(sessionService.createSession(anyString(), anyString())).thenReturn(mockSession);

		Error exception = assertThrows(Error.class, () ->
				pluginRedirectionService.createPluginSessionwithMessage(userId, userPrompt)
		);

		assertEquals("User Not Found", exception.getMessage());
		verify(userRepository, times(2)).findById(any(ObjectId.class));
		verify(sessionService, times(1)).createSession(userId, "Plugin New Chat");
		verify(messageService, never()).createMessage(anyString(), anyString(), anyString(), anyBoolean());
	}

	@Test
	void createPluginSessionWithMessage_MessageServiceThrowsIOException() throws IOException {
		when(userRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(mockUser));
		when(sessionService.createSession(anyString(), anyString())).thenReturn(mockSession);
		doThrow(new IOException("Message creation failed")).when(messageService)
				.createMessage(anyString(), anyString(), anyString(), anyBoolean());

		IOException exception = assertThrows(IOException.class, () ->
				pluginRedirectionService.createPluginSessionwithMessage(userId, userPrompt)
		);

		assertEquals("Message creation failed", exception.getMessage());
		verify(userRepository, times(2)).findById(any(ObjectId.class));
		verify(sessionService, times(1)).createSession(userId, "Plugin New Chat");
		verify(messageService, times(1)).createMessage(userId, sessionId, userPrompt, false);
	}
}