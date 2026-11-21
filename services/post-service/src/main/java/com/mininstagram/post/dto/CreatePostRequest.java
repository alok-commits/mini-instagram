package com.mininstagram.post.dto;
import jakarta.validation.constraints.*;
public record CreatePostRequest(@NotBlank @Size(max=2200) String caption,
 @Size(max=2048) @Pattern(regexp="https://.+",message="Image URL must use HTTPS") String imageUrl) {}
