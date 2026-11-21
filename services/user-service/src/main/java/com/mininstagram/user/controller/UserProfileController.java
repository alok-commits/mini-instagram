package com.mininstagram.user.controller;
import com.mininstagram.user.dto.*;
import com.mininstagram.user.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/users")
public class UserProfileController {
 private final UserProfileService service;
 public UserProfileController(UserProfileService service){this.service=service;}
 @GetMapping("/me") public ProfileResponse me(@AuthenticationPrincipal Jwt jwt){
  return service.me(jwt.getSubject(),jwt.getClaimAsString("preferred_username"));
 }
 @PatchMapping("/me") public ProfileResponse update(@AuthenticationPrincipal Jwt jwt,
    @Valid @RequestBody UpdateProfileRequest request){
  return service.update(jwt.getSubject(),jwt.getClaimAsString("preferred_username"),request);
 }
}
