package com.gestionproyectoscolaborativos.backend.services.mail.impl;

import com.gestionproyectoscolaborativos.backend.services.mail.model.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServicesImpl implements IEmailServices{

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public EmailServicesImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendMail(EmailDto emailDto) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailDto.getTo()); // para
            helper.setSubject(emailDto.getSubject()); // asunto
            Context context = new Context();
            context.setVariable("message", emailDto.getMessage());
            String contentHtml = templateEngine.process("email", context);

            helper.setText(contentHtml, true);
            javaMailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Habido un error = " + e.getMessage());
        }
    }
}
