package account.service;

import org.springframework.stereotype.Component;

import account.exceptions.CustomExceptions.PasswordValidationException;

import java.util.List;

@Component
public class PasswordValidator {

    private List<String> getBreachedPasswords() {
        return List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
    }

    public void validatePassword(String password) {
        if (password.length() < 12)
            throw new PasswordValidationException("Password length must be 12 chars minimum!");
        if (getBreachedPasswords().contains(password))
            throw new PasswordValidationException("The password is in the hacker's database!");
    }
}
