package codelens.backend.PluginRedirection.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PluginRedirectionRequest {
	private String userId;
	private String userPrompt;
}