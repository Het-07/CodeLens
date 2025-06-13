package codelens.backend.ShareableLink.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import codelens.backend.util.HttpStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import codelens.backend.ShareableLink.linkEntity.ShareableLink;
import codelens.backend.ShareableLink.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import codelens.backend.ShareableLink.responseEntity.accessLinkResponse;
import lombok.SneakyThrows;

@Service
public class LinkServiceImpl implements LinkService {

    private final LinkRepository linkRepository;
    private final int EXPIRY_TIME = 12;

    @Value("${backend.url}")
    private String BACKEND_URL;

    @Autowired
    public LinkServiceImpl(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    /**
     * Generates a new shareable link for the given document ID.
     *
     * @param documentId The ID of the document to generate a shareable link for.
     * @return The URL of the generated shareable link.
     * @throws IllegalArgumentException If the document ID is null or empty.
     */
    @Override
    public String generateShareableLink(String documentId) {
        if (documentId == null || documentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Document ID cannot be empty or null");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(EXPIRY_TIME); // expiry time (12-hours)

        ShareableLink shareableLink = new ShareableLink(token, documentId, expiryTime);
        linkRepository.save(shareableLink);

        return accessURL(token);
    }

    /**
     * Retrieves a shareable link by its token.
     *
     * @param token The token associated with the shareable link.
     * @return An Optional containing the shareable link if found and not expired, otherwise empty.
     */
    @Override
    public Optional<ShareableLink> getShareableLink(String token) {
        Optional<ShareableLink> link = linkRepository.findByToken(token);

        if (link.isPresent() && !link.get().isExpired()) {
            return link;
        }
        return Optional.empty();
    }

    /**
     * Constructs the URL to access the shareable link using the token.
     *
     * @param token The token associated with the shareable link.
     * @return The URL for accessing the shareable link.
     */

    @Override
    @SneakyThrows
    public accessLinkResponse accessShareableLink (String token) {
        Optional<ShareableLink> shareableLinkOpt = this.getShareableLink(token);

        if (shareableLinkOpt.isPresent()) {
            ShareableLink shareableLink = shareableLinkOpt.get();

            if (shareableLink.getExpiresAt().isBefore(LocalDateTime.now())) {
                 throw new IllegalAccessException("Link has expired please try again");
            }

            String documentId = shareableLink.getDocumentId();
            String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/download")
                    .queryParam("messageId", documentId)
                    .toUriString();

            return accessLinkResponse.builder()
                    .url(redirectUrl)
                    .body(Map.of(
                            "message", "downloaded successfully",
                            "redirectedFrom", "/link/access/" + token
                    ))
                    .timestamp(new Date().toString())
                    .statusCode(HttpStatusCode.MOVED_PERMANENTLY)
                    .build();
        } else {
            throw new NoSuchElementException("The Link is not valid, Please check your link.");
        }
    }

    private String accessURL(String token) {
        String URL = BACKEND_URL + "/link/access/%s";
        return String.format(URL, token);
    }

}
