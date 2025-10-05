package com.kakaotechbootcamp.community.domain.post.repository;

import com.kakaotechbootcamp.community.domain.post.entity.Attachment;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepository;
import java.util.Optional;

public interface AttachmentRepository extends CustomJpaRepository<Attachment, Long> {

    Optional<Attachment> findByPostId(Long postId);

}
