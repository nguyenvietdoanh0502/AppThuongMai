package org.example.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailService {
    private static final String MY_EMAIL = "hientbtn2k6@gmail.com";
    private static final String APP_PASSWORD = "lxjhdqvtizgjkwnc";

    public static void sendOTP(String recipientEmail, String otpCode) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MY_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MY_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Mã xác nhận khôi phục mật khẩu");
            message.setText("Chào bạn,\n\nMã OTP của bạn là: " + otpCode + "\n\nVui lòng dùng mã này để đổi mật khẩu mới.");

            Transport.send(message);
            System.out.println(">>> Đã gửi OTP thành công!");
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Gửi mail thất bại!");
        }
    }
}