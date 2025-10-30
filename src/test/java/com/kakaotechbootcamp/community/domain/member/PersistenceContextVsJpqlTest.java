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
                "아이언맨",
                "http://example.com/profile.jpg"
        );
        memberRepository.save(member);
        entityManager.flush(); // DB에 반영
        entityManager.clear(); // 영속성 컨텍스트 초기화

        Long memberId = member.getId();
        System.out.println("\n========== 초기 상태 ==========");
        System.out.println("저장된 Member ID: " + memberId);
        System.out.println("초기 닉네임: 아이언맨");

        // When: 1단계 - 영속성 컨텍스트로 닉네임을 '로키'로 변경
        System.out.println("\n========== 1단계: 영속성 컨텍스트로 '로키'로 변경 ==========");
        Member foundMember = memberRepository.findById(memberId).orElseThrow();
        System.out.println("조회 직후 닉네임: " + foundMember.getNickname());

        foundMember.updateNickname("로키");
        System.out.println("영속성 컨텍스트에서 변경 후: " + foundMember.getNickname());
        System.out.println("(아직 flush 하지 않음 - DB에는 반영 안됨)");

        // When: 2단계 - JPQL로 닉네임을 '토르'로 변경
        System.out.println("\n========== 2단계: JPQL로 '토르'로 변경 ==========");
        int updatedCount = entityManager.createQuery(
                "UPDATE Member m SET m.nickname = :nickname WHERE m.id = :id")
                .setParameter("nickname", "토르")
                .setParameter("id", memberId)
                .executeUpdate();

        System.out.println("JPQL 업데이트 영향받은 행 수: " + updatedCount);
        System.out.println("JPQL은 영속성 컨텍스트를 거치지 않고 DB에 직접 반영됩니다!");

        // Then: 영속성 컨텍스트와 DB의 상태 확인
        System.out.println("\n========== 결과 확인 ==========");

        // 영속성 컨텍스트에 있는 엔티티 확인 (flush 전)
        System.out.println("영속성 컨텍스트의 닉네임: " + foundMember.getNickname());

        // DB 직접 조회 (JPQL)
        String nicknameFromDb = (String) entityManager.createQuery(
                "SELECT m.nickname FROM Member m WHERE m.id = :id")
                .setParameter("id", memberId)
                .getSingleResult();
        System.out.println("DB에 저장된 닉네임 (JPQL 조회): " + nicknameFromDb);

        // 영속성 컨텍스트 clear 후 다시 조회
        entityManager.clear();
        Member reloadedMember = memberRepository.findById(memberId).orElseThrow();
        System.out.println("영속성 컨텍스트 clear 후 재조회한 닉네임: " + reloadedMember.getNickname());

        System.out.println("\n========== 결론 ==========");
        System.out.println("1. 영속성 컨텍스트의 '로키'는 flush되지 않아 DB에 반영되지 않음");
        System.out.println("2. JPQL의 '토르'는 영속성 컨텍스트를 우회하여 DB에 직접 반영됨");
        System.out.println("3. 최종 DB 값: " + reloadedMember.getNickname());

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
                "헐크",
                "http://example.com/profile2.jpg"
        );
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        Long memberId = member.getId();
        System.out.println("\n========== 초기 상태 ==========");
        System.out.println("초기 닉네임: 헐크");

        // When: 1단계 - 영속성 컨텍스트로 '로키'로 변경 + flush
        System.out.println("\n========== 1단계: 영속성 컨텍스트로 '로키'로 변경 + flush ==========");
        Member foundMember = memberRepository.findById(memberId).orElseThrow();
        foundMember.updateNickname("로키");
        entityManager.flush(); // DB에 반영!
        System.out.println("flush 후 영속성 컨텍스트 닉네임: " + foundMember.getNickname());

        // When: 2단계 - JPQL로 '토르'로 변경
        System.out.println("\n========== 2단계: JPQL로 '토르'로 변경 ==========");
        entityManager.createQuery(
                "UPDATE Member m SET m.nickname = :nickname WHERE m.id = :id")
                .setParameter("nickname", "토르")
                .setParameter("id", memberId)
                .executeUpdate();

        // Then
        System.out.println("\n========== 결과 확인 ==========");
        System.out.println("주의! 영속성 컨텍스트에는 여전히 '로키'가 남아있습니다: " + foundMember.getNickname());

        entityManager.clear();
        Member reloadedMember = memberRepository.findById(memberId).orElseThrow();
        System.out.println("영속성 컨텍스트 clear 후 재조회: " + reloadedMember.getNickname());

        System.out.println("\n========== 결론 ==========");
        System.out.println("1. 영속성 컨텍스트 변경 + flush → DB에 '로키' 반영됨");
        System.out.println("2. JPQL 업데이트 → DB를 '토르'로 덮어씀");
        System.out.println("3. 하지만 영속성 컨텍스트는 '로키'를 유지 (동기화 안됨!)");
        System.out.println("4. 영속성 컨텍스트를 clear해야 정확한 값 조회 가능");
        System.out.println("5. 최종 DB 값: " + reloadedMember.getNickname());

        assertThat(reloadedMember.getNickname()).isEqualTo("토르");
    }

    @Test
    @DisplayName("FlushMode.COMMIT으로 설정하면 JPQL 실행 시 자동 flush가 일어나지 않는다!")
    void testNicknameUpdateWithFlushModeCommit() {
        // Given: 초기 Member 생성 및 저장
        Member member = Member.create(
                "test3@example.com",
                "password123",
                "캡틴아메리카",
                "http://example.com/profile3.jpg"
        );
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        Long memberId = member.getId();
        System.out.println("\n========== 초기 상태 ==========");
        System.out.println("저장된 Member ID: " + memberId);
        System.out.println("초기 닉네임: 캡틴아메리카");
        System.out.println("현재 FlushMode: " + entityManager.getFlushMode());

        // When: 1단계 - 영속성 컨텍스트로 닉네임을 '로키'로 변경
        System.out.println("\n========== 1단계: 영속성 컨텍스트로 '로키'로 변경 ==========");
        Member foundMember = memberRepository.findById(memberId).orElseThrow();
        System.out.println("조회 직후 닉네임: " + foundMember.getNickname());

        foundMember.updateNickname("로키");
        System.out.println("영속성 컨텍스트에서 변경 후: " + foundMember.getNickname());
        System.out.println("(아직 flush 하지 않음 - DB에는 반영 안됨)");

        // When: 2단계 - FlushMode를 COMMIT으로 설정하고 JPQL로 '토르'로 변경
        System.out.println("\n========== 2단계: FlushMode.COMMIT으로 설정 후 JPQL로 '토르'로 변경 ==========");
        System.out.println("FlushMode를 COMMIT으로 변경합니다...");
        entityManager.setFlushMode(FlushModeType.COMMIT);
        System.out.println("변경된 FlushMode: " + entityManager.getFlushMode());
        System.out.println("이제 JPQL 실행 시 자동 flush가 일어나지 않습니다!");


        int updatedCount = entityManager.createQuery(
                "UPDATE Member m SET m.nickname = :nickname WHERE m.id = :id")
                .setParameter("nickname", "토르")
                .setParameter("id", memberId)
                .executeUpdate();

        // DB 직접 조회 (JPQL)
        String nicknameFromDb2 = (String) entityManager.createQuery(
                        "SELECT m.nickname FROM Member m WHERE m.id = :id")
                .setParameter("id", memberId)
                .getSingleResult();
        System.out.println("DB에 저장된 닉네임 (JPQL 조회): " + nicknameFromDb2);

        System.out.println("JPQL 업데이트 영향받은 행 수: " + updatedCount);

        // Then: 영속성 컨텍스트와 DB의 상태 확인
        System.out.println("\n========== 결과 확인 ==========");

        // 영속성 컨텍스트에 있는 엔티티 확인
        System.out.println("영속성 컨텍스트의 닉네임: " + foundMember.getNickname());

        // DB 직접 조회 (JPQL)
        String nicknameFromDb = (String) entityManager.createQuery(
                "SELECT m.nickname FROM Member m WHERE m.id = :id")
                .setParameter("id", memberId)
                .getSingleResult();
        System.out.println("DB에 저장된 닉네임 (JPQL 조회): " + nicknameFromDb);

        // 영속성 컨텍스트 clear 후 다시 조회
        entityManager.clear();
        Member reloadedMember = memberRepository.findById(memberId).orElseThrow();
        System.out.println("영속성 컨텍스트 clear 후 재조회한 닉네임: " + reloadedMember.getNickname());

        System.out.println("\n========== 결론 ==========");
        System.out.println("1. FlushMode.COMMIT으로 설정하면 JPQL 실행 시 자동 flush가 일어나지 않음");
        System.out.println("2. 영속성 컨텍스트의 '로키'는 DB에 반영되지 않음");
        System.out.println("3. JPQL의 '토르'가 DB에 직접 반영됨");
        System.out.println("4. 최종 DB 값: " + reloadedMember.getNickname());
        System.out.println("5. 영속성 컨텍스트의 변경사항은 트랜잭션 커밋 시점에 flush됨");

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
                "블랙위도우",
                "http://example.com/profile4.jpg"
        );
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        Long memberId = member.getId();
        System.out.println("\n========== 초기 상태 ==========");
        System.out.println("저장된 Member ID: " + memberId);
        System.out.println("초기 닉네임: 블랙위도우");
        System.out.println("현재 FlushMode: " + entityManager.getFlushMode());

        // When: 1단계 - 영속성 컨텍스트로 닉네임을 '로키'로 변경
        System.out.println("\n========== 1단계: 영속성 컨텍스트로 '로키'로 변경 ==========");
        Member foundMember = memberRepository.findById(memberId).orElseThrow();
        System.out.println("조회 직후 닉네임: " + foundMember.getNickname());

        foundMember.updateNickname("로키");
        System.out.println("영속성 컨텍스트에서 변경 후: " + foundMember.getNickname());

        // When: 2단계 - FlushMode를 COMMIT으로 설정하고 JPQL로 '토르'로 변경
        System.out.println("\n========== 2단계: FlushMode.COMMIT으로 설정 후 JPQL로 '토르'로 변경 ==========");
        entityManager.setFlushMode(FlushModeType.COMMIT);
        System.out.println("FlushMode: " + entityManager.getFlushMode());

        int updatedCount = entityManager.createQuery(
                "UPDATE Member m SET m.nickname = :nickname WHERE m.id = :id")
                .setParameter("nickname", "토르")
                .setParameter("id", memberId)
                .executeUpdate();

        System.out.println("JPQL 업데이트 영향받은 행 수: " + updatedCount);

        // Then: 중간 상태 확인 (clear 하지 않음!)
        System.out.println("\n========== 중간 상태 확인 (clear 하지 않음) ==========");
        System.out.println("영속성 컨텍스트의 닉네임: " + foundMember.getNickname());

        String nicknameFromDbBeforeCommit = (String) entityManager.createQuery(
                "SELECT m.nickname FROM Member m WHERE m.id = :id")
                .setParameter("id", memberId)
                .getSingleResult();
        System.out.println("현재 DB의 닉네임 (JPQL 조회): " + nicknameFromDbBeforeCommit);

        // 트랜잭션 커밋 시뮬레이션 (flush 명시 호출)
        System.out.println("\n========== 트랜잭션 커밋 시점 시뮬레이션 (flush 호출) ==========");
        System.out.println("flush를 명시적으로 호출합니다...");
        entityManager.flush();
        System.out.println("flush 완료!");

        // 커밋 후 DB 상태 확인
        String nicknameFromDbAfterFlush = (String) entityManager.createQuery(
                "SELECT m.nickname FROM Member m WHERE m.id = :id")
                .setParameter("id", memberId)
                .getSingleResult();
        System.out.println("flush 후 DB의 닉네임 (JPQL 조회): " + nicknameFromDbAfterFlush);

        System.out.println("\n========== 결론 ==========");
        System.out.println("1. JPQL 실행 시점: DB가 '토르'로 변경됨");
        System.out.println("2. 하지만 영속성 컨텍스트는 '로키'를 유지");
        System.out.println("3. 트랜잭션 커밋(flush) 시점: 영속성 컨텍스트의 '로키'가 DB를 덮어씀!");
        System.out.println("4. 최종 DB 값: " + nicknameFromDbAfterFlush);
        System.out.println("5. JPQL로 변경한 '토르'는 결국 '로키'로 덮어써졌음!");

        // Assertion - 최종적으로는 로키가 저장됨!
        assertThat(nicknameFromDbAfterFlush).isEqualTo("로키");
    }
}
