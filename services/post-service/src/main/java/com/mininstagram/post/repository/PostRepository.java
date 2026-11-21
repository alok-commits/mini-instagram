package com.mininstagram.post.repository;
import com.mininstagram.post.entity.Post;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PostRepository extends JpaRepository<Post,UUID> {
 Optional<Post> findByIdAndStatus(UUID id, Post.Status status);
 Page<Post> findAllByStatus(Post.Status status,Pageable pageable);
}
