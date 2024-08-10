package com.pfe.elearning.email;

import com.pfe.elearning.authentification.service.AuthService;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import com.pfe.elearning.validator.ObjectsValidator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final AuthService authService;
    @Value("${spring.mail.username}")
    private String senderEmail;
    private final String adminRoleName = "ROLE_ADMIN";
    private final String instructorRoleName = "ROLE_INSTRUCTOR";
    // private final ObjectsValidator<EmailService> validator;

    public void sendEmailToAdmins(String senderName, String senderEmail, String subject, String body) {
        List<User> admins = userRepository.findByRole(adminRoleName);
        for (User admin : admins) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(senderEmail);
                message.setTo(admin.getEmail());
                message.setSubject(subject);
                message.setText(body);
                message.setFrom(senderName + " <" + senderEmail + ">");
                javaMailSender.send(message);
                System.out.println("Email sent successfully to admin: " + admin.getEmail());
            } catch (Exception e) {
                System.out.println("Error sending email to admin: " + e.getMessage());
            }
        }
    }
   /* public void sendEmail(String recipient, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(message);
            System.out.println("Email sent successfully to: " + recipient);
        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
        }}
/*

    */
   public void sendEmail(String to, String subject, String body, String from) {
       try {
           // Create MimeMessage instance
           MimeMessage message = javaMailSender.createMimeMessage();

           // Create MimeMessageHelper instance with UTF-8 encoding
           MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

           // Set the sender's email address
           helper.setFrom(new InternetAddress(from));

           // Set the recipient's email address
           helper.setTo(to);

           // Set the email subject
           helper.setSubject(subject);

           // Set the email body as HTML content
           helper.setText(body, true);

           // Send the email message
           javaMailSender.send(message);
       } catch (MessagingException e) {
           e.printStackTrace();
       }
   }

    public void sendEmailToCandidate(String candidateEmail, String instructorEmail, String instructorName, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            helper.setFrom(new InternetAddress(instructorEmail, instructorName));
            helper.setTo(candidateEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true indicates HTML
            javaMailSender.send(message);
            System.out.println("Email sent successfully to candidate: " + candidateEmail);
        } catch (Exception e) {
            System.out.println("Error sending email to candidate: " + e.getMessage());
        }
    }

    public void sendEmailToCreatedUser(String recipientEmail, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            helper.setFrom(new InternetAddress(senderEmail, "EduCare")); // Update sender name here
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(message);
            System.out.println("Email sent successfully to new user: " + recipientEmail);
        } catch (Exception e) {
            System.out.println("Error sending email to new user: " + e.getMessage());
        }
    }
}

