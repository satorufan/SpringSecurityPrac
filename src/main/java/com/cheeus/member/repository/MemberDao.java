package com.cheeus.member.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MemberDao {

	// 로그인 시 존재하는 이메일인지 확인
	Integer existByEmail(String email);
	
	// 회원 가입 ( 기본 정보 : 이메일 )
	void createMember(String email);
	
	// 회원 탈퇴
	void deleteMember(String email);
}
