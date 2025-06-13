package codelens.backend.Document.service;

import codelens.backend.Document.requestEntity.DocumentRequest;
import codelens.backend.Document.responseEntity.DocumentResponse;

import java.io.IOException;

public interface DocumentServiceInterface {
    DocumentResponse generateAndStoreDocument(DocumentRequest request) throws IOException;

    byte[] downloadDocument(String messageId) throws IOException;
}