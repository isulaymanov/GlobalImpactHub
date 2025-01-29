package com.alien.security.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.alien.security.entity.UserModel;
import org.springframework.stereotype.Repository;

@Repository

public interface UserRepo extends CrudRepository<UserModel,Integer>{

	//Optional<UserModel> findByUsernameS(String username);

	boolean existsByUsername(String username);

	//public List<UserModel> findByName(String name);

	public UserModel findByUsername(String username);

	@Query("select u from UserModel u")
	public List<UserModel> getAlluser();

	@Query(value = "select * from UserModel", nativeQuery = true)
	public List<UserModel> getAllUserNative();

}
