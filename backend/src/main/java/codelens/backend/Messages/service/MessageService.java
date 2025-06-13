package codelens.backend.Messages.service;

import codelens.backend.Messages.entity.Message;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MessageService {
    Message createMessage(String userID, String sessionId, String userMessage, boolean IsAIGenerated) throws IOException;
    Optional<Message> getMessageById(ObjectId id);
    List<Message> getMessageByUserIdAndSessionId(String id, String sessionId);
}
