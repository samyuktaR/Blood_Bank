package com.example.bloodbank;

import java.util.List;
import java.util.Optional;

import com.example.bloodbank.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping(path="/requests")
public class RequestController {
	
	@Autowired
	private DonorRepository donorRepository;
	public DonorRepository DonorController (DonorRepository donorRepository) {
		this.donorRepository = donorRepository;
		return donorRepository;
	}
	
	private RecipientRepository recipientRepository;
	public RecipientRepository RecipientController(RecipientRepository recipientRepository) {
		this.recipientRepository = recipientRepository;
		return recipientRepository;
	}
	
	private RequestRepository requestRepository;
	public RequestController(RequestRepository requestRepository) {
		this.requestRepository = requestRepository;
	}
	
	/*public RecipientRepository RecipientController(RecipientRepository recipientRepository) {
		this.recipientRepository = recipientRepository;
		return recipientRepository;
	}*/
	
	@PostMapping//(path="/add")
	public @ResponseBody String addNewRequest(@RequestParam String bg, @RequestParam String quantity, @RequestParam String location, 
			@RequestParam String status, @RequestParam String recipientid, Recipient recipient, Donor id) {
		Request req = new Request();
		req.setBg(bg);
		req.setQuantity(quantity);
		req.setLocation(location);
		req.setStatus(status);
		req.setRecipientId(recipientid);
		req.setRecipient(recipient);
		req.setDonor(id);
		requestRepository.save(req);
		//recipientRepository.save(recipient);
		return "Request Saved";
	}
	
	/*@PostMapping
	public @ResponseBody List<Recipient> getRecipient(@RequestParam ("r_id") String r_id) {
		return recipientRepository.findById(r_id);
	}*/
	
	@GetMapping//(path="/getrequests")
	public @ResponseBody Iterable<Request> getAllRequests() {
		return requestRepository.findAll();
	}
	
	/*
	@GetMapping
	public @ResponseBody Optional<Recipient> getRecipient(@RequestParam Integer r_id) {
		return recipientRepository.findById(r_id);
	}
	*/
	
	@GetMapping(path="/{id}")
	public @ResponseBody Optional<Request> getRequest(@PathVariable ("id") Integer id) {
		return requestRepository.findById(id);
	}
	
	@GetMapping(path="/{id}/accept")
	public ResponseEntity<Donor> getDonor(@PathVariable("id") Integer id) {
		
		System.out.println("request not found: "+ id);
		
		/*Donor donor = new Donor();
		Integer donorid = donor.getId();		
		if(donorid == null) {
			return ResponseEntity.notFound().build();	
		}*/
		Optional<Donor> optionalDonor = donorRepository.findById(id);
		
		if (optionalDonor.isPresent()) {
            Donor newdonor = optionalDonor.get();
            return ResponseEntity.ok(newdonor);
        } else {
            return ResponseEntity.notFound().build();
        }	
	}
	/*
	public ResponseEntity<Recipient> getRecipient(@PathVariable("id")Integer id) {
		Optional<Recipient> optionalRecipient = recipientRepository.findById(id);
		if(optionalRecipient.isPresent()) {
			Recipient newRecipient = optionalRecipient.get();
			return ResponseEntity.ok(newRecipient);
		} else {
			return ResponseEntity.notFound().build();
		}
	}*/
	
	
	@PutMapping(path="/{id}")
	public ResponseEntity<Request>updateRequestStatus(@PathVariable Integer id) {
		Optional<Request> requestOptional = requestRepository.findById(id);
		if(requestOptional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Request request = requestOptional.get();
		request.setStatus("accepted");
		requestRepository.save(request);
		return ResponseEntity.ok(request);
	}
	
	@DeleteMapping//(path="/allRequests")
	public void deleteRequests() {
		requestRepository.deleteAll();
	}
	
	@DeleteMapping(path="/{id}")
	public void DeleteRequest(@PathVariable ("id") Integer id) {
		requestRepository.deleteById(id);
	}
}
