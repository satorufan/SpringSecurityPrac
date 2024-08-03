package com.cheeus.member.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cheeus.firebase.ImageUploadService;
import com.cheeus.member.domain.MemberPopularity;
import com.cheeus.member.domain.MemberProfile;
import com.cheeus.member.exception.MemberException;
import com.cheeus.member.repository.MemberProfileDao;
import com.cheeus.member.request.LocationRequest;

import lombok.RequiredArgsConstructor;

/////// 기능 ///////
//1. 닉네임 중복 확인 로직
//2. 회원 정보 로드 로직
//3. 회원 정보 수정 로직 (파이어베이스 + MySQL)
//*4. 회원탈퇴 로직 -- 아직 프론트와 연결하지 않음
//5. 위치 및 매칭 동의 업데이트 로직
//6. 좋아요 로직

@RequiredArgsConstructor
@Service
public class MemberProfileService {
	
	private final MemberProfileDao profileDao;
	private final ImageUploadService imageUploadService;
	
	
	// 닉네임 중복 확인
	public HttpStatus existNickname (String nickname) {
		
		Integer existNickname = profileDao.existNickname(nickname);
		
		if (existNickname > 0) {
			throw new MemberException("이미 존재하는 닉네임 입니다.", HttpStatus.BAD_REQUEST);
		}
		
		return HttpStatus.OK;
	}
	
	
	// 회원 정보 불러오기
	public MemberProfile findByEmail (String email) {
		
		MemberProfile findMember = profileDao.findByEmail(email);
		
		return findMember;
	}
	
	
	// 회원 수정
	@Transactional
	public MemberProfile updateMember (
			MemberProfile memberProfile,
			List<MultipartFile> photos,
			List<String> imageName) throws IOException {
		
		// 파이어베이스에 사진저장
		for(MultipartFile photo : photos) {
			System.out.println("updateMember");
			File tmp = imageUploadService.convertToFile( photo , "test" );
			String completeMsg = imageUploadService.uploadFile(
					tmp, 
					"profile/" + imageName.get(photos.indexOf(photo)),
					photo.getContentType() );
			
			System.out.println(completeMsg);
		};
		
		// 정보 수정
		profileDao.updateMember(memberProfile);
		
		return memberProfile;
	}
	
	
	// 회원 탈퇴
	public HttpStatus deleteMember (String email) {
		
		MemberProfile findMember = findByEmail(email);
		
		if (findMember == null) {
			throw new MemberException("존재하지 않은 아이디입니다.", HttpStatus.BAD_REQUEST);
		}
		
		profileDao.deleteMember(email);
		
		return HttpStatus.OK;
	}
	
	
	//위치 및 매칭 동의
	public void allowLocationMatching(String email, String type, String latitude, String longitude) {
		if (type.equals("location")) profileDao.allowLocation(new LocationRequest(email, latitude, longitude));
		else profileDao.allowMatching(email);
	}
	
	
	
	// 좋아요 목록 불러오기
	public ArrayList<MemberPopularity> loadPopularity(String email) {
		
		ArrayList<MemberPopularity> popularities = profileDao.findPopularity(email);
		
		return popularities;
	}
	
	// 좋아요 개수 불러오기
	public Integer findPopularity (String email) {
		
		return profileDao.countPopularity(email);
	}
	
	
	// 내가 좋아요 눌렀는가
	public boolean isClickedPopularity (MemberPopularity popularity) {
		
		//liker는 현재 로그인 유저, 나.
		if (popularity.getLiker() == null) {
			throw new MemberException("로그인 해주세요.", HttpStatus.BAD_REQUEST);
		} else {
			
			Integer check = profileDao.existPopularity(popularity);
			
			if (check == 0) {
				// 좋아요 안눌름
				return false;
			}
			
			// 눌름
			return true;
		}
	}
	
	// 좋아요 추가/삭제
	public void addPopularity (MemberPopularity popularity) {
		
		if (!isClickedPopularity(popularity)) {
			// 좋아요가 없으면 좋아요 추가
			profileDao.addPopularity(popularity);
			
		} else {
			// 있으면 좋아요 삭제
			profileDao.deletePopularity(popularity);
		}
	}
	

}
