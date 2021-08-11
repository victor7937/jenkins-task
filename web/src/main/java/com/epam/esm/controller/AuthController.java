package com.epam.esm.controller;

import com.epam.esm.dto.UserDTO;
import com.epam.esm.security.AuthDTO;
import com.epam.esm.security.AuthResponse;
import com.epam.esm.security.provider.AuthenticationAndTokenProvider;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserService userService;
    private final AuthenticationAndTokenProvider authAndTokenProvider;

    @Autowired
    public AuthController(UserService userService, AuthenticationAndTokenProvider authAndTokenProvider) {
        this.userService = userService;
        this.authAndTokenProvider = authAndTokenProvider;
    }

    /**
     * Endpoint for users authentication
     * @param authDTO - contains email and password
     * @return response with current users email and JWT token
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthDTO authDTO){
        return new AuthResponse(authDTO.getEmail(), authAndTokenProvider.authenticate(authDTO));
    }

   @GetMapping("/hello")
   public String sayHello(){
       return "Hello";
   }

    /**
     * Endpoint for new users registration
     * @param userDTO - dto for adding a new user that contains email, password and optional params like
     * firstName and lastName
     * @return response with current users email and JWT token
     */
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody UserDTO userDTO){
        userService.registration(userDTO);
        return ResponseEntity.ok().build();
    }

}
