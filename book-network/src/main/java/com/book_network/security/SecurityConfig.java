package com.book_network.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	private final AuthenticationProvider authenticationProvider;
	private final JwtAuthFilter jwtAuthFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception 
	{
		httpSecurity
					.cors(withDefaults())
					.csrf(AbstractHttpConfigurer::disable)
					.authorizeHttpRequests(
						req ->req.requestMatchers(
													"/auth/**", 
													"/v2/api-docs", 
													"/v3/api-docs", 
													"/v3/api-docs/**",
													"/swagger-resources", 
													"/swagger-resources/**", 
													"/configuration/ui",
													"/configuration/secutity", 
													"/swagger-ui/**", 
													"/webjars/**", 
													"/swagger-ui.html/**")
					.permitAll()
					.anyRequest()
					.authenticated())
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.authenticationProvider(authenticationProvider)
					.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}

}
