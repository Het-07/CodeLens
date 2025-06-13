package codelens.backend.DocxFile.service;
import codelens.backend.DocxFile.entity.Docx;
import codelens.backend.DocxFile.repository.DocxRepository;
import codelens.backend.Messages.entity.Message;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class DocxServiceImpl implements DocxService {

    private final DocxRepository docxRepository;
    private final int fontSize = 14;

    /**
     * Generates a DOCX file from the provided message and stores it in the database.
     *
     * @param message The message to be used to generate the DOCX file.
     * @throws IOException If there is an issue reading/writing the DOCX file.
     */
    @Override
    public void generateDocx(Message message) throws IOException {

        try (XWPFDocument xwpfDocument = new XWPFDocument()) {

            String folder = "src/main/java/codelens/backend/DocxFile/Summary/";
            String fileName = "Response.docx";
            Path filePath = Paths.get(folder, fileName);

            // Ensure directory exists
            Files.createDirectories(Paths.get(folder));

            // Create document content
            XWPFParagraph paragraph = xwpfDocument.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setFontSize(fontSize);
            run.setText(message.getMessage());
            run.setFontSize(fontSize); // No effect as the last applied font size is used

            // Write document to file
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile())) {
                xwpfDocument.write(fileOutputStream);
            }

            // Read the file and store it in the database
            byte[] docxBytes = Files.readAllBytes(filePath);

            Docx document = new Docx();
            document.setMessageId(message.getId());
            document.setDocxFile(docxBytes);
            docxRepository.save(document);

        }catch (IOException e) {
            throw new RuntimeException("Error while generating DOCX file", e);
        }
    }

    /**
     * Retrieves a DOCX file associated with a given message ID.
     *
     * @param messageId The message ID for which the DOCX file is retrieved.
     * @return The Docx entity containing the DOCX file.
     * @throws IOException If there is an issue reading the DOCX file.
     */
    @Override
    public Docx getDocx(String messageId) throws IOException {
        return docxRepository.findByMessageId(messageId);
    }
}
