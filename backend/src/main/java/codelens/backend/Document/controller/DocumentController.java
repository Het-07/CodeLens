package codelens.backend.Document.controller;

import codelens.backend.Document.requestEntity.DocumentRequest;
import codelens.backend.Document.responseEntity.DocumentResponse;
import codelens.backend.Document.service.DocumentServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/v1")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    private final DocumentServiceInterface documentService;

    @Autowired
    public DocumentController(DocumentServiceInterface documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/generate-docx")
    public ResponseEntity<DocumentResponse> generateDocx(
            @RequestParam String sessionId,
            @RequestParam String messageId) {
        try {
            logger.info("Received request to generate document for messageId: {}", messageId);
            DocumentRequest request = new DocumentRequest();
            request.setSessionId(sessionId);
            request.setMessageId(messageId);

            DocumentResponse response = documentService.generateAndStoreDocument(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error generating document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DocumentResponse("Error generating document: " + e.getMessage(), null));
        }
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadDocument(@RequestParam String messageId) {
        try {
            logger.info("Received request to download document for messageId: {}", messageId);
            byte[] documentBytes = documentService.downloadDocument(messageId);

            if (documentBytes == null || documentBytes.length == 0) {
                logger.error("Document bytes are empty for messageId: {}", messageId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            ByteArrayResource resource = new ByteArrayResource(documentBytes);
            logger.info("Document found. Size: {} bytes", documentBytes.length);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=document_" + messageId + ".docx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(documentBytes.length)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error downloading document", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}