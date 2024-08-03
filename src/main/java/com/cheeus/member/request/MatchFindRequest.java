package com.cheeus.member.request;

import lombok.Getter;

@Getter
public class MatchFindRequest {
	
	private String member1;
	private String member2;
	
	public MatchFindRequest(String member1, String member2) {
		// TODO Auto-generated constructor stub
		this.member1 = member1;
		this.member2 = member2;
	}
}
