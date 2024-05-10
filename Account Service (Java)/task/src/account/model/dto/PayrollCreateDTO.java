package account.model.dto;

import static account.util.PayrollUtils.PAYROLL_REGEX;
import static account.util.PayrollUtils.PAYROLL_REGEX_ERROR;

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
    @Pattern(regexp = PAYROLL_REGEX, message = PAYROLL_REGEX_ERROR)
    private String period;
    @NotNull(message = "Salary is required!")
    @Min(value = 0, message = "Salary must be >= 0")
    private long salary;
}