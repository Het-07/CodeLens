package codelens.backend.Auth.requestEntity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
	private String first_name;
	private String last_name;
	private String email;
	private String password;
}
