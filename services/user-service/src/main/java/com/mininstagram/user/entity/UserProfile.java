package com.mininstagram.user.entity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="user_profiles")
public class UserProfile {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(name="keycloak_user_id",nullable=false,unique=true,updatable=false,length=100) private String keycloakUserId;
 @Column(nullable=false,unique=true,length=80) private String username;
 @Column(name="display_name",nullable=false,length=120) private String displayName;
 @Column(length=300) private String bio="";
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected UserProfile() { }
 public UserProfile(String subject,String username) {
   this.keycloakUserId=subject; this.username=username; this.displayName=username;
 }
 @PrePersist void creation(){createdAt=Instant.now();updatedAt=createdAt;}
 @PreUpdate void updateTime(){updatedAt=Instant.now();}
 public UUID getId(){return id;}
 public String getKeycloakUserId(){return keycloakUserId;}
 public String getUsername(){return username;}
 public String getDisplayName(){return displayName;}
 public String getBio(){return bio;}
 public Instant getCreatedAt(){return createdAt;}
 public Instant getUpdatedAt(){return updatedAt;}
 public void setDisplayName(String name){displayName=name;}
 public void setBio(String value){bio=value;}
}
