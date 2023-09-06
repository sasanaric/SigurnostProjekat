package com.example.sigurnostprojekat.controllers;

import com.example.sigurnostprojekat.crypto.Crypto;
import com.example.sigurnostprojekat.exceptions.NotFoundException;
import com.example.sigurnostprojekat.models.dto.AuthResponse;
import com.example.sigurnostprojekat.models.entity.UserEntity;
import com.example.sigurnostprojekat.models.request.LoginRequest;
import com.example.sigurnostprojekat.models.request.UserRequest;
import com.example.sigurnostprojekat.security.JwtGenerator;
import com.example.sigurnostprojekat.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtGenerator jwtGenerator;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtGenerator jwtGenerator,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtGenerator = jwtGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequest userRequest) throws Exception {
        if(userService.isUsernameTaken(userRequest.getUsername())){
            return new ResponseEntity<>("Korisnicko ime zauzeto!",HttpStatus.BAD_REQUEST);
        }
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRequest.setRole("USER");
        KeyPair keyPair = Crypto.generateRSAKeyPair();
        String privateKey = Crypto.keyToString(keyPair.getPrivate());
        String publicKey = Crypto.keyToString(keyPair.getPublic());
        userRequest.setPrivateKey(privateKey);
        userRequest.setPublicKey(publicKey);
        userService.insert(userRequest, UserEntity.class);
        return new ResponseEntity<>("Korisnik je registrovan", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            System.out.println(loginRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);
            System.out.println(token);
            AuthResponse authResponse = new AuthResponse(token, loginRequest.getUsername());
            return new ResponseEntity<>(authResponse,HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Podaci nisu odgovarajući!", HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/user-id")
    public ResponseEntity<?> getCurrentUserId() {
        try {
            int id = userService.getCurrentUserId();
            System.out.println("UserLoggedInID ---> "+id);
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            return ResponseEntity.ok(0);
        }
    }



}
