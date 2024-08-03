package com.cheeus.config.auth.token;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.cheeus.config.auth.CustomOAuth2User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTUtil {
	
	@Value("${spring.jwt.secret}")
	private String secretkey;


    public String getEmail(String token) {

    	System.out.println("token : " + token);
    	System.out.println("email : " + parseClaims(token).get("email", String.class));
    	
    	return parseClaims(token).get("email", String.class);
    }

    public String getRole(String token) {

    	System.out.println("getRole : " + token);
    	
    	return parseClaims(token).get("role", String.class);
    }

    public Boolean isExpired(String token) {
    	
    	System.out.println("JWTUtil - token expired check : "+ token);
    	
		try {
			return parseClaims(token)
	        		.getExpiration()
	        		.after(new Date());
	        
		} catch (Exception e) {
			System.out.println("만료 되었습니다!");
			return false;
		}
    }
    
    //토큰 생성
    public String createJwt(
    		Map<String, Object> attributes,
    		String role, 
    		Long expiredMs) {

    	Key key = Keys.hmacShaKeyFor(secretkey.getBytes(StandardCharsets.UTF_8));
    	
    	Claims claims = Jwts.claims();
		claims.put("registrationId", attributes.get("registrationId"));
        claims.put("email", attributes.get("email"));
        claims.put("role", role);
        
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
        		.setSubject((String) attributes.get("id"))
        		.setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 10000))
                .compact();
        
    }
    
    
    // 토큰 재발급
    public String refreshToken(String token) {
        try {
        	
        	Authentication authentication = getAuthentication(token);
//        	System.out.println("principal : " + authentication.getPrincipal());
//        	CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//        	System.out.println("principal");
        	
            String role = "ROLE_USER";
            
//            System.out.println("customOAuth2User : " + customOAuth2User);
            
            return createJwt((Map<String, Object>) authentication.getPrincipal(), role, 60*60*60L);
        } catch (Exception e) {
            return null;
        }
    }
    
    
    // 토큰 만료 시간 확인
    public Date getExpirationDateFromToken(String token) {
    	
    	Claims claims = parseClaims(token);
    	
        return claims.getExpiration();
    }
    
    
    //인증된 토큰인지 확인하기 위해 토큰을 디코딩하는 작업.
    public Authentication getAuthentication(String token) {
    	System.out.println("JWTUtil - getAuthentication : " + token);
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);
        
        
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	System.out.println(authentication);
    	
		
        return new UsernamePasswordAuthenticationToken(claims, null, authorities);
//    	return authentication;
    }
    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get("role").toString()));
    }
    
    
    private Claims parseClaims(String token) {
    	
    	Key key = Keys.hmacShaKeyFor(secretkey.getBytes(StandardCharsets.UTF_8));
    	
        try {
            return Jwts
            		.parserBuilder()
            		.setSigningKey(key)
            		.build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (MalformedJwtException e) {
            throw e;
        } catch (SecurityException e) {
            throw e;
        }
    }
    
}
