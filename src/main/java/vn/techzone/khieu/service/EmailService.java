package vn.techzone.khieu.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            this.javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendEmailFromTemplate(String to, String subject,
            String templateName,
            Map<String, Object> variables) {

        Context context = new Context();
        context.setVariables(variables);

        String html = templateEngine.process(templateName, context);

        sendEmail(to, subject, html, false, true);
    }

}
