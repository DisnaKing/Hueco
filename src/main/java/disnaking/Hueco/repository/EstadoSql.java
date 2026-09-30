package disnaking.Hueco.repository;

import java.sql.SQLException;

// SQLState de PostgreSQL detrás de una excepción de Spring o Hibernate
public final class EstadoSql {

    // exclusion_violation: la cita se solapa con otra (restricción ex_cita_solape)
    public static final String SOLAPE = "23P01";

    private EstadoSql() {}

    // null si no viene de la base de datos
    public static String de(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && sql.getSQLState() != null) return sql.getSQLState();
        }
        return null;
    }

    public static boolean esSolape(Throwable e) {
        return SOLAPE.equals(de(e));
    }
}
