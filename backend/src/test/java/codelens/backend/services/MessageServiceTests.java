package codelens.backend.services;


import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import codelens.backend.DocxFile.service.DocxService;
import codelens.backend.Messages.entity.Message;
import codelens.backend.Messages.repository.MessageRepository;
import codelens.backend.Messages.requestEntity.CreateMessageRequest;
import codelens.backend.Messages.service.MessageServiceImpl;
import codelens.backend.Session.entity.Session;
import codelens.backend.Session.repository.SessionRepository;
import codelens.backend.User.User;
import codelens.backend.User.UserRepository;

@ExtendWith(MockitoExtension.class)
class MessageServiceTests {
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private DocxService docxService;

    @InjectMocks
    private MessageServiceImpl messageService;

    private Message message;
    private ObjectId userId;
    private ObjectId sessionId;

    @BeforeEach
    public void setUp() {
        userId = new ObjectId();
        sessionId = new ObjectId();

        message = Message.builder()
                .id(new ObjectId().toString())
                .userId(userId.toString())
                .sessionId(sessionId.toString())
                .message("Test Message")
                .isAIGenerated(false)
                .createdAt(Instant.now())
                .build();
    }

    /**
     * Test Case: Creating a Message for user
     */
    @Test
    public void CreateMessage_ReturnsCreatedMessage() throws IOException {
        CreateMessageRequest request = new CreateMessageRequest(userId.toString(), sessionId.toString(), "Test Message1" , true);

        when(userRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(new User()));
        when(sessionRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(new Session()));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        doNothing().when(docxService).generateDocx(any(Message.class));

        Message createdMessage = messageService.createMessage(message.getUserId() , message.getSessionId(), message.getMessage(),message.getIsAIGenerated());

        assertThat(createdMessage).isNotNull();
        assertThat(createdMessage.getMessage()).isEqualTo("Test Message");

        Mockito.verify(userRepository , times(1)).findById(any(ObjectId.class));
        Mockito.verify(sessionRepository, times(1)).findById(any(ObjectId.class));
        Mockito.verify(messageRepository, times(1)).save(any(Message.class));

    }

    /**
     * Test Case: Retrieving Messages by Id
     */
    @Test
    public void GetMessageById_ReturnsEmptyOptional() {
        Optional<Message> message = messageService.getMessageById(userId);
        assertThat(message).isEmpty();
    }

    /**
     * Test Case: Retrieving Messages by Id and sessionId
     */
    @Test
    public void GetMessageByUserIdAndSessionId_ReturnsMessage() {
        String uId= userId.toString();
        String sId= sessionId.toString();
        when(messageRepository.getMessagesByUserIdAndSessionIdOrderByCreatedAt(uId , sId)).thenReturn(List.of(message));

        List<Message> messages = messageService.getMessageByUserIdAndSessionId(uId, sId);

        assertThat(messages.size()).isEqualTo(1);

        Mockito.verify(messageRepository , times(1)).getMessagesByUserIdAndSessionIdOrderByCreatedAt(uId , sId);

    }



}