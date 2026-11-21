package com.mininstagram.post.service.impl;
import com.mininstagram.common.BusinessException;
import com.mininstagram.post.dto.*;
import com.mininstagram.post.entity.Post;
import com.mininstagram.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {
 @Mock PostRepository repository;
 @InjectMocks PostServiceImpl service;
 @Test void ownerCannotModifySomeoneElsesPost(){
   UUID id=UUID.randomUUID();
   when(repository.findByIdAndStatus(id,Post.Status.ACTIVE)).thenReturn(Optional.of(new Post("owner","hello",null)));
   BusinessException error=assertThrows(BusinessException.class,
      ()->service.update(id,"intruder",new UpdatePostRequest("hacked",null)));
   assertEquals("NOT_POST_OWNER",error.errorCode());
   verify(repository,never()).saveAndFlush(any());
 }
 @Test void deletedPostIsUnavailable(){
   UUID id=UUID.randomUUID();
   when(repository.findByIdAndStatus(id,Post.Status.ACTIVE)).thenReturn(Optional.empty());
   BusinessException ex=assertThrows(BusinessException.class,()->service.get(id));
   assertEquals("POST_NOT_FOUND",ex.errorCode());
 }
 @Test void invalidPaginationRejected(){
   assertThrows(BusinessException.class,()->service.list(-1,20));
   assertThrows(BusinessException.class,()->service.list(0,101));
 }
}
