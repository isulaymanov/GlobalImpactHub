package com.alien.security.repo;

import java.util.Optional;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.alien.security.entity.RefreshToken;



@Repository
public interface RefreshTokenRopo extends CrudRepository<RefreshToken, String> {
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
	void deleteByUserId(Long userId);

}
