package account.model.security.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SecurityEventType {

    CREATE_USER("A user has been successfully registered"),
    CHANGE_PASSWORD("A user has changed the password successfully"),
    ACCESS_DENIED("A user is trying to access a resource without access rights"),
    LOGIN_FAILED("Failed authentication"),
    GRANT_ROLE("A role is granted to a user"),
    REMOVE_ROLE("A role has been revoked"),
    LOCK_USER("The Administrator has locked a user"),
    UNLOCK_USER("The Administrator has unlocked a user"),
    DELETE_USER("The Administrator has deleted a user"),
    BRUTE_FORCE("A user has been blocked on suspicion of a brute force attack");

    private final String description;

}
