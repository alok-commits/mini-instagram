package com.mininstagram.post.controller;
import com.mininstagram.post.service.PostService;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
/** Internal route: only service token azp=like-service; never registered in gateway. */
@RestController @RequestMapping("/internal/posts")
public class InternalPostController {
 private final PostService service;
 public InternalPostController(PostService service){this.service=service;}
 @GetMapping("/{id}/exists") public boolean exists(@PathVariable UUID id){return service.exists(id);}
}
