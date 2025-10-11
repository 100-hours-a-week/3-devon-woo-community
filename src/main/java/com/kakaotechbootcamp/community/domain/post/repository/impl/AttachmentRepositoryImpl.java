package com.kakaotechbootcamp.community.domain.post.repository.impl;

import com.kakaotechbootcamp.community.domain.post.entity.Attachment;
import com.kakaotechbootcamp.community.domain.post.repository.AttachmentRepository;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepositoryImpl;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Attachment Repository 구현체
 */
@Repository
public class AttachmentRepositoryImpl extends CustomJpaRepositoryImpl<Attachment, Long> implements
        AttachmentRepository {

    public AttachmentRepositoryImpl() {
        super(Attachment::getId);
    }

    @Override
    public Optional<Attachment> findByPostId(Long postId) {
        return findAll().stream()
                .filter(attachment -> attachment.getPostId().equals(postId))
                .findFirst();
    }
}