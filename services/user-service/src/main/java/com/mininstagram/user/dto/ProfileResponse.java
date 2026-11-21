package com.mininstagram.user.dto;
import com.mininstagram.user.entity.UserProfile;
import java.time.Instant;
import java.util.UUID;
public record ProfileResponse(UUID id,String keycloakUserId,String username,String displayName,
                              String bio,Instant createdAt,Instant updatedAt) {
 public static ProfileResponse from(UserProfile p){return new ProfileResponse(p.getId(),p.getKeycloakUserId(),
    p.getUsername(),p.getDisplayName(),p.getBio(),p.getCreatedAt(),p.getUpdatedAt());}
}
