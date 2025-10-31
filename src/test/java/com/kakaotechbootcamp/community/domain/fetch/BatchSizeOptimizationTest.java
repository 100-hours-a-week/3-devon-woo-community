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
@ActiveProfiles("batch-test")
@Import(TestConfig.class)
@DisplayName("Batch Size 조회 최적화 테스트")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BatchSizeOptimizationTest {

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
    @DisplayName("Batch Size - application.yml의 default_batch_fetch_size로 IN 절 최적화")
    void testBatchSize_OptimizesWithInClause() {
        System.out.println("\n========== Batch Size 테스트 시작 ==========");

        // given: 통계 초기화
        statistics.clear();
        long startQueryCount = statistics.getPrepareStatementCount();

        // when: 일반 findAll()이지만 글로벌 batch size가 설정되어 있음
        // application-batch-test.yml에 default_batch_fetch_size: 10 설정됨
        List<Post> posts = postRepository.findAll();
        System.out.println("Post 조회 완료: " + posts.size() + "개");
        System.out.println("\n[Author 접근 - IN 절로 배치 조회 (WHERE id IN (?, ?, ...))]");

        // Author 접근 시 IN 절로 배치 조회
        posts.forEach(post -> {
            String authorName = post.getAuthor().getNickname();
            System.out.println("  - Post ID: " + post.getId() + ", Author: " + authorName);
        });

        // then: 쿼리 수 확인
        long endQueryCount = statistics.getPrepareStatementCount();
        long totalQueries = endQueryCount - startQueryCount;

        System.out.println("\n[Batch Size 결과]");
        System.out.println("총 실행된 쿼리 수: " + totalQueries);
        System.out.println("예상: 1(Post 조회) + ceil(고유 Member 수/10)(Member 배치 조회)");
        System.out.println("설정 방법: hibernate.default_batch_fetch_size: 10 (application.yml)");
        System.out.println("효과: Member를 10개씩 묶어서 WHERE id IN (?, ?, ...) 쿼리로 조회");

        // BatchSize: Post 조회 1번 + Member 배치 조회 (10개씩)
        // 15개 Post = 5명의 Member -> 1번의 IN 절로 조회 가능
        // 총 2개 쿼리 (Post 1 + Member 1)
        assertThat(totalQueries).isEqualTo(2);
        assertThat(totalQueries).isLessThan(posts.size() + 1);
        System.out.println("========== Batch Size 테스트 종료 ==========\n");
    }
}
