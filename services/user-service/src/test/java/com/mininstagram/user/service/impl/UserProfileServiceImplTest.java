package com.mininstagram.user.service.impl;
import com.mininstagram.user.dto.*;
import com.mininstagram.user.entity.UserProfile;
import com.mininstagram.user.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {
 @Mock UserProfileRepository repository;
 @InjectMocks UserProfileServiceImpl service;
 @Test void createsMinimalProfileOnFirstAccessWithoutDuplicateRows(){
   UserProfile profile=new UserProfile("user-sub","alice");
   when(repository.findByKeycloakUserId("user-sub"))
     .thenReturn(Optional.empty(),Optional.of(profile));
   assertEquals("alice",service.me("user-sub","alice").username());
   verify(repository).insertIfAbsent("user-sub","alice");
 }
 @Test void updateDoesNotAlterIdentity(){
   UserProfile profile=new UserProfile("alice-sub","alice");
   when(repository.findByKeycloakUserId("alice-sub")).thenReturn(Optional.of(profile));
   when(repository.saveAndFlush(profile)).thenReturn(profile);
   ProfileResponse response=service.update("alice-sub","alice",new UpdateProfileRequest("Alice Doe","Hello"));
   assertEquals("alice-sub",response.keycloakUserId());
   assertEquals("Alice Doe",response.displayName());
 }
}
