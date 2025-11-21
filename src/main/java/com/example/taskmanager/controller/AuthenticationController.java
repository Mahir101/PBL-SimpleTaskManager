package com.example.taskmanager.controller;

import com.example.taskmanager.dto.AuthenticationRequest;
import com.example.taskmanager.dto.AuthenticationResponse;
import com.example.taskmanager.dto.RegisterRequest;
import com.example.taskmanager.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a user")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshToken));
    }
}
