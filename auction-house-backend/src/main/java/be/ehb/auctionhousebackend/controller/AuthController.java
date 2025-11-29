package be.ehb.auctionhousebackend.controller;


import be.ehb.auctionhousebackend.dto.AuthResponse;
import be.ehb.auctionhousebackend.dto.LoginDto;
import be.ehb.auctionhousebackend.dto.RegisterDto;
import be.ehb.auctionhousebackend.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "User authentication and registration")
@Validated
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterDto registerDto) {

        logger.info("Security Event: Registration attempt for email: {}", registerDto.getEmail());

        AuthResponse authResponse = authService.register(registerDto);

        logger.info("Security Event: User registered successfully: {}", registerDto.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDto loginDto) {
        // SECURITY: Audit log for login attempt
        logger.info("Security Event: Login attempt for email: {}", loginDto.getEmail());

        String jwt = authService.login(loginDto);

        // SECURITY: Audit log for success
        logger.info("Security Event: User logged in successfully: {}", loginDto.getEmail());

        return ResponseEntity.ok(jwt);
    }

}
