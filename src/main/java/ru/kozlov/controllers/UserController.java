package ru.kozlov.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kozlov.models.LoginRequest;
import ru.kozlov.models.LoginResponse;
import ru.kozlov.models.UserDto;
import ru.kozlov.services.UserService;

@RestController
@RequestMapping("/user")
@Tag(name = "Users", description = "Контроллер для регистрации")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authManager) {
        this.userService = userService;
        this.authenticationManager = authManager;
    }

    @Operation(summary = "Регистрация")
    @PostMapping("/register")
    public UserDto register(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @Operation(summary = "Логин")
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        return new LoginResponse(loginRequest.username(), role);
    }

    @Operation(summary = "logout")
    @PostMapping("/logout")
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
