package com.cheeus.config.auth.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.cheeus.config.auth.cookie.CookieUtil;
import com.cheeus.config.auth.token.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	System.out.println("-----JWT Filter-----");
    	
    	try {
    		//Authorization 쿠키를 불러온다.
    		Cookie cookie = WebUtils.getCookie(request, "Authorization");
    		//쿠키의 Value를 가져온다.
    		if (cookie != null) {
	    		String authorizationHeader = cookie.getValue();
	    		System.out.println("authorizationHeader : " + authorizationHeader);
	
	            if (authorizationHeader != null) {
	                String token = authorizationHeader;//.substring(7); // "Bearer " 다음의 토큰 부분만 추출
	
	                if (jwtUtil.isExpired(token)) {
	                	
	                	//setAuthentication(token);
                        setAuthentication(request, token);
                        
	
	                    // 토큰 재발급 로직
	                    Date expirationDate = jwtUtil.getExpirationDateFromToken(token);
	                    Date now = new Date();
	                    long remainingTime = expirationDate.getTime() - now.getTime();
	
	                    // 토큰 만료 30분 이내라면 재발급
	                    if (remainingTime < 30 * 60 * 1000) {
	                        String refreshedToken = jwtUtil.refreshToken(token);
	                        setAuthentication(request, refreshedToken);
	                        cookieUtil.addCookie(response, "Authorization", refreshedToken, 60*60*24*7);
	                    }

	                } else {
		            	
		            	cookieUtil.deleteCookie(request, response, "Authorization");
		            	
		            	response.setStatus(HttpStatus.UNAUTHORIZED.value());
		                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		                response.setCharacterEncoding("UTF-8");
		                
		            	return;
		            }
	            }
    		}
    		
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);

    }
    
//    private void setAuthentication(String accessToken) {
//    	System.out.println(accessToken);
//        Authentication authentication = jwtUtil.getAuthentication(accessToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }
    
    private void setAuthentication(HttpServletRequest request, String accessToken) {
    	List<GrantedAuthority> authorities = new ArrayList<>();
    	authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    	
    	SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    	
    	AbstractAuthenticationToken authenticationToken = 
    			new UsernamePasswordAuthenticationToken(jwtUtil.parseClaims(accessToken), null, authorities);
    	
    	authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    	
    	securityContext.setAuthentication(authenticationToken);
    	SecurityContextHolder.setContext(securityContext);
    }
    

    
}