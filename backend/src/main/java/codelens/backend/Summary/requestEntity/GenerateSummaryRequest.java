package codelens.backend.Summary.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateSummaryRequest {
	private String userId;
	private String sessionId;
	private String userPrompt;
}
