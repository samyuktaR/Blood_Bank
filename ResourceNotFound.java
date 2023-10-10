package com.example.bloodbank;

class ResourceNotFoundException extends RuntimeException {

	ResourceNotFoundException(Integer id) {
		super("User not found: " + id);
	}
}
