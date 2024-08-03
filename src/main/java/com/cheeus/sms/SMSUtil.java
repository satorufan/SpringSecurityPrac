package com.cheeus.sms;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Component
public class SMSUtil {
	
	private DefaultMessageService messageService;
	
	@Value("${coolsms.api.key}")
	private String apikey;
	
	@Value("${coolsms.api.secret}")
	private String apiSecretKey;
	
	@Value("${coolsms.api.telnum")
	private String from;
	
	@PostConstruct
	private void init () {
		this.messageService = NurigoApp.INSTANCE.initialize(apikey, apiSecretKey, "https://api.coolsms.co.kr");
	}
	
	// 단일 메시지 발송 예제
    public ResponseEntity<?> sendOne(String to, String verificationCode) {
        Message message = new Message();
		// 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
        message.setFrom(from);
        message.setTo(to);
        message.setText("아래의 인증번호를 입력해주세요\n" + verificationCode);

        try {
        	
	        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
	        System.out.println(response);
	        return ResponseEntity.ok(response);
        } catch (Exception e) {
        	
        	System.out.println(e.getMessage());
        	return new ResponseEntity<>("fail", HttpStatus.BAD_REQUEST);
        }
        
    }
}
