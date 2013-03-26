package net.ontrack.backend;

import net.ontrack.core.RunProfile;
import net.ontrack.service.AdminService;
import net.ontrack.service.model.MailConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Profile({RunProfile.DEV, RunProfile.PROD})
public class DefaultMailService implements MailService {

    private final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final AdminService adminService;

    @Autowired
    public DefaultMailService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    @Cacheable(value = Caches.MAIL, key="'0'")
    public JavaMailSender getMailSender() {
        MailConfiguration configuration = adminService.getMailConfiguration();
        logger.debug("[mail] Creating mail sender");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties p = new Properties();
        p.put("mail.smtp.host", configuration.getHost());
        p.put("mail.smtp.auth", String.valueOf(configuration.isAuthentication()));
        p.put("mail.smtp.starttls.required", String.valueOf(configuration.isStartTls()));
        p.put("mail.user", configuration.getUser());
        p.put("password", configuration.getPassword());
        mailSender.setJavaMailProperties(p);
        return mailSender;
    }

}
