package com.mininstagram.like.repository;
import com.mininstagram.like.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.UUID;
public interface PostLikeRepository extends JpaRepository<PostLike,LikeId> {
 @Modifying
 @Query(value="INSERT INTO post_likes (post_id,user_id,created_at) VALUES (:postId,:userId,NOW()) ON CONFLICT (post_id,user_id) DO NOTHING",nativeQuery=true)
 int insertIfAbsent(@Param("postId") UUID postId,@Param("userId") String userId);
 @Modifying
 @Query("DELETE FROM PostLike l WHERE l.id.postId=:postId AND l.id.userId=:userId")
 int remove(@Param("postId") UUID postId,@Param("userId") String userId);
 long countById_PostId(UUID postId);
}
