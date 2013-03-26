package net.ontrack.backend;

import net.ontrack.core.RunProfile;
import net.ontrack.core.model.Message;
import net.ontrack.service.AdminService;
import net.ontrack.service.model.MailConfiguration;
import net.ontrack.service.model.MessageChannel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
@Profile({RunProfile.DEV, RunProfile.PROD})
public class MailPost extends AbstractMessagePost {

    private final Logger logger = LoggerFactory.getLogger(MailPost.class);

    private final AdminService adminService;

    @Autowired
    public MailPost(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public boolean supports(MessageChannel channel) {
        return (channel == MessageChannel.EMAIL);
    }

    @Override
    public void post(final Message message, final String destination) {

        MailConfiguration configuration = adminService.getMailConfiguration();

        // Mail sender
        JavaMailSenderImpl mailSender = getMailSender(configuration);

        // Reply to address
        final String replyToAddress = configuration.getReplyToAddress();
        logger.debug("[mail] Sending message from: {}", replyToAddress);

        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                prepareMessage(mimeMessage, message, destination, replyToAddress);
            }
        };

        try {
            mailSender.send(preparator);
        } catch (MailException ex) {
            logger.error("[mail] Cannot send mail: {}", ExceptionUtils.getRootCauseMessage(ex));
        }
    }

    // FIXME Cache for the mail session (use AdminService)
    private JavaMailSenderImpl getMailSender(MailConfiguration configuration) {
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

    protected void prepareMessage(MimeMessage mimeMessage, Message message, String destination, String replyToAddress) throws MessagingException {
        mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(destination));
        mimeMessage.setFrom(new InternetAddress(replyToAddress));
        mimeMessage.setSubject(message.getTitle());
        mimeMessage.setText(message.getContent().getText());
    }

}
