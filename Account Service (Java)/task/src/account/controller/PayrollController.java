package account.controller;


import account.service.PayrollService;
import org.h2.util.json.JSONString;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<Object> getEmployeePayroll() {
        System.out.println("VALID!");
        return ResponseEntity.ok().body(payrollService.getUser());
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<String> uploadPayroll() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<String> updatePayment() {
        return ResponseEntity.ok().build();
    }

}
