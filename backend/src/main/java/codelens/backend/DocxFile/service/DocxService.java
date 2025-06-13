package codelens.backend.DocxFile.service;
import codelens.backend.DocxFile.entity.Docx;
import codelens.backend.Messages.entity.Message;
import org.bson.types.ObjectId;

import java.io.IOException;

public interface DocxService {
    void generateDocx(Message message) throws IOException;
    Docx getDocx(String messageId) throws IOException;
}
