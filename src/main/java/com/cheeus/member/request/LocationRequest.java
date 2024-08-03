package com.cheeus.member.request;

import lombok.Getter;

@Getter
public class LocationRequest {

	private String email;
	private String latitude;
	private String longitude;
	
	public LocationRequest(String email, String latitude, String longitude) {
		// TODO Auto-generated constructor stub
		this.email = email;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	
	// Getter Setter
	
	
}
