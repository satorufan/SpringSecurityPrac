package com.cheeus.config.auth.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.cheeus.config.auth.CustomOAuth2User;
import com.cheeus.config.auth.cookie.CookieUtil;
import com.cheeus.config.auth.token.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//    private final TokenService tokenService;
//    private final MemberDao dao;
//    //private final ObjectMapper objectMapper;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
//            throws IOException, ServletException {
//        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
//        Member member = dao.findByEmail(oAuth2User.getName());
//
////        System.out.println("Principal에서 꺼낸 OAuth2User : " + oAuth2User);
//        log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);
//        // 최초 로그인이라면 회원가입 처리를 한다.
//        String targetUrl;
//        log.info("토큰 발행 시작");
//
//        Token token = tokenService.generateToken(member.getEmail(), "USER");
//        
////        System.out.println("Success : " + token);
//        log.info("{}", token);
//        log.info("request : {} / response : {}", request, response);
////        targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000")
////                .queryParam("token", token)
////                .build().toUriString();
//        targetUrl = "http://localhost:3000";
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
//    }
//}

// 이 클래스 수행후 JwtAuthFilter 수행..
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
    		Authentication authentication) throws IOException, ServletException {

    	System.out.println("Success Handler - onAuthenticationSuccess");
        //OAuth2User
    	CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

    	System.out.println("authentication : " + authentication);
    	System.out.println("customUserDetails : " + customUserDetails);
    	System.out.println("customUserDetails/getAttributes : " + customUserDetails.getAttributes());
    	
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(customUserDetails.getAttributes(), role, 60*60*2*1000L);

        System.out.println("Success Handler - onAuthenticationSuccess End");
        //ACCESS_TOKEN / 일주일 유지되는 쿠키
        cookieUtil.addCookie(response, "Authorization", token, 60*60*24*7);
//        //REFRESH_TOKEN
//        cookieUtil.addCookie(response, "ACCESS_TOKEN", token, 60*60*24);
        
       

        response.sendRedirect("http://localhost:3000/logincallback");
        return;
    }
}