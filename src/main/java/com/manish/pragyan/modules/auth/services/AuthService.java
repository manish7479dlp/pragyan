package com.manish.pragyan.modules.auth.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.manish.pragyan.common.exception.ConflictException;
import com.manish.pragyan.modules.auth.dtos.LoginRequestDto;
import com.manish.pragyan.modules.auth.dtos.LoginResponseDto;
import com.manish.pragyan.modules.auth.dtos.RegisterRequestDto;
import com.manish.pragyan.modules.auth.dtos.RegisterResponseDto;
import com.manish.pragyan.modules.auth.entities.CustomUserDetails;
import com.manish.pragyan.modules.auth.entities.Provider;
import com.manish.pragyan.modules.auth.entities.Role;
import com.manish.pragyan.modules.auth.entities.UserEntity;
import com.manish.pragyan.modules.auth.mappers.AuthMapper;
import com.manish.pragyan.modules.auth.repositories.AuthRepository;

@Service 
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtService jwtService;
    private  final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    public AuthService(
        AuthRepository authRepository,
        JwtService jwtService,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder,
        AuthMapper authMapper
    ) {
        this.authRepository = authRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.authMapper = authMapper;
    }
    
    private RegisterResponseDto register(String name, String email, String password, Provider provider, String avatar) {
    	UserEntity existingUser = authRepository.findByEmail(email);
    	UserEntity savedUser;
    	
    	if(existingUser == null) {
    		UserEntity userEntity = new UserEntity();
        	userEntity.setName(name);
        	userEntity.setEmail(email);
        	userEntity.setProvider(provider);
        	userEntity.setRole(Role.VIEWER);
        	userEntity.setAvatar(avatar);
        	
        	if(password != null) {
        		userEntity.setPassword(passwordEncoder.encode(password));
        	}
    		savedUser = authRepository.save(userEntity);
    		return authMapper.entityToRegisterResponseDto(savedUser);
    	} 
    	
		if(!existingUser.getProvider().equals(provider)) {
			throw new ConflictException("User already register with other provider.");
		}
		
		if(provider.equals(Provider.LOCAL)) {
			throw new ConflictException("User already register.");
		}
		
		return authMapper.entityToRegisterResponseDto(existingUser);      
    }

    public RegisterResponseDto localRegister(RegisterRequestDto registerRequestDto) {
    	String avatar = null;
        return register(registerRequestDto.name(), registerRequestDto.email(), registerRequestDto.password(), Provider.LOCAL, avatar);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password())
        );

        if(!authentication.isAuthenticated()) {
            throw new RuntimeException("Invalid Credentials");
        }

        var authUser = (CustomUserDetails) authentication.getPrincipal();
        // Token Generation
        String token = jwtService.generateToken(authUser.getEmail(), authUser);
        return authMapper.customUserDetailsToRegisterResponseDto(authUser, token);

    }
    
    public RegisterResponseDto oAuth2Login(OAuth2User oAuth2User, String registrationId) {
    	Provider provider = getProvider(registrationId);
    	String email = getEmail(oAuth2User);
    	String name = getName(oAuth2User);
    	String avatar = getAvatar(oAuth2User, provider);
    	String password = null;
    	System.out.println(provider);
    	return register(name, email, password, provider, avatar);
    }
    
    private String getAvatar(OAuth2User oAuth2User, Provider provider) {
        return switch (provider) {
            case GOOGLE -> oAuth2User.getAttribute("picture");
            case GITHUB -> oAuth2User.getAttribute("avatar_url");
            default -> throw new IllegalArgumentException("Invalid Provider: " + provider.name());
        };
    }
    
 // Pass the registrationId from OAuth2AuthenticationToken (e.g., token.getAuthorizedClientRegistrationId())
    private Provider getProvider(String registrationId) {
        if (registrationId == null) {
            throw new IllegalArgumentException("Provider registration ID cannot be null.");
        }
        
        return switch (registrationId.toLowerCase()) {
            case "github" -> Provider.GITHUB;
            case "google" -> Provider.GOOGLE;
            default -> throw new IllegalArgumentException("Invalid Provider: " + registrationId);
        };
    }
    
    private String getEmail(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            String login = oAuth2User.getAttribute("login"); 
            if (login != null) {
                email = login + "@github.noemail"; 
            } else {
                email = oAuth2User.getAttribute("sub"); 
            }
        }
        
        return email;
    }
    
    private String getName(OAuth2User oAuth2User) {
    	return oAuth2User.getAttribute("name");
    }
}
