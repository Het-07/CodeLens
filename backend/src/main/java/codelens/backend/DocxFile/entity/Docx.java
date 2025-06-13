package codelens.backend.DocxFile.entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "document")
public class Docx {
    @Id
    private ObjectId id;

    @NotNull
    private String messageId;

    @NotNull
    private byte[] docxFile;

}
