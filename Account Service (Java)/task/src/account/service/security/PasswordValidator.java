package account.service.security;

import org.springframework.stereotype.Component;

import account.exceptions.definitions.UserAuthExceptions.PasswordValidationException;

import java.util.List;

@Component
public class PasswordValidator {

    private List<String> getBreachedPasswords() {
        return List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
    }

    public void validatePassword(String password) {
        if (password.trim().length() < 12)
            throw new PasswordValidationException("Password length must be 12 chars minimum!");
        if (getBreachedPasswords().contains(password))
            throw new PasswordValidationException("The password is in the hacker's database!");
    }
}
