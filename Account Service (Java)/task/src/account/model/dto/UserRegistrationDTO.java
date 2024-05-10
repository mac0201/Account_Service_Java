package account.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserRegistrationDTO {
        //        @Size(min = 2, max = 30, message = "Name must be between 2 and 30 characters long!")
        @NotBlank(message = "Name cannot be blank!")
        private String name;

        //        @Size(min = 2, max = 30)
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
