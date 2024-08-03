package com.cheeus.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.cheeus.member.domain.Member;

public class CustomOAuth2User implements OAuth2User {

    private final Member member;
    private Map<String, Object> attributes;

    public CustomOAuth2User(Member member, Map<String, Object> attributes) {

        this.member = member;
        this.attributes = attributes;
    }
    
    

    @Override
    public Map<String, Object> getAttributes() {

        return attributes;
    }
    
    

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

			private static final long serialVersionUID = 1L;

			@Override
            public String getAuthority() {
            	System.out.println("CustomeOAuth2User - getAuthority : " + member.getRole());

                return member.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return member.getEmail();
    }

    public String getRegistrationId() {
    	
    	return member.getRegistrationId();
    }
}
