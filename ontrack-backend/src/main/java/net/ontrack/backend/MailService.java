package net.ontrack.backend;

import org.springframework.mail.javamail.JavaMailSender;

public interface MailService {

    JavaMailSender getMailSender();

}
