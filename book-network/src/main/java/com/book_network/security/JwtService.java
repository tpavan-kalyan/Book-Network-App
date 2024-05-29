package com.book_network.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
public class JwtService {

	//@Value("${application.security.jwt.secret-key}")
	private String secretKey;
	//@Value("${application.security.jwt.expiration}")
	private long jwtExpiration;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	//Step-1: Secret Key Generation for JWT Token
	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	// Step-2: JWT Token Generation Process
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}
	private String generateToken(Map<String, Object> tokenMap, UserDetails userDetails) {
		return buildToken(tokenMap, userDetails, jwtExpiration);
	}
	private String buildToken(Map<String, Object> tokenMap, UserDetails userDetails, long jwtExpiration) {
		var authorities = userDetails
								.getAuthorities()
								.stream()
								.map(GrantedAuthority::getAuthority)
								.toList();
		return Jwts
					.builder()
					.setClaims(tokenMap)
					.setSubject(userDetails.getUsername())
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
					.claim("authorities", authorities)
					.signWith(getSignInKey()).compact()
				;
	}
	
	// Step-3: Checking the weather the JWT token is VALID or NOT
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !istokenExpaired(token));
	}
	
	// Step-4: Checking the weather the JWT token is Expired or Not
	private boolean istokenExpaired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	private Date extractExpiration(String token) {
		// TODO Auto-generated method stub
		return extractClaim(token, Claims::getExpiration);
	}
	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts
					.parserBuilder()
					.setSigningKey(getSignInKey())
					.build()
					.parseClaimsJws(token)
					.getBody();
	}
}
