package disnaking.Hueco.repository;

import disnaking.Hueco.model.Cita;
import disnaking.Hueco.model.Cliente;
import disnaking.Hueco.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

}
