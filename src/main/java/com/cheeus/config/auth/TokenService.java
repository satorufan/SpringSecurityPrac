//package com.cheeus.config.auth;
//
//import java.util.Base64;
//import java.util.Date;
//
//import org.springframework.stereotype.Service;
//
//import com.cheeus.config.auth.domain.Token;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Jwts;
//import jakarta.annotation.PostConstruct;
//
//@Service
//public class TokenService{
//    private String secretKey = "token-secret-key";
//
//    @PostConstruct
//    protected void init() {
//        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
//    }
//
//
//    public Token generateToken(String uid, String role) {
//
//        Claims claims = Jwts.claims().setSubject(uid);
//        claims.put("role", role);
//        
//        System.out.println("generateToken : " + claims);
//
//        return new Token(
//                "accesstoken",
//                "refreshToken");
//    }
//
//
//    public boolean verifyToken(String token) {
//        try {
//            Jws<Claims> claims = Jwts.parserBuilder()
//                    .setSigningKey(secretKey)
//                    .build()
//                    .parseClaimsJws(token);
//            
//            System.out.println("Token : " + claims);
//            
//            return claims.getBody()
//                    .getExpiration()
//                    .after(new Date());
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//
//    public String getUid(String token) {
//    	System.out.println("getUid Token : " + token);
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
//    }
//}
