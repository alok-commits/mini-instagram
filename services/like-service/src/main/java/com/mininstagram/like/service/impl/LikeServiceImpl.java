package com.mininstagram.like.service.impl;
import com.mininstagram.common.OperationLogged;
import com.mininstagram.like.client.PostExistsClient;
import com.mininstagram.like.dto.LikeCountResponse;
import com.mininstagram.like.repository.PostLikeRepository;
import com.mininstagram.like.service.LikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
public class LikeServiceImpl implements LikeService {
 private final PostLikeRepository repository;
 private final PostExistsClient postClient;
 public LikeServiceImpl(PostLikeRepository repository,PostExistsClient postClient){
   this.repository=repository;this.postClient=postClient;
 }
 @Override @Transactional @OperationLogged("LIKE_POST")
 public void like(UUID postId,String subject){
    postClient.assertExists(postId);
    repository.insertIfAbsent(postId,subject);
 }
 @Override @Transactional @OperationLogged("UNLIKE_POST")
 public void unlike(UUID postId,String subject){
    // Unlike is idempotent even if the post was deleted in another service.
    repository.remove(postId,subject);
 }
 @Override @Transactional(readOnly=true) @OperationLogged("COUNT_POST_LIKES")
 public LikeCountResponse count(UUID postId){
    postClient.assertExists(postId);
    return new LikeCountResponse(postId,repository.countById_PostId(postId));
 }
}
