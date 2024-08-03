package com.cheeus.member.response;

public class SignUpResponse {

	private String email;

    public SignUpResponse(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
