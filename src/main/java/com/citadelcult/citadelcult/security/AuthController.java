package com.citadelcult.citadelcult.security;

import com.citadelcult.citadelcult.user.UserService;
import com.citadelcult.citadelcult.user.dtos.JwtAuthenticationResponse;
import com.citadelcult.citadelcult.user.dtos.SignInRequest;
import com.citadelcult.citadelcult.user.dtos.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return userService.signUp(request);
    }

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return userService.signIn(request);
    }
}
