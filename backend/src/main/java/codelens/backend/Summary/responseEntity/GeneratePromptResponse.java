package codelens.backend.Summary.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneratePromptResponse {
	private String model;
	private String created_at;
	private String response;
	private String done;
	private String done_reason;
	private List<String> context;
	private String total_duration;
	private String load_duration;
	private String prompt_eval_count;
	private String prompt_eval_duration;
	private String eval_count;
	private String eval_duration;

}
