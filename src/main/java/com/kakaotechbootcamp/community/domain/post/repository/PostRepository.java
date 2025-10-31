package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Fetch Join 사용
    @Query("SELECT p FROM Post p JOIN FETCH p.author")
    List<Post> findAllWithAuthorByFetchJoin();

    // EntityGraph 사용
    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT p FROM Post p")
    List<Post> findAllWithAuthorByEntityGraph();
}
