package com.manish.pragyan.modules.auth.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.manish.pragyan.modules.auth.entities.CustomUserDetails;
import com.manish.pragyan.modules.auth.entities.UserEntity;
import com.manish.pragyan.modules.auth.repositories.AuthRepository;

@Service 
public class CustomUserDetailsService implements UserDetailsService{

    private final AuthRepository authRepository;

    public CustomUserDetailsService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = authRepository.findByEmail(username);
        return new CustomUserDetails(userEntity);
    }
}
