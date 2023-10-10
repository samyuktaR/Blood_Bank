package com.example.bloodbank;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.bloodbank.Donor;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface DonorRepository extends CrudRepository<Donor, Integer> {
	List<Donor> findByBg(String bg);
	
	//List<Donor> findByAddress(@Param("longitude") double longitude,
      //      @Param("latitude") double latitude,
        //    @Param("distance") double distance);
}
