package com.routemind.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.routemind.config.JwtUtil;
import com.routemind.dto.AuthRequest;
import com.routemind.dto.AuthResponse;
import com.routemind.entity.User;
import com.routemind.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService svc;
    private final JwtUtil jwtUtil;

    public UserController(UserService svc, JwtUtil jwtUtil) {
        this.svc = svc;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse create(@RequestBody AuthRequest request) {
        User user = svc.createUser(request.username(), request.password());
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());
        return new AuthResponse(user.getUserId(), user.getUsername(), token);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@RequestBody AuthRequest request) {
        User user = svc.authenticate(request.username(), request.password());
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());
        return new AuthResponse(user.getUserId(), user.getUsername(), token);
    }
}
