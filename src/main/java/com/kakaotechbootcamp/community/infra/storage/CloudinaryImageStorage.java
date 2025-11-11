package com.kakaotechbootcamp.community.infra.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Cloudinary를 사용한 이미지 저장소 구현체
 */
@Component
@RequiredArgsConstructor
public class CloudinaryImageStorage implements ImageStorage {

    private final Cloudinary cloudinary;

    @Override
    public String upload(MultipartFile file){
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile image", e);
        }
    }
}
