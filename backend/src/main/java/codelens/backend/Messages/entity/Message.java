package codelens.backend.Messages.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "message")
public class Message {
    @Id
    private String id;

    @NotNull
    private String sessionId;

    private String userId;

    @NotNull
    private String message;

    @NotNull
    private Boolean isAIGenerated;

    private Instant createdAt;

    private byte[] document;
}

