package account.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserUpdateDTO {

    @NotBlank
    @Email
    private String user; // for both role and access updates
    private String role; // only for role updates
    @NotBlank
    private String operation; // for both updates

    public UserUpdateDTO(String user, String operation) {
        this.user = user;
        this.operation = operation;
        this.role = null;
    }

    public void setUser(String user) {
        this.user = user.toLowerCase();
    }
}
