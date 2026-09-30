#!/bin/sh
# Copia de la base de datos cada noche a las HORA_BACKUP (hora de TZ) en /backups, que es ./backups en el servidor.
# Formato custom de pg_dump (se restaura con pg_restore). Se guardan los últimos DIAS_BACKUP días.
# PGHOST, PGUSER, PGPASSWORD y PGDATABASE los pone compose.prod.yaml.

HORA=${HORA_BACKUP:-3}
DIAS=${DIAS_BACKUP:-14}

copia() {
    fichero="/backups/hueco-$(date +%Y-%m-%d-%H%M).dump"
    if pg_dump -Fc -f "$fichero.tmp"; then
        mv "$fichero.tmp" "$fichero"
        echo "$(date) copia hecha: $fichero"
    else
        rm -f "$fichero.tmp"
        echo "$(date) ERROR: pg_dump ha fallado" >&2
    fi
    # Más de DIAS días: fuera
    find /backups -name 'hueco-*.dump' -mtime +$((DIAS - 1)) -delete
}

while true; do
    set -- $(date +'%H %M %S')
    ahora=$(( ${1#0} * 3600 + ${2#0} * 60 + ${3#0} ))
    espera=$(( (HORA * 3600 - ahora + 86400) % 86400 ))
    [ "$espera" -eq 0 ] && espera=86400
    echo "$(date) próxima copia en $espera s"
    sleep "$espera"
    copia
done
