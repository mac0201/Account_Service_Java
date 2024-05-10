package account.controller;

import account.util.ResponseBuilder;
import account.model.dto.PayrollCreateDTO;
import account.model.dto.PayrollGetDTO;
import account.service.PayrollService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import static account.util.PayrollUtils.PAYROLL_REGEX;
import static account.util.PayrollUtils.PAYROLL_REGEX_ERROR;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/")
public class PayrollController {

    private final PayrollService payrollService;
    private final ResponseBuilder responseBuilder;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
        this.responseBuilder = new ResponseBuilder();
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<Object> getEmployeePayrolls(
            @RequestParam(required = false)
            @Pattern(regexp = PAYROLL_REGEX, message = PAYROLL_REGEX_ERROR)
            String period) {
        List<PayrollGetDTO> payrolls = payrollService.getPayrollsForCurrentUser(period);
        if (payrolls.size() == 1) return ResponseEntity.ok(payrolls.get(0));
        return ResponseEntity.ok().body(payrolls);
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<Object> uploadPayrolls(
            @RequestBody
            @NotEmpty(message = "Payroll list cannot be empty!")
            List<@Valid PayrollCreateDTO> payrollList) {
        payrollService.addPayrolls(payrollList);
        return ResponseEntity.ok().body(responseBuilder.setStatus("Added successfully!").build());
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<Object> updatePayment(@RequestBody @Valid PayrollCreateDTO payrollDTO) {
        payrollService.updatePayroll(payrollDTO);
        return ResponseEntity.ok().body(responseBuilder.setStatus("Updated successfully!").build());
    }

}
