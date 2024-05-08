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
public class PasswordResetDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("new_password")
    private String newPassword;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;

    public PasswordResetDTO(String email, String status) {
        this.email = email;
        this.status = status;
    }
}
