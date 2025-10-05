package com.kakaotechbootcamp.community.domain.member.repository;

import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.infra.repository.CustomJpaRepository;


public interface MemberRepository extends CustomJpaRepository<Member, Long> {

}
