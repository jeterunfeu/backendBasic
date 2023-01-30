package com.srlab.basic.authserverside.users.controllers;

import com.srlab.basic.authserverside.users.services.UserService;
import com.srlab.basic.serverside.boards.models.RequestDto;
import com.srlab.basic.serverside.utils.Helper;
import com.srlab.basic.serverside.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tokens")
public class TokenController {

    @Autowired
    private Response response;
    @Autowired
    private UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Validated RequestDto.SignUp signUp, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return userService.signUp(signUp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated RequestDto.Login login, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return userService.login(login);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Validated RequestDto.Refresh refresh, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return userService.refresh(refresh);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Validated RequestDto.Logout logout, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return userService.logout(logout);
    }

    @GetMapping("/checkInfo")
    public ResponseEntity<?> checkInfo(@Validated RequestDto.Check check, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return userService.checkInfo(check);
    }

}
