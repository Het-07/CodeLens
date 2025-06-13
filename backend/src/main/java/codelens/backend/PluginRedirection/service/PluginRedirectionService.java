package codelens.backend.PluginRedirection.service;

import codelens.backend.PluginRedirection.responseEntity.PluginRedirectionResponse;

import java.io.IOException;

public interface PluginRedirectionService {
	PluginRedirectionResponse createPluginSessionwithMessage (String userId, String userPrompt) throws IOException;
}
