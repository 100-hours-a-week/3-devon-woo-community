package com.kakaotechbootcamp.community.application.test;

import com.kakaotechbootcamp.community.domain.post.entity.Attachment;
import com.kakaotechbootcamp.community.domain.post.repository.AttachmentRepository;
import com.kakaotechbootcamp.community.domain.post.repository.impl.AttachmentRepositoryImpl;
import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Attachment 엔티티 테스트를 위한 컨트롤러
 * 개선된 SimpleJpaRepositoryImpl 아키텍처 검증
 */
@Slf4j
@RestController
@RequestMapping("/api/test/attachments")
@RequiredArgsConstructor
public class AttachmentTestController {

    private final AttachmentRepository attachmentRepository;

    @GetMapping
    public ApiResponse<List<Attachment>> getAllAttachments() {
        log.info("Getting all attachments...");
        List<Attachment> attachments = attachmentRepository.findAll();
        log.info("Found {} attachments", attachments.size());
        return ApiResponse.success(attachments);
    }

    @GetMapping("/{id}")
    public ApiResponse<Attachment> getAttachmentById(@PathVariable Long id) {
        log.info("Getting attachment by id: {}", id);
        return attachmentRepository.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.failure("Attachment not found with id: " + id));
    }

    @GetMapping("/count")
    public ApiResponse<Long> getAttachmentCount() {
        long count = attachmentRepository.count();
        log.info("Total attachments count: {}", count);
        return ApiResponse.success(count);
    }

    @PostMapping
    public ApiResponse<Attachment> createAttachment(@RequestBody Map<String, Object> request) {
        log.info("Creating new attachment: {}", request);

        // Attachment 생성 (Builder 패턴 사용)
        Attachment attachment = Attachment.builder()
                .postId(Long.valueOf(request.get("postId").toString()))
                .attachmentUrl(request.get("attachmentUrl").toString())
                .build();

        log.info("Before save - Attachment: {}", attachment);
        Attachment savedAttachment = attachmentRepository.save(attachment);
        log.info("After save - Attachment: {}", savedAttachment);

        return ApiResponse.success(savedAttachment);
    }

    @PostMapping("/init")
    public ApiResponse<String> initTestData() {
        log.info("Initializing test attachments...");

        // 테스트 첨부파일 생성 (기존 포스트들에 첨부)
        Attachment attachment1 = Attachment.builder()
                .postId(1L)     // 첫 번째 포스트
                .attachmentUrl("https://example.com/files/document1.pdf")
                .build();

        Attachment attachment2 = Attachment.builder()
                .postId(1L)     // 첫 번째 포스트 (여러 첨부파일)
                .attachmentUrl("https://example.com/images/screenshot1.png")
                .build();

        Attachment attachment3 = Attachment.builder()
                .postId(2L)     // 두 번째 포스트
                .attachmentUrl("https://example.com/videos/demo.mp4")
                .build();

        Attachment attachment4 = Attachment.builder()
                .postId(3L)     // 세 번째 포스트
                .attachmentUrl("https://cdn.example.com/resources/manual.zip")
                .build();

        Attachment savedAttachment1 = attachmentRepository.save(attachment1);
        Attachment savedAttachment2 = attachmentRepository.save(attachment2);
        Attachment savedAttachment3 = attachmentRepository.save(attachment3);
        Attachment savedAttachment4 = attachmentRepository.save(attachment4);

        log.info("Test attachments created: {}, {}, {}, {}",
                savedAttachment1.getId(), savedAttachment2.getId(),
                savedAttachment3.getId(), savedAttachment4.getId());
        return ApiResponse.success("Test attachments initialized successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAttachment(@PathVariable Long id) {
        log.info("Deleting attachment with id: {}", id);
        attachmentRepository.deleteById(id);
        return ApiResponse.success("Attachment deleted successfully");
    }
}