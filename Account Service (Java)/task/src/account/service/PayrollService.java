package account.service;

import account.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PayrollService {

    private final AuthService authService;

    public PayrollService(AuthService authService) {
        this.authService = authService;
    }

    public User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return authService.findUser(auth.getName());
    }


}
