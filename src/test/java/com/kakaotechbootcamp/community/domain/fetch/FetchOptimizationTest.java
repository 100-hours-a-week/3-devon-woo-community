package com.kakaotechbootcamp.community.domain.fetch;

import com.kakaotechbootcamp.community.config.TestConfig;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@DisplayName("FetchJoin vs EntityGraph vs BatchSize 조회 성능 비교 테스트")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FetchOptimizationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Statistics statistics;
    private static boolean dataInitialized = false;

    @BeforeAll
    void setUpTestData() {
        // 테스트 데이터 생성 (전체 테스트 실행 전 1회만)
        if (!dataInitialized) {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(status -> {
                createTestData();
                entityManager.flush();
                return null;
            });
            dataInitialized = true;
        }
    }

    @BeforeEach
    void setUp() {
        // Hibernate Statistics 초기화 (각 테스트마다)
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        statistics = sessionFactory.getStatistics();
        statistics.clear();
        statistics.setStatisticsEnabled(true);

        // 영속성 컨텍스트 초기화 (각 테스트의 독립성 보장)
        entityManager.clear();
    }

    private void createTestData() {
        // 회원 5명 생성
        for (int i = 1; i <= 5; i++) {
            Member member = Member.create(
                    "user" + i + "@test.com",
                    "password" + i,
                    "사용자" + i
            );
            memberRepository.save(member);

            // 각 회원당 게시글 3개 생성
            for (int j = 1; j <= 3; j++) {
                Post post = Post.create(
                        member,
                        "게시글 제목 " + i + "-" + j,
                        "게시글 내용 " + i + "-" + j
                );
                postRepository.save(post);

                // 각 게시글당 댓글 2개 생성
                for (int k = 1; k <= 2; k++) {
                    Comment comment = Comment.create(
                            member,
                            post,
                            "댓글 내용 " + i + "-" + j + "-" + k
                    );
                    commentRepository.save(comment);
                }
            }
        }
    }

    @Test
    @Transactional
    @DisplayName("1. Lazy Loading - N+1 문제 발생 확인")
    void testLazyLoading_NPlusOneProblem() {
        System.out.println("\n========== Lazy Loading 테스트 시작 ==========");

        // given: 통계 초기화
        statistics.clear();
        long startQueryCount = statistics.getPrepareStatementCount();

        // when: Post 조회 후 Author 접근 (N+1 발생)
        List<Post> posts = postRepository.findAll();
        System.out.println("Post 조회 완료: " + posts.size() + "개");
        System.out.println("\n[Author 접근 시작 - 각 Member마다 SELECT 쿼리 발생]");

        // Author 접근 시 N개의 추가 쿼리 발생
        posts.forEach(post -> {
            String authorName = post.getAuthor().getNickname();
            System.out.println("  - Post ID: " + post.getId() + ", Author: " + authorName);
        });

        // then: 쿼리 수 확인
        long endQueryCount = statistics.getPrepareStatementCount();
        long totalQueries = endQueryCount - startQueryCount;

        System.out.println("\n[Lazy Loading 결과]");
        System.out.println("총 실행된 쿼리 수: " + totalQueries);
        System.out.println("Post 개수: " + posts.size());
        System.out.println("예상: 1(Post 조회) + 고유 Member 수(각 Member 조회)");
        System.out.println(" N+1 문제 발생");

        // N+1 문제 확인: Post 조회 1번 + 고유 Member마다 조회 N번
        // 15개 Post = 5명의 Member -> 1 + 5 = 6개 쿼리
        assertThat(totalQueries).isEqualTo(6);  // 1 Post + 5 Members
        System.out.println("========== Lazy Loading 테스트 종료 ==========\n");
    }

    @Test
    @Transactional
    @DisplayName("2. Fetch Join - Repository 메서드로 N+1 해결")
    void testFetchJoin_SolvesNPlusOne() {
        System.out.println("\n========== Fetch Join 테스트 시작 ==========");

        // given: 통계 초기화
        statistics.clear();
        long startQueryCount = statistics.getPrepareStatementCount();

        // when: Repository의 Fetch Join 메서드로 한 번에 조회
        List<Post> posts = postRepository.findAllWithAuthorByFetchJoin();
        System.out.println("Post + Author 조회 완료: " + posts.size() + "개");
        System.out.println("\n[Author 접근 - 추가 쿼리 없음 (이미 로드됨)]");

        // Author 접근 시 추가 쿼리 없음
        posts.forEach(post -> {
            String authorName = post.getAuthor().getNickname();
            System.out.println("  - Post ID: " + post.getId() + ", Author: " + authorName);
        });

        // then: 쿼리 수 확인
        long endQueryCount = statistics.getPrepareStatementCount();
        long totalQueries = endQueryCount - startQueryCount;

        System.out.println("\n[Fetch Join 결과]");
        System.out.println("총 실행된 쿼리 수: " + totalQueries);
        System.out.println("예상: 1번의 JOIN 쿼리로 모든 데이터 조회");
        System.out.println("사용 방법: @Query(\"SELECT p FROM Post p JOIN FETCH p.author\")");

        // Fetch Join: 1번의 쿼리로 해결
        assertThat(totalQueries).isEqualTo(1);
        System.out.println("========== Fetch Join 테스트 종료 ==========\n");
    }

    @Test
    @Transactional
    @DisplayName("3. Entity Graph - Repository 어노테이션으로 동적 페치")
    void testEntityGraph_DynamicFetchStrategy() {
        System.out.println("\n========== Entity Graph 테스트 시작 ==========");

        // given: 통계 초기화
        statistics.clear();
        long startQueryCount = statistics.getPrepareStatementCount();

        // when: Repository의 EntityGraph 메서드로 동적 페치
        List<Post> posts = postRepository.findAllWithAuthorByEntityGraph();
        System.out.println("Post + Author 조회 완료 (EntityGraph): " + posts.size() + "개");
        System.out.println("\n[Author 접근 - 추가 쿼리 없음 (EntityGraph로 로드됨)]");

        // Author 접근 시 추가 쿼리 없음
        posts.forEach(post -> {
            String authorName = post.getAuthor().getNickname();
            System.out.println("  - Post ID: " + post.getId() + ", Author: " + authorName);
        });

        // then: 쿼리 수 확인
        long endQueryCount = statistics.getPrepareStatementCount();
        long totalQueries = endQueryCount - startQueryCount;

        System.out.println("\n[Entity Graph 결과]");
        System.out.println("총 실행된 쿼리 수: " + totalQueries);
        System.out.println("예상: 1번의 쿼리로 모든 데이터 조회");
        System.out.println("사용 방법: @EntityGraph(attributePaths = {\"author\"})");

        // EntityGraph도 1번의 쿼리로 해결
        assertThat(totalQueries).isEqualTo(1);
        System.out.println("========== Entity Graph 테스트 종료 ==========\n");
    }
}
