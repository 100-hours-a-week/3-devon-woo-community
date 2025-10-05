package com.kakaotechbootcamp.community.domain.member.repository.impl;

import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepositoryImpl;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Member Repository 구현체
 * SimpleJpaRepositoryImpl을 상속
 */
@Repository
public class MemberRepositoryImpl extends CustomJpaRepositoryImpl<Member, Long> implements MemberRepository {

    public MemberRepositoryImpl() {
        super(
                Member::getId,
                (member, id) -> {
                    if (member.getId() != null) {
                        return member;
                    }
                    return member.withId(id);
                }
        );
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return findAll().stream()
                .filter(member -> member.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return findAll().stream()
                .anyMatch(member -> member.getNickname().equals(nickname));
    }
}