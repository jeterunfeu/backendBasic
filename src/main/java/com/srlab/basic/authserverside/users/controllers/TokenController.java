package com.srlab.basic.authserverside.users.controllers;

import com.srlab.basic.authserverside.users.dtos.*;
import com.srlab.basic.authserverside.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

//@Tag(name = "token")
@RestController
@RequestMapping("/api/tokens")
public class TokenController {

    private final Logger LOG = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    private UserService userService;

    @Operation(description = "sign-up", responses = { @ApiResponse(responseCode = "200", description = "insert member"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping(path="/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUp(HttpServletRequest req, @RequestBody SignUpDto signUp) {

        return userService.signUp(signUp);
    }

    @Operation(description = "login", responses = { @ApiResponse(responseCode = "200", description = "login success"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping(path="/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(HttpServletRequest req, @RequestBody LoginDto login) {

        return userService.login(req, login);
    }

    @Operation(description = "token refresh", responses = { @ApiResponse(responseCode = "200", description = "token refreshed"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping(path="/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refresh(HttpServletRequest req, @RequestBody RefreshDto refresh) {

        return userService.refresh(req, refresh);
    }

    @Operation(description = "logout", responses = { @ApiResponse(responseCode = "200", description = "logout success"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping(path="/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(HttpServletRequest req, @RequestBody LogoutDto logout) {

        return userService.logout(req, logout);
    }

    @Operation(description = "get user info", responses = { @ApiResponse(responseCode = "200", description = "get user info"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping("/checkInfo")
    public ResponseEntity<?> checkInfo(HttpServletRequest req, @RequestBody CheckDto check) {

        return userService.checkInfo(check);
    }

}
