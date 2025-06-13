package codelens.backend.Session.requestEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSessionRequest {
    String sessionName;
    String sessionId;
    String userId;
}
