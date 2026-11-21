package com.mininstagram.post.entity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="posts",indexes={@Index(name="idx_posts_author_created",columnList="author_id,created_at")})
public class Post {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(name="author_id",nullable=false,updatable=false,length=100) private String authorId;
 @Column(nullable=false,length=2200) private String caption;
 @Column(name="image_url",length=2048) private String imageUrl;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Status status=Status.ACTIVE;
 @Version @Column(nullable=false) private long version;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 public enum Status {ACTIVE,DELETED}
 protected Post(){}
 public Post(String authorId,String caption,String imageUrl) {
  this.authorId=authorId; this.caption=caption; this.imageUrl=imageUrl;
 }
 @PrePersist void creation(){createdAt=Instant.now();updatedAt=createdAt;}
 @PreUpdate void updateTime(){updatedAt=Instant.now();}
 public UUID getId(){return id;}
 public String getAuthorId(){return authorId;}
 public String getCaption(){return caption;}
 public String getImageUrl(){return imageUrl;}
 public Status getStatus(){return status;}
 public Instant getCreatedAt(){return createdAt;}
 public Instant getUpdatedAt(){return updatedAt;}
 public long getVersion(){return version;}
 public void edit(String caption,String imageUrl){
  if(caption!=null) this.caption=caption;
  if(imageUrl!=null) this.imageUrl=imageUrl;
 }
 public void delete(){status=Status.DELETED;}
}
