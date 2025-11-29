package be.ehb.auctionhousebackend.service;


import be.ehb.auctionhousebackend.dto.AuthResponse;
import be.ehb.auctionhousebackend.dto.LoginDto;
import be.ehb.auctionhousebackend.dto.RegisterDto;
import be.ehb.auctionhousebackend.exception.ResourceException;
import be.ehb.auctionhousebackend.model.Person;
import be.ehb.auctionhousebackend.model.Role;
import be.ehb.auctionhousebackend.repository.PersonRepository;
import be.ehb.auctionhousebackend.repository.RoleRepository;
import be.ehb.auctionhousebackend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(PersonRepository personRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterDto registerDto) {
        if (personRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new ResourceException("Person with this email already exists");
        }

        Person user = new Person(
                registerDto.getAuctionPersonNumber(),
                registerDto.getName(),
                registerDto.getEmail(),
                passwordEncoder.encode(registerDto.getPassword())


        );
        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new ResourceException("Error: Role is not found."));
        user.setRole(userRole);
        personRepository.save(user);
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName().name(),
                user.getName()
        );
        return new AuthResponse(token);
    }

    public String login(LoginDto loginDto) {
        Person user = personRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResourceException("User not found"));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new ResourceException("Invalid password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName().name(),
                user.getName()
        );
        return token;
    }


}
