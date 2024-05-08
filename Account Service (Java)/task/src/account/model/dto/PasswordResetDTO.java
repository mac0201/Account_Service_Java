package account.model.dto;

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
////    @JsonProperty("user_id")
//    private long userId;
    @JsonProperty("password")
    private String currentPassword;
    @JsonProperty("new_password")
    private String newPassword;
}
