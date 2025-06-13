package codelens.backend.Messages.controller;

import codelens.backend.Messages.entity.Message;
import codelens.backend.Messages.requestEntity.CreateMessageRequest;
import codelens.backend.Messages.responseEntity.MessageResponse;
import codelens.backend.Messages.service.MessageService;
import codelens.backend.util.HttpStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/")
    public ResponseEntity<MessageResponse> createMessage(@RequestBody CreateMessageRequest req) throws IOException {
        Message message = messageService.createMessage(req.getUserId(), req.getSessionId(), req.getMessage(), req.getIsAIGenerated());

        MessageResponse createMessageResponse = MessageResponse.builder()
                .url("/api/v1/message")
                .statusCode(HttpStatusCode.CREATED)
                .body(Map.of(
                        "response", message,
                        "message", "Message created Successfully"
                ))
                .timestamp(new Date().toString())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(createMessageResponse);
    }

    @GetMapping("/{userId}/{sessionId}")
    public ResponseEntity<MessageResponse> getMessage(@PathVariable("userId") String userId, @PathVariable("sessionId") String sessionId) {
        List<Message> messages = messageService.getMessageByUserIdAndSessionId(userId, sessionId);

        MessageResponse response = MessageResponse.builder()
                .url("/api/v1/message")
                .statusCode(HttpStatusCode.OK)
                .body(Map.of(
                        "messages", messages
                ))
                .timestamp(new Date().toString())
                .build();

        return ResponseEntity.ok(response);
    }
}
