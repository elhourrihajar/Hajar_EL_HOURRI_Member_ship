package com.episen.eshop_ms_membership.resource;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.episen.eshop_ms_membership.model.User;
import com.episen.eshop_ms_membership.security.JwTokenGenerator;
import com.episen.eshop_ms_membership.model.JwtResponse;
import com.episen.eshop_ms_membership.service.UserService;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping(produces = {"application/json"})
public class UserResource {

	@Autowired
	private UserService userService;

	@Autowired
	private JwTokenGenerator jwtToken_gen;

	private JwtResponse jwt;
	private User current_user;
	
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public JwtResponse createAuthenticationToken(@RequestBody User user)
			throws Exception {

		check_user_existance(user);

		current_user= userService.loadUserByUsername(user.getLogin());

		String token = jwtToken_gen.generateToken(current_user);
		jwt=new JwtResponse(token);
		return jwt;
	}
	@RequestMapping(value = "/userInfo", method = RequestMethod.GET)
	public ResponseEntity<String>get_user_info(@RequestBody String login)
			throws Exception {

		List<String> priv=(List<String>) jwtToken_gen.getAllClaimsFromToken(jwt.getToken()).get("Privileges");
		String response="You are"+jwtToken_gen.getLoginFromToken(login)+" \n you are a/an :";
		for(int i=0;i<priv.size();i++)
		{
			response+=priv.get(i)+"\n";
		}
		
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	private void check_user_existance(User user) throws Exception {
		Objects.requireNonNull(user);
        if(userService.authenticate_user(user)=="this user doesn't exist")
        {
        	throw new Exception( "this user doesn't exist");
        }
	}
	

	//Methods to execute an operation over users (create,update,delete)	
	
	@PostMapping("create")
	public ResponseEntity<User> create_user(@RequestBody User user) throws Exception {
		if(!userService.is_admin(jwt.getToken()))
		{
			throw new RuntimeException("Operation not allowed! Only admin can create users");
		}
		
		userService.create_user(user);
		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	}
	
	@PutMapping("update")
	public void update_user(@RequestBody User user) {
		if(!userService.is_admin(jwt.getToken()))
		{
			throw new RuntimeException("Operation not allowed! Only admin can update users");
		}
		userService.update_user(user);
	}
	
	
	@DeleteMapping("delete/{Login}")
	public void delete_user(@PathVariable("Login") String Login) {
		if(!userService.is_admin(jwt.getToken()))
		{
			throw new RuntimeException("Operation not allowed! Only admin can delete users");
		}
		userService.delete_user(Login);
	}
	
	
	//Methods to get an information about users (list of total users, a certain user)
	
	@GetMapping("list")
	public ResponseEntity<List<User>> getAll(){
		
		return new ResponseEntity<List<User>>(userService.getAll(), HttpStatus.OK);
		
	}
	
	@GetMapping("get/{Login}")
	public ResponseEntity<User> get_user(@PathVariable("Login") String Login) {
		
		return new ResponseEntity<User>(userService.loadUserByUsername(Login), HttpStatus.OK);
	}
	
}
