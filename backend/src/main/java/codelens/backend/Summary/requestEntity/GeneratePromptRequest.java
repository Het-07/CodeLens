package codelens.backend.Summary.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneratePromptRequest {
	private String model;
	private String prompt;
	private Double temperature;
	private boolean stream;
	private Integer num_predicts;
}
