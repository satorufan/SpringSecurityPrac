package com.cheeus.config.auth.token;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
                .setExpiration(new Date(now.getTime() + expiredMs))
                .compact();
        
    }
    
    
    // 토큰 재발급
    public String refreshToken(String token) {
        try {
        	
        	Authentication authentication = getAuthentication(token);
        	
            Claims claims = parseClaims(token);
            List<SimpleGrantedAuthority> authorities = getAuthorities(claims);
        	
            String role = authorities.get(0).toString();
            
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
        
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	return authentication;
    }
    
    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
    	
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get("role").toString()));
    }
    
    
    public Claims parseClaims(String token) {
    	
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
