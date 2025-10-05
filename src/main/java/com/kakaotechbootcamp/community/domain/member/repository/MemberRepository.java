package com.kakaotechbootcamp.community.domain.member.repository;

import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepository;

import java.util.Optional;

public interface MemberRepository extends CustomJpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
