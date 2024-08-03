package com.cheeus.config.auth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cheeus.config.auth.domain.OAuth2Attribute;
import com.cheeus.member.domain.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 이 클래스는 oAuth2Login이 성공했을때 가장 먼저 실행되는 함수. 그 다음은 SuccessHandler가 실행된다.
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
				
		//  1번 : OAuth2UserService를 선언한다
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        System.out.println(userRequest.getClientRegistration());
        System.out.println(userRequest.getAccessToken().getTokenValue());
        
        //	2번 : OAuth2UserService의 loadUser를 호출하고 변수로 userRequest를 넣는다.
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        log.info("oAuth2User = {}", oAuth2User);
        
		//	3번 : userRequest에 담겨있는 정보들을 불러온다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        log.info("registrationId = {}", registrationId);
        log.info("accessToken = {}", accessToken.getTokenValue());
        log.info("userNameAttributeName = {}", userNameAttributeName);
        
        // 4번 : 직접 만든 OAuth2Attribute에 userRequest중의 일부를 대입한다.
        OAuth2Attribute oAuth2Attribute =
                OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        var memberAttribute = oAuth2Attribute.convertToMap();
        
        System.out.println("OAuth2Attribute : " + oAuth2Attribute);
		System.out.println("memberAttribute : " + memberAttribute);

		Member member = new Member();
		member.setEmail(oAuth2Attribute.getEmail());
		member.setRole("ROLE_USER");
		member.setRegistrationId(registrationId);

        log.info("member.registrationId = {}", member.getRegistrationId());

		return new CustomOAuth2User(member, memberAttribute);
		//return null;
	}
	
}

