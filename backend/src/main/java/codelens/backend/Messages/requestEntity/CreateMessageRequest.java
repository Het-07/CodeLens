package codelens.backend.Messages.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateMessageRequest {
    private String userId;
    private String sessionId;
    private String message;
    private Boolean isAIGenerated;
}
