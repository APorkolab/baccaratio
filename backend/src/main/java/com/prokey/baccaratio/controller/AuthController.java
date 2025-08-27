package com.prokey.baccaratio.controller;

import com.prokey.baccaratio.controller.dto.AuthRequest;
import com.prokey.baccaratio.controller.dto.AuthResponse;
import com.prokey.baccaratio.controller.dto.UserDto;
import com.prokey.baccaratio.mapper.UserMapper;
import com.prokey.baccaratio.model.User;
import com.prokey.baccaratio.repository.UserRepository;
import com.prokey.baccaratio.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                          JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserRepository userRepository, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody AuthRequest authRequest) {
        userRepository.findByUsername(authRequest.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Username is already taken!");
        });

        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(userMapper.userToUserDto(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
