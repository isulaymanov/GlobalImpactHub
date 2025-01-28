package com.alien.security.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.alien.security.entity.UserModel;
import com.alien.security.repo.UserRepo;

import com.alien.security.repo.*;

@Component
public class Userservice {
    @Autowired
	public UserRepo userrepo ;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRopo refreshTokenRopo;

    public List<UserModel> getAllUser(){
    	List<UserModel> alluser =(List<UserModel>) this.userrepo.findAll();
    	return alluser;
    }
    
    public UserModel findByUserName(String username) {
    	UserModel userget = userrepo.findByUsername(username);
    	return userget;
    }
    
    public UserModel addUser(UserModel userModel) {
        userrepo.save(userModel);
        return userModel;
    }
    
    public UserModel createUser(UserModel userModel) {
    	  if (userrepo.existsByUsername(userModel.getUsername())) {
    	        throw new BadCredentialsException("! @ Username already exists. Please choose a different username.");
    	    }
    	userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
    	System.out.println(userModel);
    	return userrepo.save(userModel);
    }
    
    public void updateUser(int userid,UserModel userModel) {
            userModel.setId(userid);
            userrepo.save(userModel);
    }
    
    public void deleteUser(String username) {
    	    UserModel userModel = userrepo.findByUsername(username);
    		userrepo.delete(userModel);
	}



}

