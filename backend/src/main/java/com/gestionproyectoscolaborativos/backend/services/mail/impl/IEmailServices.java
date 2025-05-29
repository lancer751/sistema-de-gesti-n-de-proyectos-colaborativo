package com.gestionproyectoscolaborativos.backend.services.mail.impl;

import com.gestionproyectoscolaborativos.backend.services.mail.model.EmailDto;
import jakarta.mail.MessagingException;

public interface IEmailServices {
    void sendMail (EmailDto emailDto) throws MessagingException;
}
