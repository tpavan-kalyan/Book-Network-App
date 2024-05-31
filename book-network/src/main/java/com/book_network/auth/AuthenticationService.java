package com.book_network.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.book_network.email.EmailService;
import com.book_network.email.EmailTemplateName;
import com.book_network.role.RoleRepository;
import com.book_network.security.JwtService;
import com.book_network.user.Token;
import com.book_network.user.TokenRepository;
import com.book_network.user.User;
import com.book_network.user.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final EmailService emailService;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	
	@Value("${application.mailing.frontend.activation-url}")
	private String activationUrl;
	
	public void register(RegistrationRequest request) throws MessagingException {
		
		var userRole = roleRepository
							.findByName("USER")
							.orElseThrow(
								() -> new IllegalStateException("ROLE USER was not initialized"));
		
		var user = User.builder()
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.accountLocked(false)
				.enabled(false)
				.roles(List.of(userRole))
				.build();
		
		userRepository.save(user);
		sendValidationEmail(user);
	}

	private void sendValidationEmail(User user) throws MessagingException {
		// TODO Auto-generated method stub
		var newToken = generateAndSendActivationToken(user);
		
		emailService.sendEmail(
					user.getEmail(),
					user.getFullName(),
					EmailTemplateName.ACTIVATE_ACCOUNT,
					activationUrl,
					newToken,
					"Account Activation"
				);
		
	}

	private String generateAndSendActivationToken(User user) {
		
		String generateToken = generateAcivationCode(6);
		
		var token = Token.builder()
							.token(generateToken)
							.createdAt(LocalDateTime.now())
							.expiredAt(LocalDateTime.now().plusMinutes(15))
							.user(user)
							.build();
		
		tokenRepository.save(token);
		
		return generateToken;
	}
	
	
// this method is used to generate the 6 digit code
	private String generateAcivationCode(int length) {
		
		String charecter = "0123456789";
		StringBuilder codeBuilder = new StringBuilder();
		SecureRandom secureRandom = new SecureRandom();
		
		for(int i = 0; i<length; i++) {
			int reandomIndex = secureRandom.nextInt(charecter.length());//0-9
			codeBuilder.append(charecter.charAt(reandomIndex));
		}
		
		return codeBuilder.toString();
	}
	
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		var auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(), 
						request.getPassword())	
				);
		
		var claims = new HashMap<String,Object>();
		var user = ((User)auth.getPrincipal());
		
		claims.put("fullName", user.getFullName());
		
		var jwtToken = jwtService.generateToken(claims, user);
		
		return AuthenticationResponse.builder()
				.token(jwtToken).build();
	}

	//@Transactional
	public void activateAccount(String token) throws MessagingException {
		
		Token savedToken = tokenRepository.findByToken(token)
								.orElseThrow(() -> new RuntimeException("Invalid token"));
		if(LocalDateTime.now().isAfter(savedToken.getExpiredAt()))		
		{
			sendValidationEmail(savedToken.getUser());
			throw new RuntimeException("Activation token has been expired. A new token has been send to email address");
		}
		
		var user = userRepository.findById(savedToken.getUser().getId())
										.orElseThrow(() -> new UsernameNotFoundException("User not found") ); 
		user.setEnabled(true);
		userRepository.save(user);
		savedToken.setValidatedAt(LocalDateTime.now());
		tokenRepository.save(savedToken);
	}

}
