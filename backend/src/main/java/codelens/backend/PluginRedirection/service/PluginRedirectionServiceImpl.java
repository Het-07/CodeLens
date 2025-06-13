package codelens.backend.PluginRedirection.service;

import codelens.backend.Messages.service.MessageService;
import codelens.backend.PluginRedirection.responseEntity.PluginRedirectionResponse;
import codelens.backend.Session.entity.Session;
import codelens.backend.Session.service.SessionService;
import codelens.backend.User.UserRepository;
import codelens.backend.util.HttpStatusCode;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PluginRedirectionServiceImpl implements PluginRedirectionService{

	private final SessionService sessionService;
	private final UserRepository userRepository;
	private final MessageService messageService;

	/**
	 * Creates a new session and message for the user, and returns a redirection response.
	 *
	 * @param userId The ID of the user creating the session.
	 * @param userPrompt The user's input message.
	 * @return A PluginRedirectionResponse object with session details and success message.
	 * @throws IOException If there is an error while generating the message.
	 */
	public PluginRedirectionResponse createPluginSessionwithMessage (String userId, String userPrompt) throws IOException {
		String sessionId = generateSession(userId);
		generateMessage(userId,userPrompt,sessionId);
		return PluginRedirectionResponse
				.builder()
				.statusCode(HttpStatusCode.OK)
				.body(Map.of(
						"sessionId", sessionId,
						"userId", userId,
						"message", "Redirected Successfully"
				))
				.url("/api/v1/redirect/codelens")
				.timestamp(new Date().toString())
				.build();
	}

	/**
	 * Generates a session for the given user.
	 *
	 * @param userId The ID of the user for whom the session will be created.
	 * @return The ID of the newly created session.
	 */
	private String generateSession (String userId){
		userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new Error("User not found"));
		Session session = sessionService.createSession(userId, "Plugin New Chat");
		return session.getId();
	}

	/**
	 * Generates a message for the user in the specified session.
	 *
	 * @param userId The ID of the user sending the message.
	 * @param userPrompt The content of the message sent by the user.
	 * @param sessionId The ID of the session where the message is being sent.
	 * @throws IOException If there is an error while generating the message.
	 */
	private void generateMessage (String userId, String userPrompt, String sessionId) throws IOException {
		userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new Error("User Not Found"));
		messageService.createMessage(userId,sessionId,userPrompt,false);
	}
}
