package org.job.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.job.app.exception.ResourceNotFoundException;
import org.job.app.model.Category;
import org.job.app.model.Jobs;
import org.job.app.model.Recruiter;
import org.job.app.repository.CategoryRepository;
import org.job.app.repository.JobsRepository;
import org.job.app.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


@RestController
@RequestMapping("/api/job/")
@OpenAPIDefinition(info = @Info(title = "Job API", version = "v1"))
@CrossOrigin(origins = "http://localhost:4200/")
public class JobController {
	
	
	@Autowired
	private JobsRepository jobRepo; 
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private RecruiterRepository recruiterRepository;
	
	
	@PostMapping("/addjob")
	public ResponseEntity<Jobs> postJob(
			@RequestParam("title")String title,
			@RequestParam("description")String description,
			@RequestParam("salary")String salary,
			@RequestParam("experience")String experience,
			@RequestParam("location")String location,
			@RequestParam("jobType")String jobType,
			@RequestParam("numberOfVacancy")String numberOfVacancy,
			@RequestParam("lastdate")String lastDate,
			//@RequestParam("image") MultipartFile image,
			
			@RequestParam("categoryId")String categoryId,
			@RequestParam("recruiterId")String recruiterId) throws IOException
	{
		
		Jobs job=new Jobs();
		job.setActive(true);
		job.setTitle(title);
		job.setDescription(description);
		job.setExperience(Integer.valueOf(experience));
		job.setLocation(location);
		job.setNumberOfVacancy(Integer.valueOf(numberOfVacancy));
		job.setJobType(jobType);
		job.setSalary(Integer.valueOf(salary));
		//job.setLastDate(new Date(lastDate))
		//job.setImage(image.getBytes());
		job.setCategory(this.categoryRepository.findById(new Long(categoryId)).get());
		job.setRecruiter(this.recruiterRepository.findById(new Long(recruiterId)).get());
		Jobs savedJob=this.jobRepo.save(job);
		
		return new ResponseEntity<Jobs>(savedJob,HttpStatus.CREATED);
	}
	
	
	 private Sort.Direction getSortDirection(String direction) {
		    if (direction.equals("asc")) {
		      return Sort.Direction.ASC;
		    } else if (direction.equals("desc")) {
		      return Sort.Direction.DESC;
		    }

		    return Sort.Direction.ASC;
		  }
	  @GetMapping("/jobs/pagination")
	  public ResponseEntity<Map<String, Object>> getAllTutorialsPage(
	      @RequestParam(required = false) String title,
	      @RequestParam(defaultValue = "0") int page,
	      @RequestParam(defaultValue = "3") int size,
	      @RequestParam(defaultValue = "id,desc") String[] sort) {

	    try {
	      List<Order> orders = new ArrayList<Order>();

	      if (sort[0].contains(",")) {
	        // will sort more than 2 fields
	        // sortOrder="field, direction"
	        for (String sortOrder : sort) {
	          String[] _sort = sortOrder.split(",");
	          orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
	        }
	      } else {
	        // sort=[field, direction]
	        orders.add(new Order(getSortDirection(sort[1]), sort[0]));
	      }

	      List<Jobs> jobs = new ArrayList<Jobs>();
	      Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

	      Page<Jobs> pageJobs;
	     
	    	pageJobs = jobRepo.findAll(pagingSort);
	    
	      jobs = pageJobs.getContent();

	      Map<String, Object> response = new HashMap<>();
	      response.put("jobs", jobs);
	      response.put("currentPage", pageJobs.getNumber());
	      response.put("totalItems", pageJobs.getTotalElements());
	      response.put("totalPages", pageJobs.getTotalPages());

	      return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	  }
	
	@GetMapping("/jobs")
	public ResponseEntity<List<Jobs>> getAllJobs()
	{
		return new ResponseEntity<List<Jobs>>(this.jobRepo.findAll(), HttpStatus.OK);
	}
	
	@GetMapping("/{jobId}")
	public ResponseEntity<Jobs> getJob(@PathVariable long jobId)
	{
		Jobs job=this.jobRepo.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
		return new ResponseEntity<Jobs>(job, HttpStatus.OK);
	}
	
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<Jobs>> getJobsByCategory(@PathVariable long categoryId)
	{
		Category category=this.categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
		
		return new ResponseEntity<List<Jobs>>(this.jobRepo.findByCategory(category), HttpStatus.OK);
	
	}
	
	@GetMapping("/recruiter/{recruiterId}")
	public ResponseEntity<List<Jobs>> getJobsByRecruiter(@PathVariable Long recruiterId)
	{
		Recruiter recruiter=this.recruiterRepository.findById(recruiterId).orElseThrow(() -> new ResourceNotFoundException("Recruiter", "id", recruiterId));;;
		
		return new ResponseEntity<List<Jobs>>(this.jobRepo.findByRecruiter(recruiter), HttpStatus.OK);
	
	}
	
	@PutMapping("/status")
	public ResponseEntity<?> jobStatusUpdate(@RequestParam("id")String id, @RequestParam("status")String status)
	{
		Optional<Jobs> job=this.jobRepo.findById(Long.parseLong(id));
		
		if(job.isPresent())
		{
			if(status.equalsIgnoreCase("Active"))
			{
				job.get().setActive(true);
			}
			else
			{
				job.get().setActive(false);
			}
			return new ResponseEntity<Jobs>(this.jobRepo.save(job.get()),HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<String>("job not found!!",HttpStatus.OK);
		}
	}

}
