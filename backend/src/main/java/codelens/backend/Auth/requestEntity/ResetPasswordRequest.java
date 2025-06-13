package codelens.backend.Auth.requestEntity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {
    private static final int MIN_PASSWORD_LENGTH = 8;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = MIN_PASSWORD_LENGTH, message = "Password must have at least " + MIN_PASSWORD_LENGTH + " characters!")
    private String password;

    @NotBlank(message = "Token is required")
    private String token;
}
