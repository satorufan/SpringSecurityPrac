package com.cheeus.member.controller;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cheeus.firebase.ImageUploadService;
import com.cheeus.member.domain.MemberProfile;
import com.cheeus.member.response.SignInResponse;
import com.cheeus.member.response.SignUpResponse;
import com.cheeus.member.service.MemberProfileService;
import com.cheeus.member.service.MemberService;

import lombok.RequiredArgsConstructor;

/////// 기능 ///////
//1. 로그인(get) 		/signIn
//2. 회원가입(post) 	/signUp			<- 이 부분에서 파이어베이스에 사진저장 로직 구현 했음.
//3. 회원탈퇴(post)		/delete
//4. 인증코드전송(post)	/send-mms
//5. 인증코드확인(post)	/verify

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberService service;
	private final MemberProfileService profileService;
	private final ImageUploadService imageUploadService;
	
	
	//로그인
	@GetMapping("/signIn")
	public ResponseEntity<SignInResponse> signIn(@RequestParam("email") String email)
			throws IOException{
		//이미 가입된 이메일인지 확인
		service.existByEmail(email);

		//가입된 이메일이면 로그인 완료 Response 리턴
		return ResponseEntity.ok(service.signIn(email));
	}
	
	//토큰 만료 체크
	@GetMapping("/tokenCheck")
	public ResponseEntity<?> tokenCheck(@RequestParam("email") String email) {
		
		return ResponseEntity.ok(service.existByEmail(email));
	}
	
	///////////////////// 이 부분은 테스트 /////////////////////////
	@GetMapping("/signIn2")
	public ResponseEntity<?> signIn2(@RequestParam("email") String email)
			throws IOException{
		//이미 가입된 이메일인지 확인
		System.out.println(email);
		return ResponseEntity.ok(email);
//		service.existByEmail(email);
//
//		//가입된 이메일이면 로그인 완료 Response 리턴
//		return ResponseEntity.ok(service.signIn(email));
	}
	
	@PostMapping("/signIn3")
	public ResponseEntity<?> signIn3(@RequestBody String email)
			throws IOException{
		//이미 가입된 이메일인지 확인
		System.out.println(email);
		return ResponseEntity.ok(email);
//		service.existByEmail(email);
//
//		//가입된 이메일이면 로그인 완료 Response 리턴
//		return ResponseEntity.ok(service.signIn(email));
	}
	//////////////////////////////////////////////////////////
	
	
	
	//회원가입
	@Transactional	//트랜잭션 - 하나라도 수행하다가 뻑나면 SQL로 실행했던 모든 작업 초기화.
	@PostMapping("/signUp")
	public ResponseEntity<SignUpResponse> signUp(
			@RequestPart(value="memberProfileDetail") MemberProfile profile,
			@RequestParam(value="photos") List<MultipartFile> photos,
			@RequestParam(value="email") List<String> imageName
			) throws IOException {
		
		// 파이어베이스에 사진저장
		for(MultipartFile photo : photos) {
			File tmp = imageUploadService.convertToFile( photo , "test" );
			String completeMsg = imageUploadService.uploadFile(
					tmp, 
					"profile/" + imageName.get(photos.indexOf(photo)), 
					photo.getContentType() );
			System.out.println(completeMsg);
		};
		
		// MySQL에 데이터 저장
		service.signUp(profile);
		
		return ResponseEntity.ok(null);
	}
	
	
	// 회원 탈퇴
	@PostMapping("/delete")
	public ResponseEntity<?> deleteProfile(@RequestParam("email") String email) {
		
		profileService.deleteMember(email);
		service.deleteMember(email);
		
		return ResponseEntity.ok("탈퇴 완료");
	}
	
	
	// 전화번호 인증 - 코드 보내기
	@PostMapping("/send-mms")
    public ResponseEntity<?> sendMmsByResourcePath(
    		@RequestParam("tel") String tel
    		) throws IOException {
		
		System.out.println("메시지 보냄");
        return service.sendSmsToAuth(tel);
    }
	
	// 코드 인증 일치 확인
	@PostMapping("/telVerify")
	public ResponseEntity<?> verifyAuthCode(
			@RequestParam("tel") String tel,
			@RequestParam("authCode") String authCode
			) {
		
		System.out.println(tel + " 유저가 입력한 인증번호 : " + authCode);
		return service.verifyAuthCode(tel, authCode);
	}
	
	
	//Redis 테스트
	@GetMapping("/redis")
	public ResponseEntity<?> redisTest() {
		
		return service.test();
	}
	
}
