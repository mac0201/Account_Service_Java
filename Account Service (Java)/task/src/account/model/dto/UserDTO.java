package account.model.dto;

import account.service.roles.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
    private long id;
    private String name;
    private String lastname;
    private String email;
    private List<String> roles;

    // Convert set of UserRole enums to a list of their names
    public void setRoles(Set<UserRole> roles) {
        this.roles = roles.stream().map(UserRole::getName).sorted().toList();
    }
}
