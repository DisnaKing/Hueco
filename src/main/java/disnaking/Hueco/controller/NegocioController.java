package disnaking.Hueco.controller;

import disnaking.Hueco.DTO.Negocio.negocioDTO;
import disnaking.Hueco.service.NegocioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/negocio")
public class NegocioController {

    private final NegocioService negocioService;

    public NegocioController(NegocioService negocioService) {
        this.negocioService = negocioService;
    }

    @GetMapping
    public negocioDTO mostrar() {
        return negocioService.obtener().orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Negocio no encontrado"));
    }
}
