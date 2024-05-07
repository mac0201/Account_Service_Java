package account.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
//@JsonIgnoreProperties
public class UserRegistrationDTO {
        @NotBlank @NotNull
        @Size(min = 2, max = 30)
        private String name;
        @NotBlank @NotNull
        @Size(min = 2, max = 30)
        private String lastname;
        @Email
        @NotBlank
//        @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
        @Pattern(regexp = "^.+@acme.com$")
        private String email;
        @NotBlank
        @Size(min = 5, max = 30) // @ToString.Exclude
        private String password;

}
