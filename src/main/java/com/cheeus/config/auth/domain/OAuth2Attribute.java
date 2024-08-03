package com.cheeus.config.auth.domain;

import java.util.HashMap;
import java.util.Map;

import com.cheeus.member.domain.Member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attribute {
    private Map<String, Object> attributes;
    private String registrationId;
    private String email;
    private String id;

    public static OAuth2Attribute of(String provider, String attributeKey,
                              Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return ofGoogle(provider, attributes);
            case "kakao":
                return ofKakao(provider, attributes);
            case "naver":
                return ofNaver(provider, attributes);
            default:
                throw new RuntimeException();
        }
    }

    private static OAuth2Attribute ofGoogle(String provider,
                                            Map<String, Object> attributes) {
    	
        return OAuth2Attribute.builder()
                .id((String) attributes.get("sub"))
                .email((String) attributes.get("email"))
                .registrationId(provider)
                .build();
    }

    private static OAuth2Attribute ofKakao(String provider,
                                           Map<String, Object> attributes) {
    	
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        
        System.out.println(attributes.get("id"));

        return OAuth2Attribute.builder()
                .id((String) attributes.get("id"))
                .email((String) kakaoAccount.get("email"))
                .attributes(kakaoAccount)
                .registrationId(provider)
                .build();
    }

    private static OAuth2Attribute ofNaver(String provider,
                                           Map<String, Object> attributes) {
    	
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attribute.builder()
                //.name((String) response.get("name"))
                .email((String) response.get("email"))
                //.picture((String) response.get("profile_image"))
                .attributes(response)
                .registrationId(provider)
                .build();
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("registrationId", registrationId);
        map.put("email", email);

        return map;
    }
}
