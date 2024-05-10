package account.controller;


import account.model.dto.PayrollCreateDTO;
import account.model.dto.PayrollGetDTO;
import account.service.AuthService;
import account.service.PayrollService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/")
public class PayrollController {

    private final PayrollService payrollService;
    private final AuthService authService;

    public PayrollController(PayrollService payrollService, AuthService authService) {
        this.payrollService = payrollService;
        this.authService = authService;
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<Object> getEmployeePayrolls(@RequestParam(required = false) @Pattern(regexp = "^((0[1-9])|(1[0-2]))-(\\d{4})$") String period) {
        List<PayrollGetDTO> payrolls = payrollService.getPayrollsForCurrentUser(period);
        if (payrolls.size() == 1) return ResponseEntity.ok(payrolls.get(0));
        return ResponseEntity.ok().body(payrolls);
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<Object> uploadPayrolls(
            @RequestBody
            @NotEmpty(message = "Payroll list cannot be empty!")
            List<@Valid PayrollCreateDTO> payrollList) throws Exception {
        payrollService.addPayrolls(payrollList);
        return ResponseEntity.ok().body(generateSuccessResponse("Added successfully!"));
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<Object> updatePayment(@RequestBody @Valid PayrollCreateDTO payrollDTO) {
        payrollService.updatePayroll(payrollDTO);
        return ResponseEntity.ok().body(generateSuccessResponse("Updated successfully!"));
    }

    private Map<String, String> generateSuccessResponse(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("status", message);
        return body;
    }

}
