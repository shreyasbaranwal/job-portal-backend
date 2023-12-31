package org.job.app.controller;

import org.job.app.dto.LoginDto;
import org.job.app.dto.RecruiterDTO;
import org.job.app.dto.JobSeekerDto;
import org.job.app.exception.ResourceNotFoundException;
import org.job.app.model.JobSeeker;
import org.job.app.model.Recruiter;
import org.job.app.model.Role;
import org.job.app.repository.RoleRepository;
import org.job.app.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200/")
@RestController
@RequestMapping("/api/auth/recruiter/")
@OpenAPIDefinition(info = @Info(title = "Recruiter API", version = "v1"))
public class RecruiterController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RecruiterRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder; 

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto loginDto){
    	Map<String, String> response=new HashMap<>();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        response.put("message", "User signed-in successfully!");
        response.put("recruiterId", String.valueOf(this.userRepository.findByEmail(loginDto.getUsernameOrEmail()).get().getId()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody JobSeekerDto signUpDto){
    	Map<String, String> response=new HashMap<>();
        // add check for username exists in a DB
        if(userRepository.existsByUsername(signUpDto.getUsername())){
        	
        	response.put("message", "Username is already taken!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // add check for email exists in DB
        if(userRepository.existsByEmail(signUpDto.getEmail())){
        	
        	response.put("message","Email is already taken!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // create user object
        Recruiter user = new Recruiter();
        user.setName(signUpDto.getName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        Role roles = roleRepository.findByName("ROLE_RECRUITER").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);
        response.put("message", "JobSeeker registered successfully");
        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);

    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Recruiter> getRecruiterById(@PathVariable long id)
    {
    	return new  ResponseEntity<Recruiter>(this.userRepository.findById(id).get(),HttpStatus.OK);
    }
    
    @PutMapping("/profile/update/{recruiterId}")
    public ResponseEntity<?> profileUpdate(@Valid @RequestBody RecruiterDTO recruiter,@PathVariable("recruiterId") long recruiterId )
    {
    	Optional<Recruiter> recruiterDBObj=this.userRepository.findById(recruiterId);
    	
    	if(recruiterDBObj.isPresent())
    	{
    		recruiterDBObj.get().setAddress(recruiter.getAddress());
    		recruiterDBObj.get().setEmail(recruiter.getEmail());
    		recruiterDBObj.get().setName(recruiter.getName());
    		recruiterDBObj.get().setPhone(recruiter.getPhone());
    		recruiterDBObj.get().setWebsite(recruiter.getWebsite());
    		recruiterDBObj.get().setUsername(recruiter.getUsername());
    		recruiterDBObj.get().setCompany(recruiter.getCompany());
        	return new  ResponseEntity<Recruiter>(this.userRepository.save(recruiterDBObj.get()),HttpStatus.OK);

    	}
    	else
    	{
        	return new  ResponseEntity<String>("Recruiter not found!!",HttpStatus.NOT_FOUND);

    	}
    }
    
    @PutMapping("/password/{recruiterId}")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,@PathVariable("recruiterId")long recruiterId  )
    {
    		Map<String, String> response=new HashMap<>();
    		Recruiter obj=this.userRepository.findById(recruiterId).get();
    		//System.out.println(this.bCryptPasswordEncoder.encode(oldPassword));
    		//System.out.println(obj.getPassword());
    		if(bCryptPasswordEncoder.matches(oldPassword, obj.getPassword()))
    		{
    			
    			obj.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
    			this.userRepository.save(obj);
    			response.put("status", "true");
    			response.put("message", "Password updated!!");
    			return new  ResponseEntity<Map<String, String>>(response,HttpStatus.OK);
    		}
    		else
    		{
    			response.put("status", "false");
    			response.put("message", "Error in password!!");
    			return new  ResponseEntity<Map<String, String>>(response,HttpStatus.BAD_REQUEST);
    		}
    	
    }
    
    @PutMapping("/picture/{recruiterId}")
    public ResponseEntity<Recruiter>updateProfilePic(@RequestParam("image") MultipartFile image,@PathVariable long recruiterId )
    {
    	
    		Recruiter obj=this.userRepository.findById(recruiterId).orElseThrow(() -> new ResourceNotFoundException("Recruiter", "id", recruiterId));;
    		try 
    		{
				obj.setLogo(image.getBytes());
			} 
    		catch (IOException e) {
				
				e.printStackTrace();
			}
    	
    	return new  ResponseEntity<Recruiter>(this.userRepository.save(obj),HttpStatus.OK);
    }
    
}

