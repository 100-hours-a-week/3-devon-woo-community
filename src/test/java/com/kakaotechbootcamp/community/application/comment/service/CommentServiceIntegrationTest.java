package com.kakaotechbootcamp.community.application.comment.service;

import com.kakaotechbootcamp.community.application.comment.dto.request.CommentCreateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.request.CommentUpdateRequest;
import com.kakaotechbootcamp.community.application.comment.dto.response.CommentResponse;
import com.kakaotechbootcamp.community.application.common.dto.response.PageResponse;
import com.kakaotechbootcamp.community.common.exception.CustomException;
import com.kakaotechbootcamp.community.common.exception.code.CommentErrorCode;
import com.kakaotechbootcamp.community.common.exception.code.CommonErrorCode;
import com.kakaotechbootcamp.community.common.exception.code.MemberErrorCode;
import com.kakaotechbootcamp.community.common.exception.code.PostErrorCode;
import com.kakaotechbootcamp.community.config.EnableSqlLogging;
import com.kakaotechbootcamp.community.config.TestConfig;
import com.kakaotechbootcamp.community.domain.post.entity.Comment;
import com.kakaotechbootcamp.community.domain.post.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Sql(scripts = "/sql/comment-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    private static final Long TEST_MEMBER1_ID = 1L;
    private static final Long TEST_MEMBER2_ID = 2L;
    private static final Long TEST_POST1_ID = 1L;
    private static final Long TEST_POST2_ID = 2L;
    private static final Long TEST_COMMENT1_ID = 1L;
    private static final Long TEST_COMMENT2_ID = 2L;

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 생성 성공")
    void createComment_Success() {
        // given
        CommentCreateRequest request = new CommentCreateRequest(
                TEST_MEMBER1_ID,
                "새로운 댓글입니다."
        );

        // when
        CommentResponse response = commentService.createComment(TEST_POST1_ID, request, TEST_MEMBER1_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEqualTo("새로운 댓글입니다.");
        assertThat(response.postId()).isEqualTo(TEST_POST1_ID);
        assertThat(response.member().memberId()).isEqualTo(TEST_MEMBER1_ID);

        // DB 검증
        Comment savedComment = commentRepository.findById(response.commentId()).orElseThrow();
        assertThat(savedComment.getContent()).isEqualTo("새로운 댓글입니다.");
        assertThat(savedComment.getPost().getId()).isEqualTo(TEST_POST1_ID);
        assertThat(savedComment.getMember().getId()).isEqualTo(TEST_MEMBER1_ID);
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 생성 실패 - 존재하지 않는 게시글")
    void createComment_PostNotFound_ThrowsException() {
        // given
        Long nonExistentPostId = 99999L;
        CommentCreateRequest request = new CommentCreateRequest(
                TEST_MEMBER1_ID,
                "댓글 내용"
        );

        // when & then
        assertThatThrownBy(() -> commentService.createComment(nonExistentPostId, request, TEST_MEMBER1_ID))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.POST_NOT_FOUND);
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 생성 실패 - 존재하지 않는 회원")
    void createComment_MemberNotFound_ThrowsException() {
        // given
        Long nonExistentMemberId = 99999L;
        CommentCreateRequest request = new CommentCreateRequest(
                nonExistentMemberId,
                "댓글 내용"
        );

        // when & then
        assertThatThrownBy(() -> commentService.createComment(TEST_POST1_ID, request, nonExistentMemberId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", MemberErrorCode.USER_NOT_FOUND);
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 수정 성공")
    void updateComment_Success() {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest(
                TEST_MEMBER1_ID,
                "수정된 댓글 내용"
        );

        // when
        CommentResponse response = commentService.updateComment(TEST_COMMENT1_ID, request, TEST_MEMBER1_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEqualTo("수정된 댓글 내용");
        assertThat(response.commentId()).isEqualTo(TEST_COMMENT1_ID);

        // DB 검증
        Comment updatedComment = commentRepository.findById(TEST_COMMENT1_ID).orElseThrow();
        assertThat(updatedComment.getContent()).isEqualTo("수정된 댓글 내용");
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 수정 실패 - 권한 없음 (다른 사용자)")
    void updateComment_NotOwner_ThrowsException() {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest(
                TEST_MEMBER2_ID,
                "수정된 댓글 내용"
        );

        // when & then - TEST_COMMENT1_ID는 TEST_MEMBER1_ID 소유이므로 TEST_MEMBER2_ID로 수정 시도 시 실패
        assertThatThrownBy(() -> commentService.updateComment(TEST_COMMENT1_ID, request, TEST_MEMBER2_ID))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", CommonErrorCode.NO_PERMISSION);
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 수정 실패 - 존재하지 않는 댓글")
    void updateComment_CommentNotFound_ThrowsException() {
        // given
        Long nonExistentCommentId = 99999L;
        CommentUpdateRequest request = new CommentUpdateRequest(
                TEST_MEMBER1_ID,
                "수정된 댓글 내용"
        );

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(nonExistentCommentId, request, TEST_MEMBER1_ID))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", CommentErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 삭제 성공")
    void deleteComment_Success() {
        // when
        commentService.deleteComment(TEST_COMMENT1_ID, TEST_MEMBER1_ID);

        // then - DB 검증 (실제 삭제되므로 존재하지 않아야 함)
        boolean exists = commentRepository.existsById(TEST_COMMENT1_ID);
        assertThat(exists).isFalse();
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 삭제 실패 - 권한 없음 (다른 사용자)")
    void deleteComment_NotOwner_ThrowsException() {
        // when & then - TEST_COMMENT1_ID는 TEST_MEMBER1_ID 소유이므로 TEST_MEMBER2_ID로 삭제 시도 시 실패
        assertThatThrownBy(() -> commentService.deleteComment(TEST_COMMENT1_ID, TEST_MEMBER2_ID))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", CommonErrorCode.NO_PERMISSION);
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 삭제 실패 - 존재하지 않는 댓글")
    void deleteComment_CommentNotFound_ThrowsException() {
        // given
        Long nonExistentCommentId = 99999L;

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(nonExistentCommentId, TEST_MEMBER1_ID))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", CommentErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 상세 조회 성공")
    void getCommentDetails_Success() {
        // when
        CommentResponse response = commentService.getCommentsDetails(TEST_COMMENT1_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.commentId()).isEqualTo(TEST_COMMENT1_ID);
        assertThat(response.content()).isEqualTo("Test Comment 1");
        assertThat(response.postId()).isEqualTo(TEST_POST1_ID);
        assertThat(response.member().nickname()).isEqualTo("tester1");
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 상세 조회 실패 - 존재하지 않는 댓글")
    void getCommentDetails_CommentNotFound_ThrowsException() {
        // given
        Long nonExistentCommentId = 99999L;

        // when & then
        assertThatThrownBy(() -> commentService.getCommentsDetails(nonExistentCommentId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", CommentErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    @EnableSqlLogging
    @DisplayName("게시글의 댓글 목록 조회 성공 - 페이징")
    void getCommentPageByPostId_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        PageResponse<CommentResponse> page = commentService.getCommentPageByPostId(TEST_POST1_ID, pageable);

        // then
        assertThat(page).isNotNull();
        assertThat(page.items()).hasSize(2); // test-data.sql에 게시글 1에 댓글 2개
        assertThat(page.totalElements()).isEqualTo(2L);

        CommentResponse firstComment = page.items().get(0);
        assertThat(firstComment.commentId()).isNotNull();
        assertThat(firstComment.content()).isNotNull();
        assertThat(firstComment.member().nickname()).isNotNull();
    }

    @Test
    @EnableSqlLogging
    @DisplayName("게시글의 댓글 목록 조회 실패 - 존재하지 않는 게시글")
    void getCommentPageByPostId_PostNotFound_ThrowsException() {
        // given
        Long nonExistentPostId = 99999L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when & then
        assertThatThrownBy(() -> commentService.getCommentPageByPostId(nonExistentPostId, pageable))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", PostErrorCode.POST_NOT_FOUND);
    }

    @Test
    @EnableSqlLogging
    @DisplayName("게시글의 댓글 목록 조회 성공 - 삭제된 댓글은 제외")
    void getCommentPageByPostId_ExcludesDeletedComments() {
        // given
        commentService.deleteComment(TEST_COMMENT1_ID, TEST_MEMBER1_ID);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        PageResponse<CommentResponse> page = commentService.getCommentPageByPostId(TEST_POST1_ID, pageable);

        // then
        assertThat(page.totalElements()).isEqualTo(1L); // 삭제된 댓글은 제외되어야 함
        assertThat(page.items())
                .noneMatch(comment -> comment.commentId().equals(TEST_COMMENT1_ID));
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 생성 후 즉시 조회 가능")
    @Transactional
    void createComment_ThenRetrieve_Success() {
        // given
        CommentCreateRequest createRequest = new CommentCreateRequest(
                TEST_MEMBER1_ID,
                "즉시 조회 테스트 댓글"
        );

        // when
        CommentResponse createdComment = commentService.createComment(TEST_POST1_ID, createRequest, TEST_MEMBER1_ID);
        CommentResponse retrievedComment = commentService.getCommentsDetails(createdComment.commentId());

        // then
        assertThat(retrievedComment.commentId()).isEqualTo(createdComment.commentId());
        assertThat(retrievedComment.content()).isEqualTo("즉시 조회 테스트 댓글");
    }

    @Test
    @EnableSqlLogging
    @DisplayName("여러 댓글 생성 후 목록 조회")
    @Transactional
    void createMultipleComments_ThenRetrieveList_Success() {
        // given - 3개의 댓글 추가 생성
        for (int i = 0; i < 3; i++) {
            CommentCreateRequest request = new CommentCreateRequest(
                    TEST_MEMBER1_ID,
                    "추가 댓글 " + i
            );
            commentService.createComment(TEST_POST1_ID, request, TEST_MEMBER1_ID);
        }

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<CommentResponse> page = commentService.getCommentPageByPostId(TEST_POST1_ID, pageable);

        // then
        assertThat(page.totalElements()).isEqualTo(5L); // 기존 2개 + 신규 3개
    }

    @Test
    @EnableSqlLogging
    @DisplayName("댓글 삭제 후 재삭제 시도 시 예외 발생")
    void deleteComment_AlreadyDeleted_ThrowsException() {
        // given - 먼저 댓글 삭제
        commentService.deleteComment(TEST_COMMENT1_ID, TEST_MEMBER1_ID);

        // when & then - 다시 삭제 시도
        assertThatThrownBy(() -> commentService.deleteComment(TEST_COMMENT1_ID, TEST_MEMBER1_ID))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", CommentErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    @EnableSqlLogging
    @DisplayName("다른 게시글에는 댓글이 조회되지 않음")
    void getCommentPageByPostId_DifferentPost_ReturnsEmpty() {
        // given - TEST_POST2_ID에는 댓글이 없음
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        PageResponse<CommentResponse> page = commentService.getCommentPageByPostId(TEST_POST2_ID, pageable);

        // then
        assertThat(page.totalElements()).isEqualTo(0L);
        assertThat(page.items()).isEmpty();
    }
}
