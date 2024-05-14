package account.controller;

import account.service.AuditorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security/")
@AllArgsConstructor
public class AuditorController {

    private final AuditorService auditorService;

    @GetMapping("/events")
    public ResponseEntity<Object> findAllLogs() {
        return ResponseEntity.ok().body(auditorService.findAllLogs());
    }

}
