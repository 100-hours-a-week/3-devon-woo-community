package com.kakaotechbootcamp.community.domain.member;

import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PersistenceContextVsJpqlTest {

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("영속성 컨텍스트로 닉네임을 '로키'로 변경 후, JPQL로 '토르'로 변경하면 DB에는 어떻게 저장될까?")
    void testNicknameUpdate() {
        // Given: 초기 Member 생성 및 저장
        Member member = Member.create(
                "test@example.com",
                "password123",
                "아이언맨"
        );
        memberRepository.save(member);
        entityManager.flush(); // DB에 반영
        entityManager.clear(); // 영속성 컨텍스트 초기화

        Long memberId = member.getId();

        // When: 1단계 - 영속성 컨텍스트로 닉네임을 '로키'로 변경
        Member foundMember = memberRepository.findById(memberId).orElseThrow();
        System.out.println("조회 직후 닉네임: " + foundMember.getNickname());

        foundMember.changeNickname("로키");
        System.out.println("영속성 컨텍스트에서 변경 후: " + foundMember.getNickname());

        // When: 2단계 - JPQL로 닉네임을 '토르'로 변경 (영속성 컨텍스트를 거치지 않고 DB에 직접 반영)
        entityManager.createQuery(
                "UPDATE Member m SET m.nickname = :nickname WHERE m.id = :id")
                .setParameter("nickname", "토르")
                .setParameter("id", memberId)
                .executeUpdate();

        // Then: 영속성 컨텍스트와 DB의 상태 확인
        System.out.println("영속성 컨텍스트의 닉네임: " + foundMember.getNickname());

        String nicknameFromDb = (String) entityManager.createQuery(
                "SELECT m.nickname FROM Member m WHERE m.id = :id")
                .setParameter("id", memberId)
                .getSingleResult();
        System.out.println("DB에 저장된 닉네임: " + nicknameFromDb);

        entityManager.clear();
        Member reloadedMember = memberRepository.findById(memberId).orElseThrow();
        System.out.println("영속성 컨텍스트 clear 후 재조회: " + reloadedMember.getNickname());

        // Assertion
        assertThat(reloadedMember.getNickname()).isEqualTo("토르");
    }

    @Test
    @DisplayName("영속성 컨텍스트 변경 후 flush하고, 그 다음 JPQL로 변경하면?")
    void testNicknameUpdateWithFlush() {
        // Given
        Member member = Member.create(
                "test2@example.com",
                "password123",
                "헐크"
        );
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        Long memberId = member.getId();

        // When: 1단계 - 영속성 컨텍스트로 '로키'로 변경 + flush
        Member foundMember = memberRepository.findById(memberId).orElseThrow();
        foundMember.changeNickname("로키");
        entityManager.flush(); // DB에 반영!
        System.out.println("flush 후 영속성 컨텍스트 닉네임: " + foundMember.getNickname());

        // When: 2단계 - JPQL로 '토르'로 변경
        entityManager.createQuery(
                "UPDATE Member m SET m.nickname = :nickname WHERE m.id = :id")
                .setParameter("nickname", "토르")
                .setParameter("id", memberId)
                .executeUpdate();

        // Then: 결과 확인 (주의! 영속성 컨텍스트에는 여전히 '로키'가 남아있음)
        System.out.println("JPQL 실행 후 영속성 컨텍스트의 닉네임: " + foundMember.getNickname());

        entityManager.clear();
        Member reloadedMember = memberRepository.findById(memberId).orElseThrow();
        System.out.println("영속성 컨텍스트 clear 후 재조회: " + reloadedMember.getNickname());

        assertThat(reloadedMember.getNickname()).isEqualTo("토르");
    }

    @Test
    @DisplayName("FlushMode.COMMIT으로 설정하면 JPQL 실행 시 자동 flush가 일어나지 않는다!")
    void testNicknameUpdateWithFlushModeCommit() {
        // Given: 초기 Member 생성 및 저장
        Member member = Member.create(
                "test3@example.com",
                "password123",
                "캡틴아메리카"
        );
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        Long memberId = member.getId();

        // When: 1단계 - 영속성 컨텍스트로 닉네임을 '로키'로 변경
        Member foundMember = memberRepository.findById(memberId).orElseThrow();
        foundMember.changeNickname("로키");
        System.out.println("영속성 컨텍스트에서 변경 후: " + foundMember.getNickname());

        // When: 2단계 - FlushMode를 COMMIT으로 설정하고 JPQL로 '토르'로 변경 (JPQL 실행 시 자동 flush가 일어나지 않음)
        entityManager.setFlushMode(FlushModeType.COMMIT);
        System.out.println("FlushMode: " + entityManager.getFlushMode());

        entityManager.createQuery(
                "UPDATE Member m SET m.nickname = :nickname WHERE m.id = :id")
                .setParameter("nickname", "토르")
                .setParameter("id", memberId)
                .executeUpdate();

        // Then: 영속성 컨텍스트와 DB의 상태 확인
        System.out.println("영속성 컨텍스트의 닉네임: " + foundMember.getNickname());

        String nicknameFromDb = (String) entityManager.createQuery(
                "SELECT m.nickname FROM Member m WHERE m.id = :id")
                .setParameter("id", memberId)
                .getSingleResult();
        System.out.println("DB에 저장된 닉네임: " + nicknameFromDb);

        entityManager.clear();
        Member reloadedMember = memberRepository.findById(memberId).orElseThrow();
        System.out.println("영속성 컨텍스트 clear 후 재조회: " + reloadedMember.getNickname());

        // Assertion - 여기서는 토르가 저장됨 (로키는 clear되어 사라졌으므로)
        assertThat(reloadedMember.getNickname()).isEqualTo("토르");
    }

    @Test
    @DisplayName("FlushMode.COMMIT - clear 없이 트랜잭션 커밋까지 가면 어떻게 될까?")
    void testNicknameUpdateWithFlushModeCommitUntilCommit() {
        // Given: 초기 Member 생성 및 저장
        Member member = Member.create(
                "test4@example.com",
                "password123",
                "블랙위도우"
        );
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        Long memberId = member.getId();

        // When: 1단계 - 영속성 컨텍스트로 닉네임을 '로키'로 변경
        Member foundMember = memberRepository.findById(memberId).orElseThrow();
        System.out.println("조회 직후 닉네임: " + foundMember.getNickname());

        foundMember.changeNickname("로키");
        System.out.println("영속성 컨텍스트에서 변경 후: " + foundMember.getNickname());

        // When: 2단계 - FlushMode를 COMMIT으로 설정하고 JPQL로 '토르'로 변경
        entityManager.setFlushMode(FlushModeType.COMMIT);
        System.out.println("FlushMode: " + entityManager.getFlushMode());

        entityManager.createQuery(
                "UPDATE Member m SET m.nickname = :nickname WHERE m.id = :id")
                .setParameter("nickname", "토르")
                .setParameter("id", memberId)
                .executeUpdate();

        // Then: 중간 상태 확인 (clear 하지 않음!)
        System.out.println("영속성 컨텍스트의 닉네임: " + foundMember.getNickname());

        String nicknameFromDbBeforeCommit = (String) entityManager.createQuery(
                "SELECT m.nickname FROM Member m WHERE m.id = :id")
                .setParameter("id", memberId)
                .getSingleResult();
        System.out.println("현재 DB의 닉네임: " + nicknameFromDbBeforeCommit);

        // 트랜잭션 커밋 시뮬레이션 (flush 명시 호출)
        entityManager.flush();
        System.out.println("flush 후 완료!");

        // 커밋 후 DB 상태 확인
        String nicknameFromDbAfterFlush = (String) entityManager.createQuery(
                "SELECT m.nickname FROM Member m WHERE m.id = :id")
                .setParameter("id", memberId)
                .getSingleResult();
        System.out.println("flush 후 DB의 닉네임: " + nicknameFromDbAfterFlush);

        // Assertion - 최종적으로는 로키가 저장됨!
        assertThat(nicknameFromDbAfterFlush).isEqualTo("로키");
    }
}
