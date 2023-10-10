package com.example.bloodbank;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.example.bloodbank.Request;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface RequestRepository extends CrudRepository<Request, Integer> {
	List<Request> findByStatus(String status);
	
	//Optional<Recipient>findById(String r_id);
	//Optional <Recipient> findById(String r_id);
}
