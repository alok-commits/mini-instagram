package com.mininstagram.like.service.impl;
import com.mininstagram.like.client.PostExistsClient;
import com.mininstagram.like.repository.PostLikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
 @Mock PostLikeRepository repository;
 @Mock PostExistsClient postClient;
 @InjectMocks LikeServiceImpl service;
 @Test void likeDelegatesToAtomicInsert(){
   UUID post=UUID.randomUUID();service.like(post,"subject-1");
   verify(postClient).assertExists(post);
   verify(repository).insertIfAbsent(post,"subject-1");
 }
 @Test void unlikeIsSafeEvenForDeletedPost(){
   UUID post=UUID.randomUUID();service.unlike(post,"subject-1");
   verify(repository).remove(post,"subject-1");
   verifyNoInteractions(postClient);
 }
 @Test void countsRequireAnActivePost(){
   UUID post=UUID.randomUUID();when(repository.countById_PostId(post)).thenReturn(3L);
   assertEquals(3,service.count(post).count());verify(postClient).assertExists(post);
 }
}
