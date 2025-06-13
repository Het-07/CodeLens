package codelens.backend.ShareableLink.service;

import codelens.backend.ShareableLink.linkEntity.ShareableLink;
import codelens.backend.ShareableLink.responseEntity.accessLinkResponse;

import java.util.Optional;

public interface LinkService {
    String generateShareableLink(String documentId);
    Optional<ShareableLink> getShareableLink(String token);
    accessLinkResponse accessShareableLink (String token);
}
