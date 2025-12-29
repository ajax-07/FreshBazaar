package com.freshbazaar.identity.service.implementation;

import com.freshbazaar.identity.dto.AuthResponse;
import com.freshbazaar.identity.dto.LoginRequest;
import com.freshbazaar.identity.dto.RegisterRequest;
import com.freshbazaar.identity.entity.UserCredential;
import com.freshbazaar.identity.exception.ValidationException;
import com.freshbazaar.identity.repository.AuthRepository;
import com.freshbazaar.identity.security.JwtTokenProvider;
import com.freshbazaar.identity.service.base.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.freshbazaar.identity.utils.CommonConstants.TOKEN_EXPIRATION_TIME;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register method and Login method: All business logic starts here
     */
    public void register(RegisterRequest request){
        // validate the user existence
        if(repository.findByUserName(request.username()).isPresent()){
            throw new ValidationException("Username already exists!");
        }

        UserCredential user = UserCredential.builder()
                .userName(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();
        repository.save(user);
    }

    public AuthResponse login(LoginRequest request){
        UserCredential user = repository.findByUserName(request.username())
                .orElseThrow(() -> new ValidationException("User doesn't exist!"));

        if(!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new ValidationException("Invalid credentials!");
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        return new AuthResponse(token, TOKEN_EXPIRATION_TIME);
    }
}
