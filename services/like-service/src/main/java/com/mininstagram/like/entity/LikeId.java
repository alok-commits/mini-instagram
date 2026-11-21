package com.mininstagram.like.entity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;
import java.util.Objects;
@Embeddable
public class LikeId implements Serializable {
 @Column(name="post_id",nullable=false) private UUID postId;
 @Column(name="user_id",nullable=false,length=100) private String userId;
 protected LikeId(){}
 public LikeId(UUID postId,String userId){this.postId=postId;this.userId=userId;}
 public UUID getPostId(){return postId;}
 public String getUserId(){return userId;}
 @Override public boolean equals(Object other) {
   if(this==other)return true;
   if(!(other instanceof LikeId that))return false;
   return Objects.equals(postId,that.postId)&&Objects.equals(userId,that.userId);
 }
 @Override public int hashCode(){return Objects.hash(postId,userId);}
}
