package com.mininstagram.post.controller;
import com.mininstagram.common.PageEnvelope;
import com.mininstagram.post.dto.*;
import com.mininstagram.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;
@RestController @RequestMapping("/api/v1/posts")
public class PostController {
 private final PostService service;
 public PostController(PostService service){this.service=service;}
 @PostMapping public ResponseEntity<PostResponse> create(@AuthenticationPrincipal Jwt jwt,
    @Valid @RequestBody CreatePostRequest request){
   PostResponse created=service.create(jwt.getSubject(),request);
   return ResponseEntity.created(URI.create("/api/v1/posts/"+created.id())).body(created);
 }
 @GetMapping public PageEnvelope<PostResponse> list(@RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="20") int size){return service.list(page,size);}
 @GetMapping("/{id}") public PostResponse get(@PathVariable UUID id){return service.get(id);}
 @PatchMapping("/{id}") public PostResponse update(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt,
    @Valid @RequestBody UpdatePostRequest request){return service.update(id,jwt.getSubject(),request);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
 public void delete(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt){service.delete(id,jwt.getSubject());}
}
