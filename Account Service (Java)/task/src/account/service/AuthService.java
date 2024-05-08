package account.service;

import account.exceptions.CustomExceptions;
import account.model.User;
import account.model.dto.PasswordResetDTO;
import account.model.dto.UserRegistrationDTO;
import account.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(CustomExceptions.UserNotFoundException::new);
    }

    @Transactional
    public User registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByEmailIgnoreCase(registrationDTO.getEmail())) {
            LOGGER.info("User with email {} already exists", registrationDTO.getEmail());
            throw new CustomExceptions.UserAlreadyExistsException();
        }
        User user = modelMapper.map(registrationDTO, User.class);
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setAuthorities(List.of("ROLE_USER"));
        LOGGER.info("User registered - {}", user.getEmail());
        return userRepository.save(user);
    }

    @Transactional
    public User updatePassword(PasswordResetDTO resetDTO, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> {
            LOGGER.info("User with email {} not found", userEmail);
            return new CustomExceptions.UserNotFoundException(); });
        user.setPassword(passwordEncoder.encode(resetDTO.getNewPassword()));
        return userRepository.save(user);
    }

    public void testUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Logged in: " + auth.getName());
        System.out.println("Roles: " + auth.getAuthorities());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findUser(email);
        UserDetails userDetails = modelMapper.map(user, UserDetails.class);
        System.out.println(userDetails);
        return userDetails;
    }
}
