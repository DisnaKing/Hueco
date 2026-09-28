package disnaking.Hueco.service;

import disnaking.Hueco.config.EmailProperties;
import disnaking.Hueco.model.Cita;
import disnaking.Hueco.model.Negocio;
import disnaking.Hueco.repository.CitaRepository;
import disnaking.Hueco.repository.NegocioRepository;
import disnaking.Hueco.service.CorreosReserva.Correo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

// Manda los emails de cada cita nueva cuando la reserva ya está guardada y en segundo plano.
// Si el envío falla se reintenta y, al final, se deja en el log: la reserva nunca falla por el email.
@Component
public class AvisosReserva {

    private static final Logger log = LoggerFactory.getLogger(AvisosReserva.class);
    private static final long NEGOCIO_ID = 1L;

    // Sin spring.mail.host no hay JavaMailSender
    private final ObjectProvider<JavaMailSender> mailSender;
    private final EmailProperties email;
    private final CitaRepository citaRepository;
    private final NegocioRepository negocioRepository;
    private final TransactionTemplate lectura;
    private final Clock clock;

    public AvisosReserva(ObjectProvider<JavaMailSender> mailSender, EmailProperties email,
                         CitaRepository citaRepository, NegocioRepository negocioRepository,
                         PlatformTransactionManager transacciones, Clock clock) {
        this.mailSender = mailSender;
        this.email = email;
        this.citaRepository = citaRepository;
        this.negocioRepository = negocioRepository;
        this.lectura = new TransactionTemplate(transacciones);
        this.lectura.setReadOnly(true);
        this.clock = clock;
    }

    @Async
    @TransactionalEventListener
    public void alReservar(CitaReservada evento) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || !email.configurado()) return;

        // Los textos se preparan dentro de una transacción (servicios y cliente son perezosos) y se mandan fuera
        List<Correo> correos = lectura.execute(estado -> {
            Cita cita = citaRepository.findById(evento.citaId()).orElse(null);
            Negocio negocio = negocioRepository.findById(NEGOCIO_ID).orElse(null);
            List<Correo> lista = new ArrayList<>();
            if (cita == null || negocio == null) return lista;
            CorreosReserva.paraCliente(cita, negocio, email, clock).ifPresent(lista::add);
            CorreosReserva.paraComercio(cita, negocio, email).ifPresent(lista::add);
            return lista;
        });
        for (Correo correo : correos) enviarConReintentos(sender, correo, evento.citaId());
    }

    private void enviarConReintentos(JavaMailSender sender, Correo correo, long citaId) {
        Duration pausa = email.pausaReintento();
        for (int intento = 1; intento <= email.intentos(); intento++) {
            try {
                sender.send(mensaje(sender, correo));
                return;
            } catch (MailException | MessagingException e) {
                if (intento == email.intentos()) {
                    log.error("No se ha podido mandar el email \"{}\" de la cita {} a {} tras {} intentos",
                            correo.asunto(), citaId, correo.para(), intento, e);
                    return;
                }
                log.warn("Fallo al mandar el email de la cita {} (intento {}): {}", citaId, intento, e.getMessage());
                if (!esperar(pausa)) return;
                pausa = pausa.multipliedBy(2);
            }
        }
    }

    private MimeMessage mensaje(JavaMailSender sender, Correo correo) throws MessagingException {
        MimeMessage mensaje = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, correo.ics() != null, StandardCharsets.UTF_8.name());
        helper.setFrom(email.remitente());
        helper.setTo(correo.para());
        if (correo.responderA() != null) helper.setReplyTo(correo.responderA());
        helper.setSubject(correo.asunto());
        helper.setText(correo.texto());
        if (correo.ics() != null) {
            helper.addAttachment("cita.ics", new ByteArrayResource(correo.ics().getBytes(StandardCharsets.UTF_8)),
                    "text/calendar; charset=UTF-8; method=PUBLISH");
        }
        return mensaje;
    }

    private static boolean esperar(Duration pausa) {
        try {
            Thread.sleep(pausa);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
