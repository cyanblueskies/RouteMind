package com.routemind.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import com.routemind.controller.errors.NotFoundError;
import com.routemind.entity.User;
import com.routemind.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService svc;

    public UserController(UserService svc) { this.svc = svc; }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(
            HttpServletResponse response,
            @RequestBody String username
    ) {
        User user =  svc.createIfNotPresent(username);
        Long user_id = user.getUserId();

        Cookie cookie = new Cookie("user_id", user_id.toString());
        cookie.setAttribute("SameSite", "Lax");
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
        return user_id;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long login(
            HttpServletResponse response,
            @RequestBody String username
    ) {
        User user = svc.findByUsername(username);

        Cookie cookie = new Cookie("user_id", user.getUserId().toString());
        cookie.setAttribute("SameSite", "Lax");
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
        return user.getUserId();
    }
}
