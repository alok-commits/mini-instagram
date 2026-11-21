package com.mininstagram.like.dto;
import java.util.UUID;
public record LikeCountResponse(UUID postId,long count) {}
