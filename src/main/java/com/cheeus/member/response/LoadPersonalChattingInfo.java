package com.cheeus.member.response;

public class LoadPersonalChattingInfo {

	private String email;
	private String nickname;
	private byte[] imageBlob;
	private String imageType;
	
	public LoadPersonalChattingInfo(String email, String nickname, byte[] imageBlob, String imageType) {
		// TODO Auto-generated constructor stub
		this.email = email;
		this.nickname = nickname;
		this.imageBlob = imageBlob;
		this.imageType = imageType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public byte[] getImageBlob() {
		return imageBlob;
	}

	public void setImageBlob(byte[] imageBlob) {
		this.imageBlob = imageBlob;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	
	
}
