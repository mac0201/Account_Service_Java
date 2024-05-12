package account.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
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
