package account.service;

import account.exceptions.CustomExceptions;
import account.model.User;
import account.model.dto.PasswordResetDTO;
import account.model.dto.UserRegistrationDTO;
import account.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
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

    public AuthService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User findUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(CustomExceptions.UserNotFoundException::new);
    }

    @Transactional
    public User registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail()))
            throw new CustomExceptions.UserAlreadyExistsException();
        User user = modelMapper.map(registrationDTO, User.class);
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setAuthorities(List.of("ROLE_USER"));
        return userRepository.save(user);
    }

    @Transactional
    public User updatePassword(PasswordResetDTO resetDTO, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(CustomExceptions.UserNotFoundException::new);
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
