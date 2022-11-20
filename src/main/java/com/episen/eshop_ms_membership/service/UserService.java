package com.episen.eshop_ms_membership.service;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.episen.eshop_ms_membership.model.JwtResponse;
import com.episen.eshop_ms_membership.model.User;
import com.episen.eshop_ms_membership.repository.UserRepository;
import com.episen.eshop_ms_membership.security.JwTokenGenerator;



@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private JwTokenGenerator jw_Token_Generator;
	
	private User admin,current_user;
	
	@PostConstruct
	public void init() {
		List<String> admin_privileges=new ArrayList<>();
		admin_privileges.add("admin");
		admin =new User();
		admin.setLogin("HajarAdmin");
		admin.setPassword("adminpwd");
		admin.setPrivileges(admin_privileges);
		repository.add(admin);
	}
	@Override
	public User loadUserByUsername(String login) throws UsernameNotFoundException {
		User user = repository.getUserByLogin(login);
		if (user!=null) {
			return user;
		} else {
			throw new UsernameNotFoundException("User not found with login: " + login);
		}
	}
	
	public Boolean is_admin(String token)
	{		
		List<String> priv=(List<String>) jw_Token_Generator.getAllClaimsFromToken(token).get("Privileges");
		if(priv.contains("admin"))
		{return true;}
		else
		{ return false;}
	}
	public String authenticate_user(User user) {
		
		current_user = loadUserByUsername(user.getLogin());
		if(current_user==null)
			return "this user doesn't exist";
		
		return jw_Token_Generator.generateToken(current_user.getLogin(), current_user.getPrivileges());
		
	}
	public void create_user(User user) {
		
		if(StringUtils.isEmpty(user.getLogin()) || StringUtils.isEmpty(user.getPassword())) {
			throw new RuntimeException("You came across a user exception");
		}
		
		if(repository.getUserByLogin(user.getLogin()) != null) {
			throw new RuntimeException("This user already exists");
		}
		repository.add(user);
	}
	
	
	public List<User> getAll(){
		return repository.getAll();
	}
	
	public void update_user(User userToUpdate) {
		
		if(null == repository.getUserByLogin(userToUpdate.getLogin())) {
			throw new RuntimeException("This user is not found");
		}
		
		repository.update(userToUpdate);
	}
	
	public void delete_user(String Login) {
		
		if(null == repository.getUserByLogin(Login)) {
			throw new RuntimeException("This user is not found");
		}
		
		repository.delete(Login);
	}

	
	
}
