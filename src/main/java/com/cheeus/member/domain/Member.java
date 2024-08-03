package com.cheeus.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


// 이 클래스는 OAuth 의 정보를 담은 공간입니다.

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

	private int id;
	private String email;
	private String role;
	private String registrationId;
	
}
