package codelens.backend.Document.service;

import codelens.backend.Document.entity.*;
import codelens.backend.Document.requestEntity.DocumentRequest;
import codelens.backend.Document.responseEntity.DocumentResponse;
import codelens.backend.Messages.entity.Message;
import codelens.backend.Messages.repository.MessageRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DocumentServiceImpl implements DocumentServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private final MessageRepository messageRepository;
    private final int TITLE_FONT_SIZE = 18;
    private final int INDENT_SIZE = 720;
    private final int CODE_FONT_SIZE = 10;

    /**
     * Constructor to initialize DocumentServiceImpl with MessageRepository dependency.
     *
     * @param messageRepository The repository for managing message entities.
     */
    @Autowired
    public DocumentServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Generates a document based on the provided message ID, stores it in the database,
     * and returns a response with the document details.
     *
     * @param request The request containing the message ID for document generation.
     * @return A DocumentResponse indicating the result of the document generation.
     * @throws IOException If the message cannot be found or there is an error during document generation.
     */
    @Override
    public DocumentResponse generateAndStoreDocument(DocumentRequest request) throws IOException {
        logger.info("Generating document for messageId: {}", request.getMessageId());

        // Fetch the message by ID
        Optional<Message> messageOpt = messageRepository.findById(request.getMessageId());

        if (!messageOpt.isPresent()) {
            logger.error("Message not found with ID: {}", request.getMessageId());
            throw new IOException("Message not found with ID: " + request.getMessageId());
        }

        Message message = messageOpt.get();
        logger.info("Message found: {}", message.getId());
        logger.debug("Message content length: {}", message.getMessage() != null ? message.getMessage().length() : 0);

        // Check if message content exists
        if (message.getMessage() == null || message.getMessage().trim().isEmpty()) {
            logger.error("Message content is empty for ID: {}", request.getMessageId());
            throw new IOException("Message content is empty for ID: " + request.getMessageId());
        }

        // Generate the document directly without storing it
        byte[] docxBytes = generateDocxFromMessage(message.getMessage());

        // Store document bytes in the database
        message.setDocument(docxBytes);
        messageRepository.save(message);
        logger.info("Document generated and stored. Size: {} bytes", docxBytes.length);

        // Create and return the response using the builder pattern
        return DocumentResponse.builder()
                .message("Document generated successfully")
                .filePath("Stored in database")
                .build();
    }

    /**
     * Downloads the document for a given message ID.
     * If the document doesn't exist, it will generate it on-the-fly and return it.
     *
     * @param messageId The ID of the message to download the document for.
     * @return A byte array representing the document.
     * @throws IOException If the message cannot be found or there is an error during document retrieval.
     */
    @Override
    public byte[] downloadDocument(String messageId) throws IOException {
        logger.info("Downloading document for messageId: {}", messageId);

        Optional<Message> messageOpt = messageRepository.findById(messageId);

        if (messageOpt.isEmpty()) {
            logger.error("Message not found with ID: {}", messageId);
            throw new IOException("Message not found with ID: " + messageId);
        }

        Message message = messageOpt.get();

        // If document doesn't exist, generate it on-the-fly
        if (message.getDocument() == null || message.getDocument().length == 0) {
            logger.info("Document not found in database, generating on-the-fly");

            if (message.getMessage() == null || message.getMessage().trim().isEmpty()) {
                logger.error("Message content is empty for ID: {}", messageId);
                throw new IOException("Message content is empty for ID: " + messageId);
            }

            byte[] docxBytes = generateDocxFromMessage(message.getMessage());
            message.setDocument(docxBytes);
            messageRepository.save(message);
            logger.info("Document generated on-the-fly. Size: {} bytes", docxBytes.length);
            return docxBytes;
        }

        logger.info("Document found in database. Size: {} bytes", message.getDocument().length);
        return message.getDocument();
    }

    /**
     * Generates a DOCX file from the provided message text.
     *
     * @param messageText The message text to be converted into a DOCX document.
     * @return A byte array representing the generated DOCX file.
     * @throws IOException If there is an error while generating the DOCX file.
     */
    byte[] generateDocxFromMessage(String messageText) throws IOException {
        logger.info("Generating DOCX from message text");

        try (XWPFDocument document = new XWPFDocument()) {
            // Add title
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("CodeLens");
            titleRun.setBold(true);
            titleRun.setFontSize(TITLE_FONT_SIZE);
            titleRun.addBreak();

            // Add content paragraph
            XWPFParagraph contentParagraph = document.createParagraph();
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText(messageText);

            // Add code blocks if present
            List<String> codeBlocksText = extractCodeBlocksSimple(messageText);
            if (!codeBlocksText.isEmpty()) {
                XWPFParagraph codeHeader = document.createParagraph();
                XWPFRun codeHeaderRun = codeHeader.createRun();
                codeHeaderRun.setText("Code Samples");
                codeHeaderRun.setBold(true);
                codeHeaderRun.addBreak();

                for (String code : codeBlocksText) {
                    XWPFParagraph codeParagraph = document.createParagraph();
                    codeParagraph.setIndentationLeft(INDENT_SIZE);
                    codeParagraph.setBorderBottom(Borders.SINGLE);
                    codeParagraph.setBorderTop(Borders.SINGLE);
                    codeParagraph.setBorderLeft(Borders.SINGLE);
                    codeParagraph.setBorderRight(Borders.SINGLE);

                    XWPFRun codeRun = codeParagraph.createRun();
                    codeRun.setText(code);
                    codeRun.setFontFamily("Courier New");
                    codeRun.setFontSize(CODE_FONT_SIZE);
                    codeRun.addBreak();
                }
            }

            // Create a parsed content object using the builder pattern
            // Convert the extracted string code blocks to CodeBlock objects
            List<CodeBlock> codeBlocks = new ArrayList<>();
            for (String codeText : codeBlocksText) {
                // Try to extract language from the first line
                String language = "text";
                String code = codeText;

                int newlineIndex = codeText.indexOf('\n');
                if (newlineIndex > 0) {
                    language = codeText.substring(0, newlineIndex).trim();
                    code = codeText.substring(newlineIndex + 1);
                }

                codeBlocks.add(CodeBlock.builder()
                        .language(language)
                        .code(code)
                        .build());
            }

            ParsedContent ParsedContent = codelens.backend.Document.entity.ParsedContent.builder()
                    .title("CodeLens Document")
                    .codeBlocks(codeBlocks)
                    .sections(new ArrayList<>())
                    .bulletPoints(new ArrayList<>())
                    .originalContent(messageText)
                    .build();

            // Write to byte array and return
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                document.write(out);
                byte[] result = out.toByteArray();
                logger.info("Document generated successfully. Size: {} bytes", result.length);
                return result;
            }
        } catch (Exception e) {
            throw new IOException("Error generating document: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts code blocks from the provided text. A code block is identified by the ``` markers.
     *
     * @param text The text from which code blocks should be extracted.
     * @return A list of code blocks extracted from the text.
     */
    List<String> extractCodeBlocksSimple(String text) {
        List<String> codeBlocks = new ArrayList<>();
        int index = 0;

        while (index < text.length()) {
            // Find opening ```
            int startIndex = text.indexOf("```", index);
            if (startIndex == -1) break;

            // Find closing ```
            int endIndex = text.indexOf("```", startIndex + 3);
            if (endIndex == -1) break;

            // Extract everything between the backticks (including the opening language identifier)
            String content = text.substring(startIndex + 3, endIndex);
            codeBlocks.add(content);

            // Update index to continue searching after this match
            index = endIndex + 3;
        }

        return codeBlocks;
    }
}