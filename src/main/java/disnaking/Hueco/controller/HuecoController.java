package disnaking.Hueco.controller;

import disnaking.Hueco.DTO.Hueco.diaHuecosDTO;
import disnaking.Hueco.service.HuecoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/huecos")
public class HuecoController {

    private final HuecoService huecoService;

    public HuecoController(HuecoService huecoService) {
        this.huecoService = huecoService;
    }

    // GET /api/huecos?servicios=1,3 — lo que no sea un número se ignora, como en el frontend
    @GetMapping
    public List<diaHuecosDTO> listar(@RequestParam(defaultValue = "") String servicios) {
        List<Long> ids = Arrays.stream(servicios.split(","))
                .map(String::trim)
                .filter(s -> s.matches("\\d{1,18}"))
                .map(Long::valueOf)
                .distinct()
                .toList();
        return huecoService.huecos(ids);
    }
}
