package com.cheeus.member.controller;


import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cheeus.member.service.MemberMatchService;

import lombok.RequiredArgsConstructor;

/////// 기능 ///////
//1. 매칭 프로필 로드(get)
//2. 스와이프(post)			/swipe
//3. 채팅방에서 표시할 유저 불러오기(get)	/chattingPersonal
//4. 단체챗에서 표시할 유저 불러오기(get)	/chattingTogether

@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MemberMatchController {
	
	private final MemberMatchService memberMatchService;

	@GetMapping
	public ResponseEntity<?> loadProfiles (@RequestParam("email") String email ) {
		try {
            // Return response with HTTP status 200 OK
			return ResponseEntity.ok(memberMatchService.findAll(email));
        } catch (Exception e) {
            // Handle IOException appropriately
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load profile and image: " + e.getMessage());
        }
	}
	
	// 매칭시스템 로직
	@PostMapping("/swipe")
	public ResponseEntity<?> findMatchRooms (
			@RequestParam("member1") String member1,
			@RequestParam("member2") String member2,
			@RequestParam("type") String type
			) {
		
		return ResponseEntity.ok(memberMatchService.match(member1, member2, type));
		
	}
	
	// 채팅유저 정보 불러오기
	@GetMapping("/chattingPersonal")
	public ResponseEntity<?> loadChattingPersonal (
			@RequestParam("email") String email
			) throws IOException {
		
		return ResponseEntity.ok(memberMatchService.loadPersonalChattingInfo(email));
	}
	
	// 단체 채팅유저 정보 불러오기
	@GetMapping("/chattingTogether")
	public ResponseEntity<?> loadChattingTogether (
			@RequestParam("email") String email
			) throws IOException {
		
		return ResponseEntity.ok(memberMatchService.loadPersonalChattingInfo(email));
	}
	
}
