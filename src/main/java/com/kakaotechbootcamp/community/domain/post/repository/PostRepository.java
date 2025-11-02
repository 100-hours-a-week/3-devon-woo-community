package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {

    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.id = :id")
    Optional<Post> findByIdWithMember(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    int incrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId AND p.likeCount > 0")
    int decrementLikeCount(@Param("postId") Long postId);

}
