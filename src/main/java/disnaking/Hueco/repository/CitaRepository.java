package disnaking.Hueco.repository;

import disnaking.Hueco.model.Cita;
import disnaking.Hueco.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

}
