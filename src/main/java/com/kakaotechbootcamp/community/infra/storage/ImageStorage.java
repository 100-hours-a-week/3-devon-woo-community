package com.kakaotechbootcamp.community.infra.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 이미지 저장소 인터페이스
 */
public interface ImageStorage {

    /**
     * 이미지를 업로드하고 URL을 반환합니다.
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드된 이미지의 URL
     * @throws IOException 업로드 중 오류 발생 시
     */
    String upload(MultipartFile file);
}
