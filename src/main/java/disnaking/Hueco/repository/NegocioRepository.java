package disnaking.Hueco.repository;

import disnaking.Hueco.model.Negocio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NegocioRepository extends JpaRepository<Negocio, Long> {

    // SELECT … FOR UPDATE: pone en fila las reservas del comercio para que dos no cojan la misma hora
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from Negocio n where n.id = :id")
    Optional<Negocio> findByIdParaReservar(long id);
}
