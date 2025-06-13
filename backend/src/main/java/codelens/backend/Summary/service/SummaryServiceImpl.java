package codelens.backend.Summary.service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import codelens.backend.Summary.responseEntity.SummaryResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import codelens.backend.Messages.service.MessageService;
import codelens.backend.Session.repository.SessionRepository;
import codelens.backend.Summary.requestEntity.GeneratePromptRequest;
import codelens.backend.Summary.responseEntity.GeneratePromptResponse;
import codelens.backend.User.UserRepository;
import codelens.backend.util.HttpStatusCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;



@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService{
	private final RestTemplate restTemplate;
	private final MessageService messageService;
	private final UserRepository userRepository;
	private final SessionRepository sessionRepository;
	private static final int SHORT_PREDICT = 30;
	private static final int LONG_PREDICT = 50;
	private static final int PROMPT_LENGTH = 500;

	private final String model = "qwen2.5-coder:0.5b";

	@Value("${llama.bot.url}")
	private String llama;

	/**
	 * Generates a short summary response based on the user's input.
	 *
	 * @param userPrompt The prompt provided by the user.
	 * @param userId The user ID.
	 * @param sessionId The session ID.
	 * @return A SummaryResponse containing the generated chat response.
	 */
	@Override
	public SummaryResponse sendPromptShort(String userPrompt, String userId, String sessionId) {
		if(pluginCharacterValidation(userPrompt)){
			throw new RuntimeException("Character Limit of 500 exceeded");
		}

		GeneratePromptRequest payload = GeneratePromptRequest
				.builder()
				.model(model)
				.prompt(userPrompt)
				.temperature(0.0)
				.stream(false)
				.num_predicts(SHORT_PREDICT)
				.build();
		return generateChat(payload, "/ollama/generate/short",userId, sessionId);
	}

	/**
	 * Generates a more detailed summary response based on the user's input.
	 *
	 * @param userPrompt The prompt provided by the user.
	 * @param userId The user ID.
	 * @param sessionId The session ID.
	 * @return A SummaryResponse containing the generated chat response.
	 */
	@Override
	public SummaryResponse sendPromptDescriptive(String userPrompt, String userId, String sessionId) {
		GeneratePromptRequest payload = GeneratePromptRequest
				.builder()
				.model(model)
				.prompt(userPrompt)
				.temperature(0.0)
				.stream(false)
				.num_predicts(LONG_PREDICT)
				.build();

		return generateChat(payload, "/ollama/generate/descriptive", userId, sessionId);
	}

	/**
	 * Helper method to send the prompt to the Ollama API and receive the response.
	 *
	 * @param payload The request payload to be sent to the API.
	 * @param url The endpoint URL for the request.
	 * @param userId The user ID.
	 * @param sessionId The session ID.
	 * @return A SummaryResponse containing the generated chat response.
	 * @throws TimeoutException If the API response times out or is empty.
	 */
	@SneakyThrows
	private SummaryResponse generateChat(GeneratePromptRequest payload, String url, String userId, String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<GeneratePromptRequest> entity = new HttpEntity<>(payload, headers);

		ResponseEntity<GeneratePromptResponse> response = restTemplate.exchange(llama,HttpMethod.POST, entity, GeneratePromptResponse.class);

		if(response.getBody() != null && response.getBody().getResponse() != null){
			String generatedChat = response.getBody().getResponse();

			if(url.equals("/ollama/generate/descriptive")){
				createMessageEntry(generatedChat, userId, sessionId);
			}

			return SummaryResponse
					.builder()
					.statusCode(HttpStatusCode.OK)
					.body(Map.of(
							"message", generatedChat
					))
					.url(url)
					.timestamp(new Date().toString())
					.build();
		}
		throw new TimeoutException("Ollama API response timed out or returned an empty body.");
	}

	/**
	 * Creates a message entry for the generated chat response in the database.
	 *
	 * @param response The generated chat response.
	 * @param userId The user ID.
	 * @param sessionId The session ID.
	 * @throws IOException If there is an error creating the message entry.
	 */
	private void createMessageEntry (String response, String userId, String sessionId) throws IOException {
		userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new Error("User Not Found"));
		sessionRepository.findById(new ObjectId(sessionId)).orElseThrow(() -> new Error("Session Not Found"));
		messageService.createMessage(userId,sessionId,response,true);
	}

	/**
	 * Validates whether the user's prompt exceeds the character limit of 500 characters.
	 *
	 * @param userPrompt The user input prompt.
	 * @return true if the prompt exceeds 500 characters, false otherwise.
	 */
	private boolean pluginCharacterValidation (String userPrompt){
		return userPrompt.length() > PROMPT_LENGTH; 
	}
}
