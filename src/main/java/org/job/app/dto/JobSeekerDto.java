package org.job.app.dto;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class JobSeekerDto {
	
	
	@NotEmpty
    private String name;
       
	
	// user name should not be null or empty
	// user name should have at least 2 characters
	@Column(name = "name", nullable = false)	
	@NotEmpty
	@Size(min = 2, message = "user name should have at least 2 characters")
    private String username;
    
	@NotEmpty
    @Email(message="Enter valid Email Id.")	
    private String email;
    
    @NotEmpty
	@Size(min = 8, message = "password should have at least 8 characters")
    private String password;
}
