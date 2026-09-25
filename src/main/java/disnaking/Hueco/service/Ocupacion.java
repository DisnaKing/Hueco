package disnaking.Hueco.service;

import disnaking.Hueco.model.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;

// Lo que el cálculo de huecos necesita saber de una cita
public record Ocupacion(LocalDate fecha, LocalTime hora, int duracionMinutos, EstadoCita estado) {
}
