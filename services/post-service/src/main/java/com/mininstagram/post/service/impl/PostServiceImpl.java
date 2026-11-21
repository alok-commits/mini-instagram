package com.mininstagram.post.service.impl;
import com.mininstagram.common.*;
import com.mininstagram.post.dto.*;
import com.mininstagram.post.entity.Post;
import com.mininstagram.post.repository.PostRepository;
import com.mininstagram.post.service.PostService;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
public class PostServiceImpl implements PostService {
 private final PostRepository repository;
 public PostServiceImpl(PostRepository repository){this.repository=repository;}
 private Post active(UUID id){return repository.findByIdAndStatus(id,Post.Status.ACTIVE)
    .orElseThrow(()->new BusinessException(HttpStatus.NOT_FOUND,"POST_NOT_FOUND","Post not found"));}
 private void requireOwner(Post p,String userId){
  if(!p.getAuthorId().equals(userId))
   throw new BusinessException(HttpStatus.FORBIDDEN,"NOT_POST_OWNER","You cannot modify this post");
 }
 @Override @Transactional @OperationLogged("CREATE_POST")
 public PostResponse create(String userId,CreatePostRequest request){
   return PostResponse.from(repository.saveAndFlush(new Post(userId,request.caption(),request.imageUrl())));
 }
 @Override @Transactional(readOnly=true) @OperationLogged("LIST_POSTS")
 public PageEnvelope<PostResponse> list(int page,int size){
   if(page<0||size<1||size>100) throw new BusinessException(HttpStatus.BAD_REQUEST,"INVALID_PAGE","Invalid pagination parameters");
   var sort=Sort.by(Sort.Order.desc("createdAt"),Sort.Order.desc("id"));
   return PageEnvelope.from(repository.findAllByStatus(Post.Status.ACTIVE,PageRequest.of(page,size,sort)).map(PostResponse::from));
 }
 @Override @Transactional(readOnly=true) @OperationLogged("GET_POST")
 public PostResponse get(UUID id){return PostResponse.from(active(id));}
 @Override @Transactional @OperationLogged("UPDATE_POST")
 public PostResponse update(UUID id,String userId,UpdatePostRequest request){
   Post p=active(id); requireOwner(p,userId);
   p.edit(request.caption(),request.imageUrl());
   return PostResponse.from(repository.saveAndFlush(p));
 }
 @Override @Transactional @OperationLogged("DELETE_POST")
 public void delete(UUID id,String userId){Post p=active(id);requireOwner(p,userId);p.delete();repository.saveAndFlush(p);}
 @Override @Transactional(readOnly=true)
 public boolean exists(UUID id){active(id);return true;}
}
