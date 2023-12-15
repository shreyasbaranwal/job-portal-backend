package org.job.app.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecruiterDTO {
	
	 	private String name;
	 	
	 	@NotEmpty
		@Size(min = 2, message = "user name should have at least 2 characters")
	    private String username;
	    
	    @NotEmpty
	    @Email(message="Enter valid Email Id.")
	    private String email;
	    
	    @NotEmpty
		@Size(min = 8, message = "password should have at least 8 characters")
	    private String password;
	    
	  
	    private String website;
	    private String company;
	    private String address;
	    
	    @Digits(message="Number should contain 10 digits.", fraction = 0, integer = 10)
	    private long phone;
	    
	    
}
