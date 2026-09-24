package disnaking.Hueco.model;

public enum EstadoApertura {
    // Ahora está dentro de un tramo; hora = cierre de ese tramo
    ABIERTO,
    // Cerrado, pero hoy abre más tarde; hora = próxima apertura
    ABRE_HOY,
    // Ya no abre hoy; dia y hora = próxima apertura
    CERRADO_HOY
}
