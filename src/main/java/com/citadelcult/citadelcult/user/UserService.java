package com.citadelcult.citadelcult.user;

import com.citadelcult.citadelcult.security.JwtService;
import com.citadelcult.citadelcult.user.dtos.JwtAuthenticationResponse;
import com.citadelcult.citadelcult.user.dtos.SignInRequest;
import com.citadelcult.citadelcult.user.dtos.SignUpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public User getUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        return userRepository.findUserByEmail(email);
    }

    public User createUser(User user) {
        String email = user.getEmail();
        User existingUser = getUserByEmail(email);
        if (existingUser != null) {
            String errorMessage = "Customer with email " + email + " already exists";
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPhone(user.getPhone());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());

        return userRepository.save(newUser);
    }

    public JwtAuthenticationResponse signUp(SignUpRequest signUpRequest) {
        if(getUserByEmail(signUpRequest.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.SEE_OTHER, "Email is already taken!");
        }

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(UserRole.ROLE_CUSTOMER);
        user = userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .build();
    }

    public JwtAuthenticationResponse signIn(SignInRequest signInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));

        var user = userRepository.findUserByEmail(signInRequest.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .build();
    }
}
