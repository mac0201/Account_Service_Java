package account.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserRegistrationDTO {
        @NotBlank(message = "Name cannot be blank!")
        private String name;

        @NotBlank(message = "Last name cannot be blank!")
        private String lastname;

        @Email
        @NotBlank(message = "Email cannot be blank!")
        @Pattern(regexp = "^.+@acme\\.com$", message = "Email must end with @acme.com")
        private String email;

        @NotBlank(message = "Password cannot be blank!")
        @ToString.Exclude
        private String password;

        // Set email to lowercase
        public void setEmail(String email) { this.email = email.toLowerCase(); }
}
