package account.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordResetDTO {
    @JsonProperty("new_password")
    private String newPassword;
    private String email;
    private String status;

    public PasswordResetDTO(String email, String status) {
        this.email = email;
        this.status = status;
    }
}
