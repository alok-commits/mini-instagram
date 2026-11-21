package com.mininstagram.post.dto;
import com.mininstagram.post.entity.Post;
import java.time.Instant;
import java.util.UUID;
public record PostResponse(UUID id,String authorId,String caption,String imageUrl,
    Instant createdAt,Instant updatedAt,long version){
 public static PostResponse from(Post p){return new PostResponse(p.getId(),p.getAuthorId(),
  p.getCaption(),p.getImageUrl(),p.getCreatedAt(),p.getUpdatedAt(),p.getVersion());}
}
