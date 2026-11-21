package com.mininstagram.like.controller;
import com.mininstagram.like.dto.LikeCountResponse;
import com.mininstagram.like.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping("/api/v1/posts/{postId}/likes")
public class LikeController {
 private final LikeService service;
 public LikeController(LikeService service){this.service=service;}
 @PutMapping("/me") @ResponseStatus(HttpStatus.NO_CONTENT)
 public void like(@PathVariable UUID postId,@AuthenticationPrincipal Jwt jwt){
   service.like(postId,jwt.getSubject());
 }
 @DeleteMapping("/me") @ResponseStatus(HttpStatus.NO_CONTENT)
 public void unlike(@PathVariable UUID postId,@AuthenticationPrincipal Jwt jwt){
   service.unlike(postId,jwt.getSubject());
 }
 @GetMapping("/count") public LikeCountResponse count(@PathVariable UUID postId){return service.count(postId);}
}
