package account.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserRegistrationDTO {
        @NotBlank
        @Size(min = 2, max = 30)
        private String name;

        @NotBlank
        @Size(min = 2, max = 30)
        private String lastname;

        @Email
        @NotBlank
        @Pattern(regexp = "^.+@acme\\.com$")
        private String email;

        @NotBlank
        @ToString.Exclude
        private String password;

        // Set email to lowercase
        public void setEmail(String email) { this.email = email.toLowerCase(); }
}
