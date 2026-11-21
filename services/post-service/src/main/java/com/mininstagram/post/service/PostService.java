package com.mininstagram.post.service;
import com.mininstagram.common.PageEnvelope;
import com.mininstagram.post.dto.*;
import java.util.UUID;
public interface PostService {
 PostResponse create(String userId,CreatePostRequest request);
 PageEnvelope<PostResponse> list(int page,int size);
 PostResponse get(UUID id);
 PostResponse update(UUID id,String userId,UpdatePostRequest request);
 void delete(UUID id,String userId);
 boolean exists(UUID id);
}
