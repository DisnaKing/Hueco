package disnaking.Hueco.model;

public enum EstadoDia {
    // Queda al menos una hora libre
    LIBRE,
    // Se trabaja, pero no queda ninguna hora
    COMPLETO,
    // Sin tramos ese día de la semana, o con cierre puntual
    CERRADO
}
