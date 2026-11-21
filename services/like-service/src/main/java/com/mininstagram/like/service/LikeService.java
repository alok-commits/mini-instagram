package com.mininstagram.like.service;
import com.mininstagram.like.dto.LikeCountResponse;
import java.util.UUID;
public interface LikeService {
 void like(UUID postId,String subject);
 void unlike(UUID postId,String subject);
 LikeCountResponse count(UUID postId);
}
