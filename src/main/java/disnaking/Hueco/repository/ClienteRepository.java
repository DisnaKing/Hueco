package disnaking.Hueco.repository;

import disnaking.Hueco.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository  extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByTelefono(String telefono);
}
