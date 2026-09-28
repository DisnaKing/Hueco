# Migraciones del esquema

El esquema de la base de datos lo gestiona **Flyway**. Cada cambio es un fichero SQL numerado en
`src/main/resources/db/migration`, que se aplica una sola vez y en orden al arrancar la aplicación. Flyway apunta en
la tabla `flyway_schema_history` qué migraciones se han aplicado, con su checksum.

Hibernate no crea ni cambia tablas: con `spring.jpa.hibernate.ddl-auto=validate` solo comprueba al arrancar que las
entidades cuadran con el esquema. Si una entidad cambia sin su migración, la aplicación **no arranca** y los tests fallan.

## Qué hay

| Fichero | Qué hace |
|---|---|
| `db/migration/V1__esquema_inicial.sql` | El esquema tal como lo creaba Hibernate antes de Flyway, con nombres legibles en las restricciones (`pk_cita`, `uk_cliente_telefono`, `fk_cita_cliente`…) |
| `db/dev/R__datos_dev.sql` | Solo perfil `dev`: la peluquería de ejemplo. Es repetible (`R__`): Flyway la ejecuta después de las versionadas y otra vez si cambia el fichero, pero solo carga datos si no hay negocio |

`db/dev` solo está en `spring.flyway.locations` del perfil `dev` (`application-dev.properties`). En producción y en
los tests no se carga. Los datos de un comercio real se dan de alta con [`alta-comercio.sql`](alta-comercio.sql).

## Reglas

1. **Una migración aplicada no se edita nunca.** Flyway compara el checksum y la aplicación no arrancaría en las bases
   donde ya se aplicó. Para corregirla, otra migración.
2. **Solo hacia delante.** No hay rollback: un error se arregla con una migración nueva.
3. **Nombre**: `V<n>__descripcion.sql`, con `n` el siguiente número libre y la descripción en minúsculas con guiones
   bajos (`V2__indices_cita.sql`).
4. **Todo cambio de entidad lleva su migración**, en el mismo commit. `validate` lo comprueba al arrancar y en los tests.
5. **Nombres de restricciones explícitos**: `pk_`, `uk_`, `fk_`, `ck_` o `ix_` más tabla y columna, para que los
   errores y las migraciones posteriores los puedan citar.
6. **Esquema, no datos de un comercio.** Los datos de alta van en `alta-comercio.sql` y los de ejemplo en `db/dev`.

## Añadir una migración

1. Cambiar la entidad.
2. Crear `db/migration/V<n>__....sql` con el `ALTER TABLE`, `CREATE INDEX`… correspondiente.
3. `./mvnw test`: los tests arrancan un Postgres vacío, aplican todas las migraciones y validan las entidades.
4. En desarrollo se aplica sobre la base existente al arrancar. Para partir de cero: `docker compose down -v`.

## Tests

- Las clases con el perfil `test` comparten un Postgres de Testcontainers
  (`spring.datasource.url=jdbc:tc:postgresql:17-alpine:///hueco?TC_DAEMON=true`). Cada clase empieza con
  `limpiar.sql` (`TRUNCATE ... RESTART IDENTITY CASCADE`) y carga sus datos con `@Sql`.
- `HuecoApplicationTests` (perfil `dev`) y `ProdSinSeedTest` (perfil `prod`) importan `PostgresPropio`: un contenedor
  nuevo solo para cada una, para comprobar qué deja cada perfil en una base vacía.
