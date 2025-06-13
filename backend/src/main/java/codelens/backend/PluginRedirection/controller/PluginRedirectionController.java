package codelens.backend.PluginRedirection.controller;
import codelens.backend.PluginRedirection.requestEntity.PluginRedirectionRequest;
import codelens.backend.PluginRedirection.responseEntity.PluginRedirectionResponse;
import codelens.backend.PluginRedirection.service.PluginRedirectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/redirect")
@RequiredArgsConstructor
public class PluginRedirectionController {
	private final PluginRedirectionService pluginRedirectionService;

	@PostMapping("/codelens")
	public ResponseEntity<PluginRedirectionResponse> redirect (@Valid @RequestBody PluginRedirectionRequest req) throws IOException {
		return ResponseEntity.ok(pluginRedirectionService.createPluginSessionwithMessage(req.getUserId(),req.getUserPrompt()));
	}
}
