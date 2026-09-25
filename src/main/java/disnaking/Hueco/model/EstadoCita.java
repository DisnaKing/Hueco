package disnaking.Hueco.model;

public enum EstadoCita {
    PENDIENTE,
    CONFIRMADA,
    CANCELADA,
    COMPLETADA,
    NO_SHOW;

    // Solo las citas vivas ocupan hueco; si PENDIENTE no bloqueara, dos personas podrían coger la misma hora
    public boolean bloqueaHueco() {
        return this == PENDIENTE || this == CONFIRMADA;
    }
}
