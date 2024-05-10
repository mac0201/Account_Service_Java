package account.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class PayrollCreateDTO {
    @NotBlank(message = "Employee email is required!")
    private String employee;
    @NotBlank(message = "Period is required!")
    @Pattern(regexp = "^((0[1-9])|(1[0-2]))-(\\d{4})$", message = "Period must match following pattern: MM-YYYY")
    private String period;
    @NotNull(message = "Salary is required!")
    @Min(value = 0, message = "Salary must be >= 0")
    private long salary;
}