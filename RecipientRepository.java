package com.example.bloodbank;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.example.bloodbank.Recipient;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface RecipientRepository extends CrudRepository<Recipient, Integer> {
	List<Recipient> findByStatus(String status);
	//Optional <Recipient> findById(String r_id);
}
