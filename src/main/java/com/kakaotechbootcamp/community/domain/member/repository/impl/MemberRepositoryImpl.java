package com.kakaotechbootcamp.community.domain.member.repository.impl;

import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepositoryImpl;
import org.springframework.stereotype.Repository;

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
}