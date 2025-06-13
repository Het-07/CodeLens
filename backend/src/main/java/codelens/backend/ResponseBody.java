package codelens.backend;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBody {
	private Integer statusCode;
	private Object body;
	private String url;

	private String timestamp;
}
