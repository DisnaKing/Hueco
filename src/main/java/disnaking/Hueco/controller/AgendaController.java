package disnaking.Hueco.controller;

import disnaking.Hueco.DTO.Agenda.agendaDiaDTO;
import disnaking.Hueco.service.AgendaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

// Protegido con la credencial del comercio (ver SeguridadConfig)
@RestController
@RequestMapping("/api/agenda")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    // GET /api/agenda?desde=2026-10-01 — siete días; sin desde, a partir de hoy
    @GetMapping
    public List<agendaDiaDTO> semana(@RequestParam(required = false)
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde) {
        return agendaService.semana(desde);
    }
}
