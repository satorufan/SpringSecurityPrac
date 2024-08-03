package com.cheeus.member.repository;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.cheeus.member.domain.MemberMatch;
import com.cheeus.member.domain.MemberProfile;
import com.cheeus.member.request.MatchFindRequest;

@Mapper
@Repository
public interface MemberMatchDao {

	// 매치 카드 불러오기
	ArrayList<MemberProfile> findAll (String email);
	
	
	
	// 매치 불러오기 - 둘중 한명이라도 스와이프 했는가?
	Integer findMatchRooms (MatchFindRequest findRequest);
	
	// 매치상태 불러오기
	Integer loadMatchState (MatchFindRequest findRequest);
	
	
	// 내가 선택한 카드 불러오기
	ArrayList<String> findMyMatch (String email);
	
	
	
	// 매치 방 생성 - 매치 컨트롤
	void createMatchRooms (MemberMatch memberMatch);
	
	// 매치 업데이트
	void MatchUpdate (MemberMatch memberMatch);
	
	// 매치 최종 성공!
	MemberMatch successMatch (MatchFindRequest findRequest);
}
