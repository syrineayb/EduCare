package com.pfe.elearning.contact.dto;

import com.pfe.elearning.contact.Contact;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class ContactMapper {
    public Contact ToContactEntity(ContactRequest request) {
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setEmail(request.getEmail());
        contact.setSubject(request.getSubject());
        contact.setMessage(request.getMessage());
        return contact;
    }


    public ContactResponse toContactResponse(Contact contact) {
        ContactResponse response = new ContactResponse();
        // Map fields from Contact entity to ContactResponse DTO
        response.setName(contact.getName());
        response.setEmail(contact.getEmail());
        response.setSubject(contact.getSubject());
        response.setMessage(contact.getMessage());
        return response;
    }
}