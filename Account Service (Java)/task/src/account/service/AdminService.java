package account.service;

import account.exceptions.definitions.AdminExceptions.*;
import account.model.User;
import account.model.dto.UserDTO;
import account.model.dto.UserRoleUpdateDTO;
import account.model.security.events.SecurityEventBroadcaster;
import account.model.security.events.SecurityEventType;
import account.model.security.events.SecurityLog;
import account.repository.UserRepository;
import account.model.roles.UserRole;
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

    private final SecurityEventBroadcaster eventBroadcaster;

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
        eventBroadcaster.broadcastSecurityEvent(DELETE_USER, getAdminEmailFromContext(), email, "path...");
    }

    @Transactional
    public UserDTO updateUserRole(UserRoleUpdateDTO dto) {
        User user = findUser(dto.getUser());
        SecurityEventType securityAction = null;
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
                }
            }
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new RoleNotFoundException();
        }
        userRepository.save(user);
        LOGGER.info("{} ROLE {} - user {}", dto.getOperation(), dto.getRole(), dto.getUser());
        eventBroadcaster.broadcastSecurityEvent(securityAction, getAdminEmailFromContext(), user.getEmail(), "path...");
        return modelMapper.map(user, UserDTO.class);
    }

    private String getAdminEmailFromContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
