package com.kakaotechbootcamp.community.application.common.controller;

import com.kakaotechbootcamp.community.common.dto.api.ApiResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    @Value("${storage.cloudinary.api.key}")
    private String apiKey;

    @Value("${storage.cloudinary.api.secret}")
    private String apiSecret;

    @Value("${storage.cloudinary.cloud.name}")
    private String cloudName;

    @Value("${storage.cloudinary.upload.preset:unsigned_preset}")
    private String uploadPreset;

    @GetMapping("/sign")
    public ApiResponse<Map<String, Object>> sign(
            @RequestParam(value = "type", defaultValue = "post") String type
    ) {
        long timestamp = System.currentTimeMillis() / 1000L;

        // type에 따라 folder 결정
        String folder = "profile".equals(type) ? "profiles" : "posts";

        // 서명 생성에 사용할 파라미터를 TreeMap으로 관리 (자동 알파벳순 정렬)
        Map<String, String> paramsToSign = new TreeMap<>();
        paramsToSign.put("folder", folder);
        paramsToSign.put("timestamp", String.valueOf(timestamp));
        paramsToSign.put("upload_preset", uploadPreset);

        // 알파벳순으로 정렬된 파라미터를 & 로 연결하고 마지막에 api_secret 추가
        StringBuilder toSign = new StringBuilder();
        paramsToSign.forEach((key, value) -> {
            if (toSign.length() > 0) {
                toSign.append("&");
            }
            toSign.append(key).append("=").append(value);
        });
        toSign.append(apiSecret);

        String signature = DigestUtils.sha1Hex(toSign.toString());

        // 응답 데이터 생성 (서명 생성에 사용한 파라미터를 모두 포함)
        Map<String, Object> body = new HashMap<>();
        body.put("apiKey", apiKey);
        body.put("cloudName", cloudName);
        body.put("timestamp", timestamp);
        body.put("signature", signature);
        body.put("uploadPreset", uploadPreset);
        body.put("folder", folder);  // 서명 생성에 사용한 folder 포함

        return ApiResponse.success(body);
    }
}