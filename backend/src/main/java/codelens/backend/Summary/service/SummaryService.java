package codelens.backend.Summary.service;
import codelens.backend.Summary.responseEntity.SummaryResponse;

public interface SummaryService {
	SummaryResponse sendPromptShort (String userPrompt, String userId, String sessionId);
	SummaryResponse sendPromptDescriptive (String userPrompt, String userId, String sessionId);

}
