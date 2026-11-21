package com.mininstagram.like.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="post_likes")
public class PostLike {
 @EmbeddedId private LikeId id;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 protected PostLike(){}
 public PostLike(LikeId id){this.id=id;this.createdAt=Instant.now();}
 public LikeId getId(){return id;}
 public Instant getCreatedAt(){return createdAt;}
}
