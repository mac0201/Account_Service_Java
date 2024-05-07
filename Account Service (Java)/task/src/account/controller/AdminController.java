package account.controller;

import account.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/user/role")
    public ResponseEntity<String> updateRole() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<String> findAllUsers() {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser() {
        return ResponseEntity.ok().build();
    }

}
