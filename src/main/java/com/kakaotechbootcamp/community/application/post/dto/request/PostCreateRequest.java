package com.kakaotechbootcamp.community.application.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "게시글 제목은 필수입니다")
    private String title;

    @NotBlank(message = "게시글 내용은 필수입니다")
    private String content;

    private String image;
}