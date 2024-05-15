package account.service;

import account.exceptions.definitions.AdminExceptions.*;
import account.model.User;
import account.model.dto.UserDTO;
import account.model.dto.UserRoleUpdateDTO;
//import account.model.security.events.SecurityEventBroadcaster;
import account.model.security.events.SecurityEventLogger;
import account.model.security.events.SecurityEventType;
import account.repository.UserRepository;
import account.model.roles.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import account.exceptions.definitions.UserAuthExceptions.UserNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

import static account.model.security.events.SecurityEventType.*;

@Service
@AllArgsConstructor
public class AdminService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    private final SecurityEventLogger eventLogger;
    private HttpServletRequest requestContext;

//    private final SecurityEventBroadcaster eventBroadcaster;

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
        // Get info about current user
//        eventBroadcaster.broadcastSecurityEvent(DELETE_USER, getAdminEmailFromContext(), email, "path...");
        eventLogger.handleSecurityEvent(DELETE_USER, getAdminEmailFromContext(), email, requestContext.getServletPath());
    }

    @Transactional
    public UserDTO updateUserRole(UserRoleUpdateDTO dto) {
        User user = findUser(dto.getUser());
        SecurityEventType securityAction = null;
        String eventObject = null;
        try {
            var targetRole = UserRole.valueOf(dto.getRole());
            switch (dto.getOperation()) {
                case GRANT -> {
                    // get the group of first user's role
                    var userGroup = user.getRoles().stream().findFirst().orElseThrow().getRoleGroup();
                    // verify the provided role belongs to same group, else throw exception
                    if (!targetRole.getRoleGroup().equals(userGroup)) throw new RoleGroupConstraintValidationException();
                    // checks completed, add new role
                    user.getRoles().add(targetRole);
                    securityAction = GRANT_ROLE;
                    eventObject = "Grant role %s to %s".formatted(dto.getRole(), user.getEmail());
                }
                case REMOVE -> {
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
        LOGGER.info("{} ROLE {} - user {}", dto.getOperation(), dto.getRole(), dto.getUser());
        eventLogger.handleSecurityEvent(securityAction, getAdminEmailFromContext(), eventObject, requestContext.getServletPath());
//        eventBroadcaster.broadcastSecurityEvent(securityAction, getAdminEmailFromContext(), user.getEmail(), "path...");
        return modelMapper.map(user, UserDTO.class);
    }

    private String getAdminEmailFromContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public boolean isUserNonLocked(String email) {
        try {
            User user = findUser(email);
//        return userRepository.existsByEmailIgnoreCaseAndAccountLockedTrue(email);
            return user.isAccountNonLocked();
        } catch (UserNotFoundException ex) {
            return true;
        }
    }

    public void updateUserAccess(String operation, String user, String requestPath) {
        String requestBy = null;
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

    private boolean unlockUser(String email, String requestBy) {
        User user = findUser(email);
        if (user.isAccountLocked()) {
            user.lockAccount(false);
            System.out.printf("Account %s unlocked!", email);
            userRepository.save(user);
            eventLogger.handleSecurityEvent(SecurityEventType.UNLOCK_USER, requestBy, "Unlock user " + email, requestContext.getServletPath());
        } else {
            System.out.println("ACCOUNT NOT LOCKED!");
        }
        return true;
    }

    private void lockUser(String email, String requestPath, String requestBy) {
        try {
            User user = findUser(email);
            if (user.getRoles().contains(UserRole.ADMINISTRATOR)) throw new DeleteAdminException();

            if (user.isAccountNonLocked()) {
                System.out.println(user.getEmail() + " ACCOUNT IS UNLOCKED - LOCKING...");
                user.lockAccount(true);
                userRepository.save(user);
                eventLogger.handleSecurityEvent(
                        SecurityEventType.LOCK_USER, requestBy, "Lock user " +email,
                        requestPath != null ? requestPath : requestContext.getServletPath());
            } else {
                System.out.println("ACCOUNT IS ALREADY LOCKED!!");
            }
        } catch (UserNotFoundException ex) {
//            System.err.println("User not found exception ignored");
            // throw exception only if admin requested the lock (otherwise it was invoked by authentication handler)
            if (requestBy != null) throw new UserNotFoundException();
        }
    }
}
