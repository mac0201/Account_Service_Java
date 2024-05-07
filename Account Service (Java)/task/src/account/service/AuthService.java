package account.service;

import account.exceptions.CustomExceptions;
import account.model.User;
import account.model.dto.PasswordResetDTO;
import account.model.dto.UserRegistrationDTO;
import account.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AuthService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public User registerUser(UserRegistrationDTO registrationDTO) {

        if (userRepository.existsByEmail(registrationDTO.getEmail()))
            throw new CustomExceptions.UserAlreadyExistsException();

        User user = modelMapper.map(registrationDTO, User.class);

        return userRepository.save(user);
    }

    public User updatePassword(PasswordResetDTO resetDTO) {
//        User user = userRepository.findById(resetDTO.getUserId()).orElseThrow(CustomExceptions.UserNotFoundException::new);
        return null;
    }


}
