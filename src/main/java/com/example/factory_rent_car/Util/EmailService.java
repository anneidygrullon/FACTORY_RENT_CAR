package com.example.factory_rent_car.Util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.InputStream;
import java.util.Properties;

public class EmailService {

    private static Properties config;
    private static boolean configurado = false;

    static {
        cargarConfiguracion();
    }

    private static void cargarConfiguracion() {
        config = new Properties();
        try (InputStream in = EmailService.class.getResourceAsStream("/com/example/factory_rent_car/email.properties")) {
            if (in == null) {
                System.err.println("[EmailService] No se encontr\u00F3 email.properties");
                return;
            }
            config.load(in);
            configurado = true;
        } catch (Exception e) {
            System.err.println("[EmailService] Error al cargar configuraci\u00F3n: " + e.getMessage());
        }
    }

    private static Session crearSesion() {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.getProperty("mail.smtp.host"));
        props.put("mail.smtp.port", config.getProperty("mail.smtp.port"));
        props.put("mail.smtp.auth", config.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", config.getProperty("mail.smtp.host"));

        String username = config.getProperty("mail.username");
        String password = config.getProperty("mail.password");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public static boolean enviar(String destinatario, String asunto, String cuerpo) {
        if (!configurado) {
            MensajeFactory.error("Correo no configurado.\nRevisa email.properties.");
            return false;
        }
        try {
            Session session = crearSesion();
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(config.getProperty("mail.from"), config.getProperty("mail.from.name")));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            msg.setSubject(asunto);
            msg.setText(cuerpo);
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            MensajeFactory.error("Error al enviar correo:\n" + e.getMessage());
            return false;
        }
    }

    public static void enviarAsync(String destinatario, String asunto, String cuerpo, Runnable onSuccess, Runnable onError) {
        AsyncTask.ejecutar(
                () -> enviar(destinatario, asunto, cuerpo),
                onSuccess,
                onError
        );
    }
}
