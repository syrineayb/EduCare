// ContactController.java
package com.pfe.elearning.contact;

import com.pfe.elearning.contact.dto.ContactRequest;
import com.pfe.elearning.email.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmailToAdmins(@Valid @RequestBody ContactRequest request) {
        String subject = "New Contact Form Submission: " + request.getSubject();
        String body = "Dear Admin,\n\n"
                + "A new contact form has been submitted with the following details:\n\n"
                + "Name: " + request.getName() + "\n"
                + "Email: " + request.getEmail() + "\n"
                + "Message:\n" + request.getMessage() + "\n\n"
                + "Please review and respond accordingly.\n\n"
                + "Best Regards,\n"
                + "EduCare Platform";

        emailService.sendEmailToAdmins(request.getName(), request.getEmail(), subject, body);

        return ResponseEntity.ok("Contact form submitted successfully!");
    }

}