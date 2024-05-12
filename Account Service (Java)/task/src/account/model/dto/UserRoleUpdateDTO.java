package account.model.dto;

import account.model.roles.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRoleUpdateDTO {
    @NotBlank
    @Email
    private String user;
    @NotBlank
    private String role;
    @NotNull
    private UserRole.RoleModifyOperation operation;
}
