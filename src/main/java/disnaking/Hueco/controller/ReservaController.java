package disnaking.Hueco.controller;

import disnaking.Hueco.DTO.Reserva.reservaCrearDTO;
import disnaking.Hueco.DTO.Reserva.reservaResumenDTO;
import disnaking.Hueco.Exception.Reserva.ReservaInvalidaException;
import disnaking.Hueco.Exception.Reserva.ReservaRechazadaException;
import disnaking.Hueco.service.ReservaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

// Reserva pública como invitado: sin credencial (ver SeguridadConfig)
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> reservar(@RequestBody reservaCrearDTO datos, HttpServletRequest request) {
        String token = reservaService.reservar(datos, request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("token", token));
    }

    @GetMapping("/{token}")
    public reservaResumenDTO resumen(@PathVariable String token) {
        return reservaService.resumen(token);
    }

    // ?nombre= es el nombre del comercio, que vive en el frontend (business.config.js)
    @GetMapping("/{token}/cita.ics")
    public ResponseEntity<String> ics(@PathVariable String token, @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "calendar", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("cita.ics").build().toString())
                .body(reservaService.ics(token, nombre));
    }

    @ExceptionHandler(ReservaInvalidaException.class)
    public ResponseEntity<Map<String, Object>> invalida(ReservaInvalidaException e) {
        return ResponseEntity.badRequest().body(Map.of("errores", e.getErrores()));
    }

    @ExceptionHandler(ReservaRechazadaException.class)
    public ResponseEntity<Map<String, String>> rechazada(ReservaRechazadaException e) {
        return ResponseEntity.status(e.getMotivo().getStatus()).body(Map.of("motivo", e.getMotivo().name()));
    }
}
