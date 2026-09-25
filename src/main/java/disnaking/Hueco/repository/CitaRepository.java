package disnaking.Hueco.repository;

import disnaking.Hueco.model.Cita;
import disnaking.Hueco.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByFechaBetween(LocalDate desde, LocalDate hasta);

    Optional<Cita> findByToken(String token);

    // Citas vivas de hoy en adelante de un teléfono, para el límite por cliente
    long countByClienteTelefonoAndFechaGreaterThanEqualAndEstadoIn(String telefono, LocalDate desde,
                                                                   Collection<EstadoCita> estados);
}
