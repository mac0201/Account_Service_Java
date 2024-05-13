package account.service;

import account.model.User;
import account.model.dto.PasswordResetDTO;
import account.model.dto.UserDTO;
import account.model.dto.UserRegistrationDTO;
import account.model.security.events.SecurityEventBroadcaster;
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

    private final SecurityEventBroadcaster eventBroadcaster;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);


    private User getUser(String userEmail) {
        return userRepository.findByEmailIgnoreCase(userEmail).orElseThrow(() -> {
            LOGGER.info("Error retrieving current user from database, email: {}", userEmail);
            return new UserNotFoundException(); });
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
            LOGGER.warn("ADMIN ROLE ASSIGNED TO USER: {}", user.getEmail());
            //! broadcast GRANT_ROLE event?
        }
        else user.setRoles(Set.of(USER));

        LOGGER.info("User registered - {}", user.getEmail());
        eventBroadcaster.broadcastSecurityEvent(CREATE_USER, null, user.getEmail(), "path...");
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
        eventBroadcaster.broadcastSecurityEvent(CHANGE_PASSWORD, null, userEmail, "path...");
        return new PasswordResetDTO(userEmail, "The password has been updated successfully");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUser(email);
        return modelMapper.map(user, UserDetails.class);
    }
}
