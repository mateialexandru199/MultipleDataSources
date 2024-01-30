package com.example.multipledatasource.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.multipledatasource.model.User;
import com.example.multipledatasource.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public List<User> getAlUsers() {
		return userRepository.findAll();
	}

}
