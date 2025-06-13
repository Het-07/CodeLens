package codelens.backend.services;

import codelens.backend.Document.requestEntity.DocumentRequest;
import codelens.backend.Document.responseEntity.DocumentResponse;
import codelens.backend.Document.service.DocumentServiceImpl;
import codelens.backend.Messages.entity.Message;
import codelens.backend.Messages.repository.MessageRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DocumentServiceImplTest {

    @InjectMocks
    private DocumentServiceImpl documentServiceImpl;

    @Mock
    private MessageRepository messageRepository;

    private Message mockMessage;
    private ObjectId messageObjectId;
    private DocumentRequest documentRequest;

    // Add reflection methods
    private Method generateDocxMethod;
    private Method extractCodeBlocksMethod;
    private static final int CODE_BLOCK_SIZE = 2;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        messageObjectId = new ObjectId("60d9b98d9b9a1c0d2c2d2f2f");
        mockMessage = new Message();
        mockMessage.setId(String.valueOf(messageObjectId));
        mockMessage.setMessage("## Title\nSome content with code block: ```java\nSystem.out.println('Hello');```");

        // Using the builder pattern for DocumentRequest
        documentRequest = DocumentRequest.builder()
                .sessionId("session1")
                .messageId(messageObjectId.toString())
                .build();

        // Get access to private methods via reflection
        generateDocxMethod = DocumentServiceImpl.class.getDeclaredMethod("generateDocxFromMessage", String.class);
        generateDocxMethod.setAccessible(true);

        extractCodeBlocksMethod = DocumentServiceImpl.class.getDeclaredMethod("extractCodeBlocksSimple", String.class);
        extractCodeBlocksMethod.setAccessible(true);
    }

    // Original tests unchanged...

    @Test
    void testGenerateAndStoreDocument_success() throws IOException {
        // Arrange
        when(messageRepository.findById(messageObjectId.toString())).thenReturn(Optional.of(mockMessage));

        // Act
        DocumentResponse response = documentServiceImpl.generateAndStoreDocument(documentRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Document generated successfully", response.getMessage());
        assertEquals("Stored in database", response.getFilePath());
        verify(messageRepository, times(1)).save(mockMessage);
    }

    @Test
    void testGenerateAndStoreDocument_messageNotFound() {
        // Arrange
        documentRequest = DocumentRequest.builder()
                .sessionId("session1")
                .messageId("invalidMessageId")
                .build();

        when(messageRepository.findById(any(String.class))).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(IOException.class, () -> documentServiceImpl.generateAndStoreDocument(documentRequest));
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void testGenerateAndStoreDocument_emptyMessageContent() {
        // Arrange
        mockMessage.setMessage("");
        when(messageRepository.findById(messageObjectId.toString())).thenReturn(Optional.of(mockMessage));

        // Act and Assert
        assertThrows(IOException.class, () -> documentServiceImpl.generateAndStoreDocument(documentRequest));
        verify(messageRepository, never()).save(mockMessage);
    }

    @Test
    void testGenerateAndStoreDocument_nullMessageContent() {
        // Arrange
        mockMessage.setMessage(null);
        when(messageRepository.findById(messageObjectId.toString())).thenReturn(Optional.of(mockMessage));

        // Act and Assert
        assertThrows(IOException.class, () -> documentServiceImpl.generateAndStoreDocument(documentRequest));
        verify(messageRepository, never()).save(mockMessage);
    }

    @Test
    void testGenerateAndStoreDocument_exceptionDuringGeneration() {
        // Arrange
        when(messageRepository.findById(messageObjectId.toString())).thenReturn(Optional.of(mockMessage));

        // Mock the messageRepository to throw a RuntimeException instead
        doThrow(new RuntimeException("Error generating document"))
                .when(messageRepository).save(any(Message.class));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> documentServiceImpl.generateAndStoreDocument(documentRequest));
    }

    @Test
    void testDownloadDocument_success() throws IOException {
        // Arrange
        byte[] mockDocumentBytes = "Mock document bytes".getBytes();
        mockMessage.setDocument(mockDocumentBytes);
        when(messageRepository.findById(messageObjectId.toString())).thenReturn(Optional.of(mockMessage));

        // Act
        byte[] result = documentServiceImpl.downloadDocument(messageObjectId.toString());

        // Assert
        assertArrayEquals(mockDocumentBytes, result);
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void testDownloadDocument_generateOnTheFly() throws Exception {
        // Arrange
        when(messageRepository.findById(messageObjectId.toString())).thenReturn(Optional.of(mockMessage));

        // Act
        byte[] result = documentServiceImpl.downloadDocument(messageObjectId.toString());

        // Assert - just check it's not empty and has reasonable size
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(messageRepository, times(1)).save(mockMessage);
    }

    @Test
    void testDownloadDocument_emptyDocument() throws Exception {
        // Arrange
        mockMessage.setDocument(new byte[0]);
        when(messageRepository.findById(messageObjectId.toString())).thenReturn(Optional.of(mockMessage));

        // Act
        byte[] result = documentServiceImpl.downloadDocument(messageObjectId.toString());

        // Assert - Fix: Instead of comparing byte arrays, verify document was generated
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(messageRepository, times(1)).save(mockMessage);
    }

    @Test
    void testDownloadDocument_nullDocument() throws Exception {
        // Arrange
        mockMessage.setDocument(null);
        when(messageRepository.findById(messageObjectId.toString())).thenReturn(Optional.of(mockMessage));

        // Act
        byte[] result = documentServiceImpl.downloadDocument(messageObjectId.toString());

        // Assert - Fix: Instead of comparing byte arrays, verify document was generated
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(messageRepository, times(1)).save(mockMessage);
    }

    @Test
    void testDownloadDocument_nullMessageContent() {
        // Arrange
        mockMessage.setDocument(null);
        mockMessage.setMessage(null);
        when(messageRepository.findById(messageObjectId.toString())).thenReturn(Optional.of(mockMessage));

        // Act and Assert
        assertThrows(IOException.class, () -> documentServiceImpl.downloadDocument(messageObjectId.toString()));
    }

    @Test
    void testDownloadDocument_emptyMessageContent() {
        // Arrange
        mockMessage.setDocument(null);
        mockMessage.setMessage("");
        when(messageRepository.findById(messageObjectId.toString())).thenReturn(Optional.of(mockMessage));

        // Act and Assert
        assertThrows(IOException.class, () -> documentServiceImpl.downloadDocument(messageObjectId.toString()));
    }

    @Test
    void testDownloadDocument_messageNotFound() {
        // Arrange
        when(messageRepository.findById(any(String.class))).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(IOException.class, () -> documentServiceImpl.downloadDocument("invalidMessageId"));
    }

    @Test
    void testGenerateDocxFromMessage() throws Exception {
        // Arrange
        String messageText = "Test message with code block: ```java\nSystem.out.println('Hello');```";

        // Act
        byte[] docxBytes = (byte[]) generateDocxMethod.invoke(documentServiceImpl, messageText);

        // Assert
        assertNotNull(docxBytes);
        assertTrue(docxBytes.length > 0);
    }

    @Test
    void testGenerateDocxFromMessage_withMultipleCodeBlocks() throws Exception {
        // Arrange
        String messageText = "Test with multiple code blocks:\n```java\nSystem.out.println('Hello');\n```\n```python\nprint('Hello')\n```";

        // Act
        byte[] docxBytes = (byte[]) generateDocxMethod.invoke(documentServiceImpl, messageText);

        // Assert
        assertNotNull(docxBytes);
        assertTrue(docxBytes.length > 0);
    }

    @Test
    void testGenerateDocxFromMessage_withoutCodeBlocks() throws Exception {
        // Arrange
        String messageText = "Test message with no code blocks";

        // Act
        byte[] docxBytes = (byte[]) generateDocxMethod.invoke(documentServiceImpl, messageText);

        // Assert
        assertNotNull(docxBytes);
        assertTrue(docxBytes.length > 0);
    }

    @Test
    void testGenerateDocxFromMessage_withEmptyMessage() throws Exception {
        // Arrange
        String messageText = "";

        // Act
        byte[] docxBytes = (byte[]) generateDocxMethod.invoke(documentServiceImpl, messageText);

        // Assert
        assertNotNull(docxBytes);
        assertTrue(docxBytes.length > 0);  // Even with empty content, a doc should be generated
    }

    @Test
    void testExtractCodeBlocksSimple() throws Exception {
        // Arrange
        String messageText = "Some text with code blocks:\n```java\nCode block 1```\n```python\nCode block 2```";

        // Act
        @SuppressWarnings("unchecked")
        List<String> codeBlocks = (List<String>) extractCodeBlocksMethod.invoke(documentServiceImpl, messageText);

        // Assert
        assertNotNull(codeBlocks);
        assertEquals(CODE_BLOCK_SIZE, codeBlocks.size());
        assertEquals("java\nCode block 1", codeBlocks.get(0));
        assertEquals("python\nCode block 2", codeBlocks.get(1));
    }

    @Test
    void testExtractCodeBlocksSimple_withMultilineCodeBlocks() throws Exception {
        // Arrange
        String messageText = "Text with multiline code:\n```java\nLine 1\nLine 2\nLine 3\n```";

        // Act
        @SuppressWarnings("unchecked")
        List<String> codeBlocks = (List<String>) extractCodeBlocksMethod.invoke(documentServiceImpl, messageText);

        // Assert
        assertNotNull(codeBlocks);
        assertEquals(1, codeBlocks.size());
        assertEquals("java\nLine 1\nLine 2\nLine 3\n", codeBlocks.get(0));
    }

    @Test
    void testExtractCodeBlocksSimple_noCodeBlocks() throws Exception {
        // Arrange
        String messageText = "Some text without code blocks";

        // Act
        @SuppressWarnings("unchecked")
        List<String> codeBlocks = (List<String>) extractCodeBlocksMethod.invoke(documentServiceImpl, messageText);

        // Assert
        assertNotNull(codeBlocks);
        assertTrue(codeBlocks.isEmpty());
    }

    @Test
    void testExtractCodeBlocksSimple_withIncompleteCodeBlock() throws Exception {
        // Arrange
        String messageText = "Text with incomplete code block: ```java\nCode without closing ticks";

        // Act
        @SuppressWarnings("unchecked")
        List<String> codeBlocks = (List<String>) extractCodeBlocksMethod.invoke(documentServiceImpl, messageText);

        // Assert
        assertNotNull(codeBlocks);
        assertTrue(codeBlocks.isEmpty());  // Should not extract incomplete blocks
    }

    @Test
    void testExtractCodeBlocksSimple_withEmptyCodeBlock() throws Exception {
        // Arrange
        String messageText = "Text with empty code block: ``````";

        // Act
        @SuppressWarnings("unchecked")
        List<String> codeBlocks = (List<String>) extractCodeBlocksMethod.invoke(documentServiceImpl, messageText);

        // Assert
        assertNotNull(codeBlocks);
        assertEquals(1, codeBlocks.size());
        assertEquals("", codeBlocks.get(0));
    }
}