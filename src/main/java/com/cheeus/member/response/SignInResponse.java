package com.cheeus.member.response;

public class SignInResponse {

	private String nickname;
    private String email;

    public SignInResponse(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String jwt) {
        this.nickname = jwt;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }
    
}
