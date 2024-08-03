package com.cheeus.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.cheeus.config.auth.CustomOAuth2UserService;
import com.cheeus.config.auth.filter.JwtAuthFilter;
import com.cheeus.config.auth.handler.OAuth2FailureHandler;
import com.cheeus.config.auth.handler.OAuth2SuccessHandler;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity // Spring Security 요청할때 로그인 해야함. 
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
	
	private final CustomOAuth2UserService oAuth2UserService;
	private final OAuth2SuccessHandler successHandler;
	private final OAuth2FailureHandler failureHandler;
    private final JwtAuthFilter jwtAuthFilter;
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
        return web -> web.ignoring()
                .requestMatchers("/error", "/favicon.ico", "/profileUploads/**","/messageUploads/**", "/js/**","/webjars/**");
    }
    

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
        http
        	.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
        	.csrf(AbstractHttpConfigurer::disable)
        	.httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
            .formLogin(AbstractHttpConfigurer::disable) // 기본 login form 비활성화
            .logout(AbstractHttpConfigurer::disable) // 기본 logout 비활성화
            .headers(c -> c.frameOptions(
                    FrameOptionsConfig::disable).disable()) // X-Frame-Options 비활성화
            .sessionManagement(c ->
                    c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음
            
            //로그인 한사람은 /member로 시작하는 url 모두 허용.
            .authorizeHttpRequests(a -> a
                    //.requestMatchers("/member/**").permitAll()//.authenticated()
            		.requestMatchers("/member/**").hasRole("USER")
            		.requestMatchers("http://localhost:8080/login/oauth2/code/**").authenticated()
            		//.requestMatchers("/member/signIn").authenticated()//.hasRole("USER")
                    //.anyRequest().authenticated()
            		.anyRequest().permitAll()
                );
            
	        
	    http
	    
	    	.addFilterBefore(jwtAuthFilter, 
	    			UsernamePasswordAuthenticationFilter.class)
	    	.logout((logout) -> logout
	    			.deleteCookies("Authorization")
	    			.logoutSuccessUrl("http://localhost:3000/"))
            .oauth2Login((oauth2) -> oauth2
            		.successHandler(successHandler)
            		.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                            .userService(oAuth2UserService))
            		.failureHandler(failureHandler)
            		)
            ;
            
//            .loginPage("/token/expired")
//            .successHandler(successHandler)
//            .userInfoEndpoint().userService(oAuth2UserService);
        
//	        .oauth2Login(oauth -> // OAuth2 로그인 기능에 대한 여러 설정의 진입점
//            // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
//            oauth.userInfoEndpoint(c -> c.userService(oAuth2UserService))
//                    // 로그인 성공 시 핸들러
//                    .successHandler(oAuth2SuccessHandler)
//	        		)
//	        .addFilterBefore(tokenAuthenticationFilter,
//                    UsernamePasswordAuthenticationFilter.class)
//            .addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass()) // 토큰 예외 핸들링
//
//            // 인증 예외 핸들링
//            .exceptionHandling((exceptions) -> exceptions
//                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
//                    .accessDeniedHandler(new CustomAccessDeniedHandler())); 
        
        return http.build();
    }
	
//  CORS 설정
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Arrays.asList(
            		"http://localhost:3000"
            		)); // ⭐️ 허용할 origin
            config.setAllowCredentials(true);
            
//            config.setExposedHeaders(Collections.singletonList("Set-Cookie"));
//            config.setExposedHeaders(Collections.singletonList("Authorization"));
            return config;
        };
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
}
