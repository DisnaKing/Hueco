package disnaking.Hueco.service;

import disnaking.Hueco.config.ReservaPublicaProperties;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

// Reservas de la última hora por IP, en memoria: hay una instalación por comercio y se
// reinicia con el servidor, que para frenar a un bot es suficiente
@Component
public class LimiteReservasPorIp {

    private static final Duration VENTANA = Duration.ofHours(1);

    private final Clock clock;
    private final ReservaPublicaProperties reglas;
    private final Map<String, Deque<Instant>> reservas = new HashMap<>();

    public LimiteReservasPorIp(Clock clock, ReservaPublicaProperties reglas) {
        this.clock = clock;
        this.reglas = reglas;
    }

    public synchronized boolean permitido(String ip) {
        return recientes(ip).size() < reglas.maxReservasPorIpHora();
    }

    public synchronized void registrar(String ip) {
        recientes(ip).addLast(clock.instant());
    }

    private Deque<Instant> recientes(String ip) {
        Deque<Instant> lista = reservas.computeIfAbsent(ip, k -> new ArrayDeque<>());
        Instant limite = clock.instant().minus(VENTANA);
        while (!lista.isEmpty() && !lista.peekFirst().isAfter(limite)) lista.pollFirst();
        return lista;
    }
}
