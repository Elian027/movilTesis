package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class enviarCorreo extends AsyncTask<String, Void, Boolean> {
    private Context context;

    public enviarCorreo(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            // Configuración del servidor SMTP
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            // Credenciales del remitente
            final String correoR = cargarConfi.getCorreoR(context);
            final String passwordR = cargarConfi.getPasswordR(context);

            // Verificar que las credenciales no sean nulas
            if (correoR == null || passwordR == null) {
                return false;
            }

            // Crear sesión de correo
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(correoR, passwordR);
                        }
                    });

            // Crear mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(correoR));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(params[0]));
            message.setSubject(params[1]);
            message.setText(params[2]);

            // Enviar mensaje
            Transport.send(message);

            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            // Éxito al enviar el correo
            Toast.makeText(context, "Correo enviado exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            // Error al enviar el correo
            Toast.makeText(context, "Error al enviar el correo", Toast.LENGTH_SHORT).show();
        }
    }
}
