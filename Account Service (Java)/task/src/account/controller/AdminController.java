package account.controller;

import account.model.dto.UserDTO;
import account.model.dto.UserUpdateDTO;
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
    public ResponseEntity<Object> updateRole(@Valid @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok().body(adminService.updateUserRole(dto));
    }

    @DeleteMapping(value = {"/{email}", ""})
    public ResponseEntity<Object> deleteUser(@PathVariable String email) {
        adminService.deleteUser(email);
        var response = SuccessResponse.builder()
                .user(email)
                .status("Deleted successfully!")
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/access")
//    public ResponseEntity<Object> updateUserAccess(@RequestBody UserAccessUpdateDTO accessDTO) {
    public ResponseEntity<Object> updateUserAccess(@RequestBody UserUpdateDTO accessDTO) {
        adminService.updateUserAccess(accessDTO.getOperation(), accessDTO.getUser(), null);
        System.out.println("UPDATE ACCESS FOR: " + accessDTO.getUser());
        var response = SuccessResponse.builder().status("User %s %s!".formatted(accessDTO.getUser().toLowerCase(),
                accessDTO.getOperation().equals("LOCK") ? "locked" : "unlocked")).build();
        return ResponseEntity.ok().body(response);
    }
}
