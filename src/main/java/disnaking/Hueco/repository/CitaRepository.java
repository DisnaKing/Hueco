package disnaking.Hueco.repository;

import disnaking.Hueco.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
