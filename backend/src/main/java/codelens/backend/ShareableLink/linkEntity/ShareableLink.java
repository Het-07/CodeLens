package codelens.backend.ShareableLink.linkEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shareable_links")
public class ShareableLink {

    @Id
    private String id;
    private String token;
    private String documentId;
    private LocalDateTime expiresAt;

    public ShareableLink(String token, String documentId, LocalDateTime expiresAt) {
        this.token = token;
        this.documentId = documentId;
        this.expiresAt = expiresAt;
    }

   public String getId() {
        return id;
   }

   public void setId(String id) {
        this.id = id;
   }

   public String getToken() {
        return token;
   }

   public void setToken(String token) {
        this.token = token;
   }

   public String getDocumentId() {
        return documentId;
   }

   public void setDocumentId(String documentId) {
        this.documentId = documentId;
   }

   public LocalDateTime getExpiresAt() {
        return expiresAt;
   }

   public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
   }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
