package com.book_network.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest 
{
	@NotEmpty(message = "Firstname is mandatory")
	@NotBlank(message = "Firstname is mandatory")
    private String firstName;
	
	@NotEmpty(message = "Lastname is mandatory")
	@NotBlank(message = "Lastname is mandatory")
    private String lastName;
	
	@Email(message = "Emaill is not formatted --> xyz@email.com")
	@NotEmpty(message = "Email is mandatory")
	@NotBlank(message = "Email is mandatory")
    private String email;
	
	@Size(min = 8, message = "Password should be 8 charecters minimum")
    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    private String password;
    
}
