package com.manish.pragyan.modules.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.manish.pragyan.modules.auth.entities.UserEntity;

@Repository 
public interface AuthRepository extends  JpaRepository<UserEntity, String>{
    UserEntity findByEmail(String email);
}
