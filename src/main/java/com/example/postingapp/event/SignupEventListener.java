package com.example.postingapp.event;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.postingapp.entity.User;
import com.example.postingapp.service.VerificationTokenService;

@Component
public class SignupEventListener {
	private final VerificationTokenService verificationTokenService;
	private final JavaMailSender javaMailSender;
	private static final Logger logger = LoggerFactory.getLogger(SignupEventListener.class);
	
	public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
		this.verificationTokenService = verificationTokenService;
		this.javaMailSender = mailSender;
	}
	
	@EventListener
	private void onSignupEvent(SignupEvent signupEvent) {
		logger.info("【DEBUG】認証メール送信イベントを開始します。ユーザーID: {}", signupEvent.getUser().getId());
		User user = signupEvent.getUser();
		String token = UUID.randomUUID().toString();
		verificationTokenService.create(user, token);
		
		String senderAddress = "samurai@sandbox5f14d4f807704dd6b61773e787fd9728.mailgun.org";
		String recipientAddress = user.getEmail();
		String subject = "メール認証";
		String confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;
		String message = "以下のリンクをクリックして会員登録を完了してください。";
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(senderAddress);
		mailMessage.setTo(recipientAddress);
		mailMessage.setSubject(subject);
		mailMessage.setText(message + "\n" + confirmationUrl);
		try {
	        // メール送信処理
	        javaMailSender.send(mailMessage);
	        logger.info("【DEBUG】認証メールの送信に成功しました。宛先: {}", recipientAddress); // <-- 成功時のログ

	    } catch (Exception e) {
	        // 例外が発生した場合、スタックトレースを含めてログに出力
	        logger.error("【ERROR】認証メールの送信中に致命的なエラーが発生しました。宛先: {}", recipientAddress, e); // <-- 失敗時のログを強化

	        // ここで例外を再スローしないことで、DB登録後のリダイレクト処理を続行させます。
	    }
	} 
}
