package com.alien.security.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.alien.security.dto.UserModelDTO;
import com.alien.security.jwt.CustomUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import com.alien.security.entity.RefreshToken;
import com.alien.security.entity.UserModel;
import com.alien.security.jwt.JWTHepler;
import com.alien.security.model.JwtRequest;
import com.alien.security.model.JwtResponse;
import com.alien.security.model.RefreshTokenRequest;
import com.alien.security.service.RefreshTokenService;
import com.alien.security.service.Userservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api")
public class RestApp {
   
	@Autowired
	private Userservice userservice;
	 
	@Autowired 
	private RefreshTokenService refreshTokenService;

	@Autowired
	private CustomUserDetailService customUserDetailService;
	
	
	@GetMapping("/user")
	public ResponseEntity<List<UserModel>> getBook() {
		List<UserModel> res = userservice.getAllUser();
		if(res.size() <= 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return  ResponseEntity.status(HttpStatus.CREATED).body(res);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/userprivate")
	public ResponseEntity<List<UserModel>> getBookPrivate() {
	    List<UserModel> res = userservice.getAllUser();
	    if (res.size() <= 0) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
	    return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	
	@GetMapping("/userbyusername/{username}")
	public ResponseEntity<UserModel> getUserByusername(@PathVariable String username){
		UserModel userModel = userservice.findByUserName(username);
		System.out.println(userModel);
		return ResponseEntity.status(HttpStatus.OK).body(userModel);
	}


	@GetMapping("/currentuser")
	public UserModelDTO getLoggedUer(Principal principal){
		UserModel user = (UserModel) customUserDetailService.loadUserByUsername(principal.getName());
		return new UserModelDTO(
				user.getId(),
				user.getPhone(),
				user.getEmail(),
				user.getDateOfBirth(),
				user.getPhotoUrl(),
				user.getGender(),
				user.getGreeting(),
				user.getUsername(),
				user.getRole()
		);
	}
	
	
	@PostMapping("/adduser")
	public ResponseEntity<UserModel> addUser(@RequestBody UserModel userModel){
		UserModel u = null;
		try {
			u = this.userservice.addUser(userModel);
		    System.out.println(u);
		    return ResponseEntity.status(HttpStatus.CREATED).body(u);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/createuser")
	public ResponseEntity<Map<UserModel,String>> createUser(@RequestBody UserModel userModel){
		try {
	        UserModel user = userservice.createUser(userModel);
	        Map<UserModel, String> data = new HashMap<>();
	        data.put(user, "User Created");
		    return ResponseEntity.status(HttpStatus.OK).body(data);
		}catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Duplicate UserName : "+e.toString());
        }
	}

	@PostMapping("/uploadPhoto/{id}")
	public ResponseEntity<?> uploadPhoto(@PathVariable Integer id, @RequestParam("file")MultipartFile file) {
		try {
			Optional<UserModel> userModelOptional = userservice.getUserById(id);
			if (userModelOptional.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			UserModel user = userModelOptional.get();
			String photoUrl = userservice.savePhoto(file);
			user.setPhotoUrl(photoUrl);
			userservice.updateUser(id, user);

			return ResponseEntity.ok("Фото загружена: " + photoUrl);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Ошибка загрузки фото");
		}
	}

	@GetMapping("/photo-user/{id}")
	public ResponseEntity<Resource> getPhoto(@PathVariable Integer id) {
		Optional<UserModel> userModelOptional = userservice.getUserById(id);
		if (userModelOptional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		UserModel user = userModelOptional.get();
		String photoUrl = user.getPhotoUrl();

		if (photoUrl == null || photoUrl.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		try {
			Path path = Paths.get("D:/uploads").resolve(photoUrl.substring(photoUrl.lastIndexOf("/") + 1));  // Восстанавливаем путь до файла
			Resource resource = new FileSystemResource(path);

			if (!resource.exists()) {
				return ResponseEntity.notFound().build();
			}

			return ResponseEntity.ok()
					.contentType(MediaType.IMAGE_PNG)
					.body(resource);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(null);
		}
	}


	@ExceptionHandler(DataIntegrityViolationException.class)
    public String exceptionHandlerDupicateUsername(String str) {
        return  str;
    }
	
	
	@PatchMapping("/updateuser")
	public ResponseEntity<String> updateUser(@RequestBody UserModel userModel){
		try {
			int userid = userModel.getId();
			userservice.updateUser(userid,userModel);
		    return ResponseEntity.status(HttpStatus.CREATED).body("Updated Successfully");
		}catch (Exception e) {
			// TODO: handle exception
			String error = "Error "+e;
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}
	
	@DeleteMapping("/delete/{username}")
	public ResponseEntity<String> deleteUser(@PathVariable String username){
		try {
		userservice.deleteUser(username);
		return ResponseEntity.status(HttpStatus.OK).build();
		}catch (Exception e) {
			System.out.println(e);
			String error = username+" : Not Found or some other error or "+e;
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}
	

	  @Autowired
	    private UserDetailsService userDetailsService;

	    @Autowired
	    private AuthenticationManager manager;


	    @Autowired
	    private JWTHepler helper;

	    private Logger logger = LoggerFactory.getLogger(RestApp.class);


	    @PostMapping("/login")
	    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
	    	 System.out.println("IN CONTROLLER :- "+request.getUsername().toString() + " : "+ request.getPassword());
	        this.doAuthenticate(request.getUsername(), request.getPassword());

	        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
	        logger.atInfo().log("Loaded User ",userDetails);
	        String token = this.helper.generateToken(userDetails);
            RefreshToken refreshToken =  this.refreshTokenService.createRefreshToken(userDetails.getUsername());
	        JwtResponse response = JwtResponse.builder()
	                .jwtToken(token)
	                .refreshToken(refreshToken.getRefreshToken())
	                .username(userDetails.getUsername()).build();
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	    
	    
	    @PostMapping("/logout")
	    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null) {
	            new SecurityContextLogoutHandler().logout(request, response, authentication);
	        }

	        return ResponseEntity.ok("Logout successful");
	    }

	    @PostMapping("/refreshToken")
	    public JwtResponse refreshJwtToken(@RequestBody RefreshTokenRequest request){
	    	RefreshToken refreshTokenOBJ = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
	    	UserModel userModel =  refreshTokenOBJ.getUser();
	    	String token = this.helper.generateToken(userModel);
	    	return JwtResponse.builder()
	    			.username(userModel.getUsername())
	    			.jwtToken(token)
	    			.refreshToken(refreshTokenOBJ.getRefreshToken())
	    			.build();
	    }
	    
	    @ExceptionHandler(BadCredentialsException.class)
	    public String exceptionHandler(String msg) {
	        return msg;
	    }
	    private void doAuthenticate(String username, String password) {

	        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
	        try {
	            manager.authenticate(authentication);


	        } catch (BadCredentialsException e) {
	            throw new BadCredentialsException(" Invalid Username or Password  !!");
	        }

	    }
       
	
}
