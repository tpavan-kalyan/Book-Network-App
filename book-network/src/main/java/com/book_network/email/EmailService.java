package com.book_network.email;

import static java.nio.charset.StandardCharsets.*;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import static org.springframework.mail.javamail.MimeMessageHelper.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	
	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;
	
	//something to be added
	@Async
	public void sendEmail(
				String to,
				String userName,
				EmailTemplateName emailTemplate,
				String confirmationUrl,
				String activationCode,
				String subject
				
			) throws MessagingException {
		
		String templateName;
		
		if (emailTemplate == null) {
			templateName = "confirm -emal";
		}else {
			templateName = emailTemplate.name();
		}
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		
		MimeMessageHelper messageHelper = new MimeMessageHelper(
							mimeMessage,
							MULTIPART_MODE_MIXED,
							UTF_8.name()
		);
		
		Map<String, Object> properties = new HashMap<>();
		properties.put("username", userName);
		properties.put("confirmationUrl", confirmationUrl);
		properties.put("activation_code", activationCode);
	
		Context context = new Context();
		
		context.setVariables(properties);
		
		messageHelper.setFrom("contact@booknetwork.com");
		messageHelper.setTo(to);
		messageHelper.setSubject(subject);
		
		String template = templateEngine.process(templateName, context);
		
		messageHelper.setText(template, true);
		
		mailSender.send(mimeMessage);
		
	}
		
	
}
	

