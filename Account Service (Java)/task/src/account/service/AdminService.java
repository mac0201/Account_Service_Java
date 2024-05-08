package account.service;

import account.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;

    private final static Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }





}
