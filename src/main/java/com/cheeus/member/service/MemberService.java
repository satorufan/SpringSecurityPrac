package com.cheeus.member.service;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cheeus.member.domain.MemberProfile;
import com.cheeus.member.exception.MemberException;
import com.cheeus.member.repository.MemberDao;
import com.cheeus.member.repository.MemberProfileDao;
import com.cheeus.member.response.SignInResponse;
import com.cheeus.member.response.SignUpResponse;
import com.cheeus.sms.SMSUtil;

import lombok.RequiredArgsConstructor;

/////// 기능 ///////
// 1. 이미 존재하는 이메일 확인 로직
// 2. 로그인 로직 -> email을 받아서 nickname을 응답함
// 3. 회원가입 로직
// *4. 회원탈퇴 로직 -- 아직 프론트와 연결하지 않음
// 5. 전화번호 인증 로직

@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberDao dao;
	private final MemberProfileDao profileDao;
	// 전화번호 인증
	private final SMSUtil smsUtil;
	private final RedisTemplate<String, Object> redisTemplate;
	private final int MAX_ATTEMPTS = 5;	// 최대 시도 횟수
	private final int BLOCK_DURATION = 1; // 5회 모두 실패시 1분동안 인증 제한.
	private final int AUTH_CODE_EXPIRY = 3; // 3분동안 아무 입력이 없으면 만료.

	
	// 가입 시 이미 존재하는 회원인지 확인
	public HttpStatus existByEmail(String email) {
		
		try {
			
			Integer existMember = dao.existByEmail(email);
			if (existMember == 0) {
				throw new MemberException("존재하지 않는 이메일입니다.", HttpStatus.BAD_REQUEST);
			}
			return HttpStatus.OK;
		
		} catch (MemberException e) {
			throw new MemberException("존재하지 않는 이메일입니다.", HttpStatus.BAD_REQUEST);
		}
		
	}
	

	// 로그인
	public SignInResponse signIn(String email) throws IOException {
		// 회원정보 존재 확인
		Integer existMember = dao.existByEmail(email);
		String nickname = profileDao.findByEmail(email).getNickname();
		
		if (existMember == 0) {
			return new SignInResponse(null, null);
		}
		
		return new SignInResponse(nickname, email);
	}
	
	
	
	// 회원가입
	@Transactional
	public SignUpResponse signUp(MemberProfile profile) {
		
		dao.createMember(profile.getEmail());	//member DB에 저장
		
		profileDao.createMember(profile);	//profile DB에 저장
		
		return new SignUpResponse(profile.getEmail());
		
	}
	
	// 회원 탈퇴
	public void deleteMember(String email) {
		dao.deleteMember(email);
	}
	
	
	
	/////////////////// 전화번호 인증 ////////////////////////////
	
	
	// 전화번호 인증 - 인증코드 생성 후 메시지 보내기
	public ResponseEntity<?> sendSmsToAuth(String tel) {
		
		String authCodeKey = "auth:code:" + tel;
        String attemptsKey = "auth:attempts:" + tel;
        String blockKey = "auth:block:" + tel;
		
        System.out.println("block? : " + redisTemplate.opsForValue().get(blockKey));
        if (redisTemplate.opsForValue().get(blockKey) == null) {
			// 인증코드 생성
			String authCode = generateAuthCode();
			redisTemplate.opsForValue().set(authCodeKey, authCode, AUTH_CODE_EXPIRY, TimeUnit.MINUTES);
			redisTemplate.opsForValue().set(attemptsKey, 0, AUTH_CODE_EXPIRY, TimeUnit.MINUTES);
			return smsUtil.sendOne(tel, authCode);
        } else {
        	
        	return new ResponseEntity<>("5회이상 실패하였습니다. 잠시후에 다시 시도해주세요.", HttpStatus.OK);
        }
		
	}
	
	// 인증코드 생성
	public String generateAuthCode () {
		
		Random random = new Random();
		
		return String.format("%06d", random.nextInt(999999));
	}
	
	
	// 입력받은 코드 인증하기
	public ResponseEntity<?> verifyAuthCode(String tel, String authCode) {
		
		String authCodeKey = "auth:code:" + tel;	//인증코드
        String attemptsKey = "auth:attempts:" + tel;	//인증 기회 횟수
        String blockKey = "auth:block:" + tel;		//실패시 사용할 키

        Integer attempts = (Integer) redisTemplate.opsForValue().get(attemptsKey);
        if (attempts != null && attempts >= MAX_ATTEMPTS) {

        	// 인증 모두 실패시 대기시간 설정
        	if (redisTemplate.opsForValue().get(blockKey) == null) {
        		
                redisTemplate.opsForValue().set(blockKey, "blocked", BLOCK_DURATION, TimeUnit.MINUTES);

        	}
            
        	return new ResponseEntity<>("5회이상 실패하였습니다. 잠시후에 다시 시도해주세요.", HttpStatus.OK);
        }
        
		String storedCode = (String) redisTemplate.opsForValue().get(authCodeKey);
		
		// 인증번호가 일치하지 않을 경우
		if (storedCode == null || !storedCode.equals(authCode)) {
			// 인증 실패 시 시도 횟수 증가
            redisTemplate.opsForValue().increment(attemptsKey, 1);
            attempts = (Integer) redisTemplate.opsForValue().get(attemptsKey);
            

            return new ResponseEntity<>(attempts, HttpStatus.OK);
		}
		
		// 인증번호가 일치할 경우
		redisTemplate.delete(authCodeKey);
		redisTemplate.delete(attemptsKey);
		return new ResponseEntity<>("success", HttpStatus.OK);
		
	}
	
	
	
	//Redis 테스트
	public ResponseEntity<?> test () {
		
		String redisKey = "test";
		String cnt = "cnt";
		
		if (redisTemplate.opsForValue().get(redisKey) != null) {
			
			System.out.println("redis code : " + redisTemplate.opsForValue().get(redisKey));
			redisTemplate.opsForValue().increment(cnt, 1);
			System.out.println(redisTemplate.opsForValue().get(cnt));
			if((Integer) redisTemplate.opsForValue().get(cnt) > 5) {
				

				redisTemplate.delete(redisKey);
				redisTemplate.opsForValue().set(cnt, 0);
				
				return new ResponseEntity<>("5회 넘음, 레디스에 있는 데이터가 삭제됩니다.", HttpStatus.BAD_REQUEST);
				
			}
		} else {
			System.out.println("만료됨");

			String authCode = generateAuthCode();
			redisTemplate.opsForValue().set(redisKey, authCode, AUTH_CODE_EXPIRY, TimeUnit.MINUTES);
			
			System.out.println("인증코드 생성 : " + redisTemplate.opsForValue().get(redisKey));
		}
		

		return new ResponseEntity<>("레디스 테스트 : " + redisTemplate.opsForValue().get(redisKey), HttpStatus.OK);
	}
	
}
