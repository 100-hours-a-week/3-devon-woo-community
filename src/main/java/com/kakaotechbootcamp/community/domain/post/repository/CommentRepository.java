package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.id = :commentId")
    Optional<Comment> findByIdWithMember(@Param("commentId") Long commentId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.member.id = :memberId ORDER BY c.createdAt DESC")
    List<Comment> findByMemberIdWithPost(@Param("memberId") Long memberId);
}
