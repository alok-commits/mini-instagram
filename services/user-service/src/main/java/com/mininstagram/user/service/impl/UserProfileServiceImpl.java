package com.mininstagram.user.service.impl;
import com.mininstagram.common.OperationLogged;
import com.mininstagram.user.dto.*;
import com.mininstagram.user.entity.UserProfile;
import com.mininstagram.user.repository.UserProfileRepository;
import com.mininstagram.user.service.UserProfileService;
import org.springframework.http.HttpStatus;
import com.mininstagram.common.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class UserProfileServiceImpl implements UserProfileService {
 private final UserProfileRepository repository;
 public UserProfileServiceImpl(UserProfileRepository repository){this.repository=repository;}
 private UserProfile getOrCreate(String subject,String preferredUsername){
   var existing=repository.findByKeycloakUserId(subject);
   if(existing.isPresent()) return existing.get();
   // Native Postgres ON CONFLICT handles parallel first-access requests safely.
   String username=preferredUsername==null||preferredUsername.isBlank()||preferredUsername.length()>80
     ? subject : preferredUsername;
   repository.insertIfAbsent(subject,username);
   return repository.findByKeycloakUserId(subject).orElseThrow(()->
     new BusinessException(HttpStatus.CONFLICT,"PROFILE_CONFLICT","Username already in use"));
 }
 @Override @Transactional @OperationLogged("GET_MY_PROFILE")
 public ProfileResponse me(String sub,String username){return ProfileResponse.from(getOrCreate(sub,username));}
 @Override @Transactional @OperationLogged("UPDATE_MY_PROFILE")
 public ProfileResponse update(String sub,String username,UpdateProfileRequest req){
   UserProfile p=getOrCreate(sub,username);
   if(req.displayName()!=null) p.setDisplayName(req.displayName());
   if(req.bio()!=null) p.setBio(req.bio());
   return ProfileResponse.from(repository.saveAndFlush(p));
 }
}
