package com.mininstagram.user.repository;
import com.mininstagram.user.entity.UserProfile;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface UserProfileRepository extends JpaRepository<UserProfile,UUID> {
 Optional<UserProfile> findByKeycloakUserId(String subject);
 @Modifying
 @Query(value="INSERT INTO user_profiles(id,keycloak_user_id,username,display_name,bio,created_at,updated_at) " +
  "VALUES (gen_random_uuid(),:subject,:username,:username,'',NOW(),NOW()) ON CONFLICT DO NOTHING",nativeQuery=true)
 int insertIfAbsent(@Param("subject") String subject,@Param("username") String username);
}
