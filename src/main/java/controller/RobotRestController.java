package controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.robot.mr.enums.Direction;
import com.robot.mr.facade.RobotController;

@RestController
@RequestMapping("/robot")
public class RobotRestController {
 
    private final RobotController facade;
 
    public RobotRestController(RobotController facade) {
        this.facade = facade;
    }
 
    @PostMapping("/move")
    public ResponseEntity<Map<String, String>> move(
            @RequestParam String direction,
            @RequestParam(defaultValue = "50") int speed) {
        try {
            facade.move(Direction.valueOf(direction.toUpperCase()), speed);
            return ResponseEntity.ok(Map.of("status", "OK", "direction", direction, "speed", String.valueOf(speed)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Direção inválida: " + direction));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).body(Map.of("error", e.getMessage()));
        }
    }
 
    @PostMapping("/stop")
    public ResponseEntity<Map<String, String>> stop() {
        try {
            facade.stop();
            return ResponseEntity.ok(Map.of("status", "OK"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).body(Map.of("error", e.getMessage()));
        }
    }
 
    @PostMapping("/photo")
    public ResponseEntity<Map<String, String>> photo() {
        try {
            facade.takePhoto();
            String cached = facade.getCachedPhoto();
 
            // Foto ainda não chegou do ESP32 — processamento assíncrono
            if (cached == null) {
                return ResponseEntity.accepted()
                    .body(Map.of("status", "PROCESSING", "message", "Foto solicitada, aguarde o evento PHOTO_CAPTURED"));
            }
 
            return ResponseEntity.ok(Map.of("status", "OK", "photo", cached));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).body(Map.of("error", e.getMessage()));
        }
    }
 
    @GetMapping("/photo/cached")
    public ResponseEntity<Map<String, String>> getCachedPhoto() {
        String cached = facade.getCachedPhoto();
        if (cached == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Nenhuma foto disponível em cache"));
        }
        return ResponseEntity.ok(Map.of("status", "OK", "photo", cached));
    }
 
    @PostMapping("/guard/enable")
    public ResponseEntity<Map<String, String>> enableGuard() {
        try {
            facade.enableGuardMode();
            return ResponseEntity.ok(Map.of("status", "OK", "guardMode", "ON"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).body(Map.of("error", e.getMessage()));
        }
    }
 
    @PostMapping("/guard/disable")
    public ResponseEntity<Map<String, String>> disableGuard() {
        facade.disableGuardMode();
        return ResponseEntity.ok(Map.of("status", "OK", "guardMode", "OFF"));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(facade.getStatus());
    }
}
