package account.model.roles;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    ADMINISTRATOR("ROLE_ADMINISTRATOR", RoleGroup.GROUP_ADMIN),
    USER("ROLE_USER", RoleGroup.GROUP_BUSINESS),
    ACCOUNTANT("ROLE_ACCOUNTANT", RoleGroup.GROUP_BUSINESS),
    AUDITOR("ROLE_AUDITOR", RoleGroup.GROUP_BUSINESS);
    private final String name;
    private final RoleGroup roleGroup;

    @Override
    public String toString() {
        return this.name;
    }

    public enum RoleGroup {
        GROUP_ADMIN,
        GROUP_BUSINESS
    }

    public enum RoleModifyOperation {
        GRANT,
        REMOVE
    }
}
