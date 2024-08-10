package com.pfe.elearning.contact;

import com.pfe.elearning.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "contacts")
public class Contact extends BaseEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "subject", length = 100)
    private String subject;
    @Column(name = "message", length = 1000)
    private String message;


}