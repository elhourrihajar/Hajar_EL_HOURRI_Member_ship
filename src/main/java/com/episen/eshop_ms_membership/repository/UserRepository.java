package com.episen.eshop_ms_membership.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.episen.eshop_ms_membership.model.User;


@Component
public class UserRepository {

	private Map<String, User> userInMemory = new HashMap<String, User>(); 
	
	
	public void add(User user) {
		System.out.println("adduser -> Login : " + user.getLogin());
		
		userInMemory.put(user.getLogin(), user);
	}
	
	public User getUserByLogin(String Login) {
		System.out.println("Get user by name -> Login : " + Login);
		
		return userInMemory.get(Login);
	}
	
	public List<User> getAll(){
		System.out.println("Get All -> count : " + userInMemory.size());
		
		return new ArrayList<>(userInMemory.values());
	}
	
	public void update(User userToUpdate) {
		
		userInMemory.put(userToUpdate.getLogin(), userToUpdate);
	}
	
	public void delete(String Login) {
		
		userInMemory.remove(Login);
	}
}
