package account.service;

import account.exceptions.CustomExceptions;
import account.model.User;
import account.model.dto.PasswordResetDTO;
import account.model.dto.UserRegistrationDTO;
import account.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    public User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(CustomExceptions.UserNotFoundException::new);
    }

    @Transactional
    public User registerUser(UserRegistrationDTO registrationDTO) {
        // Validate password
        passwordValidator.validatePassword(registrationDTO.getPassword());
        if (userRepository.existsByEmailIgnoreCase(registrationDTO.getEmail())) {
            LOGGER.debug("User with email {} already exists", registrationDTO.getEmail());
            throw new CustomExceptions.UserAlreadyExistsException();
        }

        User user = modelMapper.map(registrationDTO, User.class);
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setAuthorities(List.of("ROLE_USER"));

        LOGGER.info("User registered - {}", user.getEmail());
        return userRepository.save(user);
    }

    @Transactional
    public PasswordResetDTO updatePassword(PasswordResetDTO resetDTO, String userEmail) {
        // Validate password
        passwordValidator.validatePassword(resetDTO.getNewPassword());

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> {
            LOGGER.info("Error retrieving current user from database, email: {}", userEmail);
            return new CustomExceptions.UserNotFoundException(); });

        if (passwordEncoder.matches(resetDTO.getNewPassword(), user.getPassword()))
            throw new CustomExceptions.PasswordValidationException("The passwords must be different!");

        user.setPassword(passwordEncoder.encode(resetDTO.getNewPassword()));
        userRepository.save(user);

        return new PasswordResetDTO(userEmail, "The password has been updated successfully");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findUser(email);
        return modelMapper.map(user, UserDetails.class);
    }
}
