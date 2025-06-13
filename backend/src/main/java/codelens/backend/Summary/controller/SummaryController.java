package codelens.backend.Summary.controller;

import codelens.backend.Summary.requestEntity.GenerateSummaryRequest;
import codelens.backend.Summary.responseEntity.SummaryResponse;
import codelens.backend.Summary.service.SummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ollama/generate")
@RequiredArgsConstructor
public class SummaryController {

	private final SummaryService summaryService;

	@PostMapping("/short")
	public ResponseEntity<SummaryResponse> summaryShort(@Valid @RequestBody GenerateSummaryRequest req){
		return ResponseEntity.ok(summaryService.sendPromptShort(req.getUserPrompt(),req.getUserId(),req.getSessionId()));

	}
	@PostMapping("/descriptive")
	public ResponseEntity<SummaryResponse> summaryDescriptive(@Valid @RequestBody GenerateSummaryRequest req){
		return ResponseEntity.ok(summaryService.sendPromptDescriptive(req.getUserPrompt(),req.getUserId(),req.getSessionId()));

	}
}
