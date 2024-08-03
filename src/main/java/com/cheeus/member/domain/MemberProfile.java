package com.cheeus.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfile {

	private String email;
	private String name;
	private String nickname;
	private int photo;
	private String tel;
	private String birth;
	private int gender;
	private String tags;
	private boolean matchOk;
	private boolean locationOk;
	private String latitude;
	private String longitude;
	private String location;
	private String intro;
	
}
