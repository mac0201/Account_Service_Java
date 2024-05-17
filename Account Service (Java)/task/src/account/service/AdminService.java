package account.service;

import account.exceptions.definitions.AdminExceptions.*;
import account.model.User;
import account.model.dto.UserDTO;
import account.model.dto.UserUpdateDTO;
import account.model.security.events.SecurityEventLogger;
import account.model.security.events.SecurityEventType;
import account.repository.UserRepository;
import account.model.roles.UserRole;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import account.exceptions.definitions.UserAuthExceptions.UserNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

import static account.model.security.events.SecurityEventType.*;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SecurityEventLogger eventLogger;
    private final Logger appLogger;

    public User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(UserNotFoundException::new);
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();
    }

    @Transactional
    public void deleteUser(String email) {
        User user = findUser(email);
        if (user.getRoles().contains(UserRole.ADMINISTRATOR)) throw new DeleteAdminException();
        userRepository.delete(user);
        appLogger.info("User {} DELETED", email);
        eventLogger.handleSecurityEvent(DELETE_USER, getAdminEmailFromContext(), email);
    }

    @Transactional
    public UserDTO updateUserRole(UserUpdateDTO dto) {
        User user = findUser(dto.getUser());
        SecurityEventType securityAction = null;
        String eventObject = null;
        try {
            var targetRole = UserRole.valueOf(dto.getRole());
            switch (dto.getOperation()) {
                case "GRANT" -> {
                    // get the group of first user's role
                    var userGroup = user.getRoles().stream().findFirst().orElseThrow().getRoleGroup();
                    // verify the provided role belongs to same group, else throw exception
                    if (!targetRole.getRoleGroup().equals(userGroup)) throw new RoleGroupConstraintValidationException();
                    // checks completed, add new role
                    user.getRoles().add(targetRole);
                    securityAction = GRANT_ROLE;
                    eventObject = "Grant role %s to %s".formatted(dto.getRole(), user.getEmail());
                }
                case "REMOVE" -> {
                    var userRoles = user.getRoles();
                    // do not remove admin role
                    if (userRoles.contains(UserRole.ADMINISTRATOR)) throw new DeleteAdminException();
                    // check if user has role assigned
                    if (!userRoles.contains(targetRole)) throw new UserRoleNotAssigned();
                    // check if user has only one role
                    if (userRoles.size() == 1) throw new LastRoleException();

                    user.getRoles().remove(targetRole);
                    securityAction = REMOVE_ROLE;
                    eventObject = "Remove role %s from %s".formatted(dto.getRole(), user.getEmail());
                }
            }
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new RoleNotFoundException();
        }
        userRepository.save(user);
        appLogger.info("{} ROLE {} - user {}", dto.getOperation(), dto.getRole(), dto.getUser());
        eventLogger.handleSecurityEvent(securityAction, getAdminEmailFromContext(), eventObject);
        return modelMapper.map(user, UserDTO.class);
    }

    public void updateUserAccess(String operation, String user, String requestPath) {
        String requestBy;
        try {
            // retrieve email if admin made the request
            requestBy = SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (NullPointerException ignored) {
            requestBy = user;
        }

        switch (operation) {
            case "LOCK" -> lockUser(user, requestPath, requestBy);
            case "UNLOCK" -> unlockUser(user, requestBy);
            default -> throw new IllegalArgumentException("Unknown operation: " + operation);
        }
    }

    private void unlockUser(String email, String requestBy) {
        User user = findUser(email);
        if (user.isAccountLocked()) {
            user.lockAccount(false);
            userRepository.save(user);
            appLogger.info("User {} UNLOCKED", email);
            eventLogger.handleSecurityEvent(SecurityEventType.UNLOCK_USER, requestBy, "Unlock user " + email);
        }
    }

    private void lockUser(String email, String requestPath, String requestBy) {
        try {
            User user = findUser(email);
            if (user.getRoles().contains(UserRole.ADMINISTRATOR)) throw new LockAdminException(); // prevent locking admin

            if (user.isAccountNonLocked()) {
                user.lockAccount(true);
                userRepository.save(user);
                appLogger.info("User {} LOCKED", email);
                eventLogger.handleSecurityEvent(
                        SecurityEventType.LOCK_USER, requestBy, "Lock user " + email);
                // requestPath != null ? requestPath : requestContext.getServletPath()
            }
        } catch (UserNotFoundException ex) {
            if (requestBy != null) throw new UserNotFoundException();
        }
    }

    private String getAdminEmailFromContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
