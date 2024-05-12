package account.controller;

import account.model.dto.UserDTO;
import account.model.dto.UserRoleUpdateDTO;
import account.service.AdminService;
import account.util.ResponseBuilders.SuccessResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/admin/user/")
@Validated
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        List<UserDTO> response = adminService.findAllUsers();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/role")
    public ResponseEntity<Object> updateRole(@Valid @RequestBody UserRoleUpdateDTO dto) {
        return ResponseEntity.ok().body(adminService.updateUserRole(dto));
    }

    @DeleteMapping(value = {"/{email}", ""})
    public ResponseEntity<Object> deleteUser(@PathVariable(required = true) String email) {
        adminService.deleteUser(email);
        var response = SuccessResponse.builder()
                .user(email)
                .status("Deleted successfully!")
                .build();
        return ResponseEntity.ok().body(response);
    }
}
