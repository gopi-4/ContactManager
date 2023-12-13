package com.smart.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.smart.repository.UserRepository;
import com.smart.entities.User;

public class CustomUserDetailsService implements UserDetailsService{
	@Autowired
	UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userRepository.getUserByEmail(username).orElse(null);
		if (user==null) return null;
		return new CustomUserDetails(user);
	}

}