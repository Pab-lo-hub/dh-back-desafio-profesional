package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.dto.ReservaDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía un correo con los detalles de la reserva al usuario.
     * @param reservaDTO Datos de la reserva
     * @param userEmail Correo del usuario
     * @throws MessagingException si hay un error al enviar el correo
     */
    public void sendReservationEmail(ReservaDTO reservaDTO, String userEmail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(userEmail);
        helper.setSubject("Confirmación de Reserva #" + reservaDTO.getId());
        helper.setText(buildEmailTemplate(reservaDTO), true); // true indica HTML

        mailSender.send(message);
    }

    /**
     * Construye la plantilla HTML para el correo.
     * @param reservaDTO Datos de la reserva
     * @return Plantilla HTML como String
     */
    private String buildEmailTemplate(ReservaDTO reservaDTO) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }
                .header { background-color: #f8f8f8; padding: 10px; text-align: center; border-bottom: 1px solid #ddd; }
                .content { padding: 20px; }
                .footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h2>Confirmación de Reserva</h2>
                </div>
                <div class="content">
                    <p>Estimado/a <strong>%s</strong>,</p>
                    <p>Hemos registrado su reserva con éxito. A continuación, los detalles:</p>
                    <ul>
                        <li><strong>ID de Reserva:</strong> %d</li>
                        <li><strong>Producto:</strong> %s</li>
                        <li><strong>Fecha de Inicio:</strong> %s</li>
                        <li><strong>Fecha de Fin:</strong> %s</li>
                        <li><strong>Estado:</strong> %s</li>
                    </ul>
                    <p>Gracias por elegir nuestro servicio.</p>
                </div>
                <div class="footer">
                    <p>© 2025 Desafío Profesional. Todos los derechos reservados.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(
                reservaDTO.getUsuarioNombre(),
                reservaDTO.getId(),
                reservaDTO.getProductoNombre(),
                reservaDTO.getStartDate().toString(),
                reservaDTO.getEndDate().toString(),
                reservaDTO.getEstado()
        );
    }
}
