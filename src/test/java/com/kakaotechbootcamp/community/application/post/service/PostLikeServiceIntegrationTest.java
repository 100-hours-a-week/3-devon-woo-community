package com.kakaotechbootcamp.community.application.post.service;

import com.kakaotechbootcamp.community.application.post.dto.response.PostLikeResponse;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.PostErrorCode;
import com.kakaotechbootcamp.community.config.TestConfig;
import com.kakaotechbootcamp.community.domain.member.entity.Member;
import com.kakaotechbootcamp.community.domain.member.repository.MemberRepository;
import com.kakaotechbootcamp.community.domain.post.entity.Post;
import com.kakaotechbootcamp.community.domain.post.repository.PostLikeRepository;
import com.kakaotechbootcamp.community.domain.post.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PostLikeServiceIntegrationTest {

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    private Long testMember2Id;
    private Long testPostId;

    @BeforeEach
    void setUp() {
        // JPA를 사용한 테스트 데이터 생성
        Member testMember1 = Member.create("test1@example.com", "password123!", "tester1");
        testMember1.updateProfileImage("https://example.com/profile1.jpg");
        testMember1 = memberRepository.save(testMember1);

        Member testMember2 = Member.create("test2@example.com", "password123!", "tester2");
        testMember2.updateProfileImage("https://example.com/profile2.jpg");
        testMember2 = memberRepository.save(testMember2);
        testMember2Id = testMember2.getId();

        Post testPost = Post.create(testMember1, "Test Post", "Test Content");
        testPost = postRepository.save(testPost);
        testPostId = testPost.getId();
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 정리
        postLikeRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 좋아요 추가 성공")
    void likePost_Success() {
        // when
        PostLikeResponse response = postLikeService.likePost(testPostId, testMember2Id);

        // then
        assertThat(response.postId()).isEqualTo(testPostId);
        assertThat(response.likeCount()).isEqualTo(1L);

        // DB 검증
        Post updatedPost = postRepository.findById(testPostId).orElseThrow();
        assertThat(updatedPost.getLikeCount()).isEqualTo(1L);

        boolean likeExists = postLikeRepository.existsByPostIdAndMemberId(testPostId, testMember2Id);
        assertThat(likeExists).isTrue();
    }

    @Test
    @DisplayName("게시글 좋아요 취소 성공")
    void unlikePost_Success() {
        // given - 먼저 좋아요 추가
        postLikeService.likePost(testPostId, testMember2Id);

        // when
        PostLikeResponse response = postLikeService.unlikePost(testPostId, testMember2Id);

        // then
        assertThat(response.postId()).isEqualTo(testPostId);
        assertThat(response.likeCount()).isEqualTo(0L);

        // DB 검증
        Post updatedPost = postRepository.findById(testPostId).orElseThrow();
        assertThat(updatedPost.getLikeCount()).isEqualTo(0L);

        boolean likeExists = postLikeRepository.existsByPostIdAndMemberId(testPostId, testMember2Id);
        assertThat(likeExists).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 좋아요 시도 시 예외 발생")
    void likePost_PostNotFound_ThrowsException() {
        // given
        Long nonExistentPostId = 99999L;

        // when & then
        assertThatThrownBy(() -> postLikeService.likePost(nonExistentPostId, testMember2Id))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("중복 좋아요 시도 시 예외 발생")
    void likePost_AlreadyLiked_ThrowsException() {
        // given - 먼저 좋아요 추가
        postLikeService.likePost(testPostId, testMember2Id);

        // when & then - 다시 좋아요 시도
        assertThatThrownBy(() -> postLikeService.likePost(testPostId, testMember2Id))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.ALREADY_LIKED);
    }

    @Test
    @DisplayName("존재하지 않는 좋아요 취소 시도 시 예외 발생")
    void unlikePost_LikeNotFound_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> postLikeService.unlikePost(testPostId, testMember2Id))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.LIKE_NOT_FOUND);
    }

    @Test
    @DisplayName("여러 사용자가 동시에 좋아요 추가 - 원자적 업데이트 검증")
    void likePost_ConcurrentLikes_AtomicUpdate() throws InterruptedException {
        // given
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // 10명의 테스트 회원을 JPA로 생성
        Long[] memberIds = new Long[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            Member member = Member.create("concurrent" + i + "@example.com", "password123!", "concur" + i);
            member.updateProfileImage("https://example.com/profile.jpg");
            member = memberRepository.save(member);
            memberIds[i] = member.getId();
        }

        // when - 10명이 동시에 좋아요 추가
        for (int i = 0; i < numberOfThreads; i++) {
            Long memberId = memberIds[i];
            executorService.execute(() -> {
                try {
                    postLikeService.likePost(testPostId, memberId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 예외는 무시 (중복 좋아요 등)
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 좋아요 수가 정확히 10개여야 함
        Post updatedPost = postRepository.findById(testPostId).orElseThrow();
        assertThat(updatedPost.getLikeCount()).isEqualTo(numberOfThreads);
        assertThat(successCount.get()).isEqualTo(numberOfThreads);
    }

    @Test
    @DisplayName("동시에 좋아요 추가/취소 - 최종 카운트 일관성 검증")
    void likeAndUnlikePost_ConcurrentOperations_ConsistentCount() throws InterruptedException {
        // given
        int numberOfThreads = 20; // 10개는 좋아요, 10개는 좋아요 취소
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 20명의 테스트 회원 생성 및 좋아요 추가를 JPA로 처리
        Long[] memberIds = new Long[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            Member member = Member.create("mixed" + i + "@example.com", "password123!", "mixed" + i);
            member.updateProfileImage("https://example.com/profile.jpg");
            member = memberRepository.save(member);
            memberIds[i] = member.getId();

            // 먼저 모든 회원이 좋아요 추가
            postLikeService.likePost(testPostId, memberIds[i]);
        }

        // when - 절반은 좋아요 취소, 절반은 재추가 시도 (실패할 것)
        for (int i = 0; i < numberOfThreads; i++) {
            Long memberId = memberIds[i];
            boolean shouldUnlike = i < numberOfThreads / 2;

            executorService.execute(() -> {
                try {
                    if (shouldUnlike) {
                        // 첫 10명은 좋아요 취소
                        postLikeService.unlikePost(testPostId, memberId);
                    } else {
                        // 나머지 10명은 이미 좋아요 했으므로 재추가 시도 (실패)
                        try {
                            postLikeService.likePost(testPostId, memberId);
                        } catch (CustomException e) {
                            // 중복 좋아요 예외 예상
                        }
                    }
                } catch (Exception e) {
                    // 무시
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 최종 좋아요 수는 10개여야 함 (20개 - 10개 취소)
        Post updatedPost = postRepository.findById(testPostId).orElseThrow();
        assertThat(updatedPost.getLikeCount()).isEqualTo(numberOfThreads / 2);
    }
}
