package account.service;

import account.model.User;
import account.model.dto.PasswordResetDTO;
import account.model.dto.UserDTO;
import account.model.dto.UserRegistrationDTO;
import account.model.security.events.SecurityEventLogger;
import account.repository.UserRepository;
import account.service.security.PasswordValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static account.model.roles.UserRole.*;
import static account.model.security.events.SecurityEventType.CREATE_USER;
import static account.model.security.events.SecurityEventType.CHANGE_PASSWORD;

import account.exceptions.definitions.UserAuthExceptions.*;

import java.util.Set;

@Service
@AllArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final SecurityEventLogger eventLogger;

    private final Logger appLogger;

    private User getUser(String userEmail) {
        return userRepository.findByEmailIgnoreCase(userEmail).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        // Validate password
        passwordValidator.validatePassword(registrationDTO.getPassword());
        if (userRepository.existsByEmailIgnoreCase(registrationDTO.getEmail())) throw new UserAlreadyExistsException();

        User user = modelMapper.map(registrationDTO, User.class);
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        if (userRepository.count() == 0) { // assign admin role to first user
            user.setRoles(Set.of(ADMINISTRATOR));
        }
        else user.setRoles(Set.of(USER));

        appLogger.debug("User registered - {}", user.getEmail());
        eventLogger.handleSecurityEvent(CREATE_USER, null, user.getEmail());

        return modelMapper.map(
                userRepository.save(user), UserDTO.class);
    }

    @Transactional
    public PasswordResetDTO updatePassword(PasswordResetDTO resetDTO, String userEmail) {
        // Validate password
        passwordValidator.validatePassword(resetDTO.getNewPassword());

        User user = getUser(userEmail);

        if (passwordEncoder.matches(resetDTO.getNewPassword(), user.getPassword()))
            throw new PasswordValidationException("The passwords must be different!");

        user.setPassword(passwordEncoder.encode(resetDTO.getNewPassword()));
        userRepository.save(user);
        appLogger.debug("Updated password for user {}", userEmail);
        eventLogger.handleSecurityEvent(CHANGE_PASSWORD, user.getEmail(), user.getEmail());
        return new PasswordResetDTO(userEmail, "The password has been updated successfully");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUser(email);
        return modelMapper.map(user, UserDetails.class);
    }
}
