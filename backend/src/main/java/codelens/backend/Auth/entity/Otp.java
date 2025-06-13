package codelens.backend.Auth.entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "otp")
public class Otp {
    @Id
    private ObjectId id;

    @NotNull
    @Indexed(unique = true)
    private String otp;

    @NotNull
    private Instant expiryDate;

    @NotNull
    @Indexed(unique = true)
    private ObjectId userId;
}

