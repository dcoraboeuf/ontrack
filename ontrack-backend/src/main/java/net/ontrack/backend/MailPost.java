package net.ontrack.backend;

import net.ontrack.backend.db.StartupService;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
@Profile({RunProfile.DEV, RunProfile.PROD})
public class MailPost extends AbstractMessagePost implements ConfigurationCacheSubscriber<MailConfiguration>, StartupService {

	private final Logger logger = LoggerFactory.getLogger(MailPost.class);

    private final AdminService adminService;
	private final JavaMailSender mailSender;
    private final ConfigurationCache configurationCache;

	@Autowired
	public MailPost(AdminService adminService, JavaMailSender mailSender, ConfigurationCache configurationCache) {
        this.adminService = adminService;
        this.mailSender = mailSender;
        this.configurationCache = configurationCache;
	}

    @PostConstruct
    public void subscribeToConfigurationChanges() {
        configurationCache.subscribe(ConfigurationCacheKey.MAIL, this);
    }

    @Override
    public void onConfigurationChange(ConfigurationCacheKey key, MailConfiguration value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public boolean supports(MessageChannel channel) {
		return (channel == MessageChannel.EMAIL);
	}

	@Override
	public void post(final Message message, final String destination) {
		
		final String replyToAddress = configurationService.getConfigurationValue(ConfigurationKey.MAIL_REPLY_TO);
		logger.debug("[mail] Sending message from: {}", replyToAddress);

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				prepareMessage(mimeMessage, message, destination, replyToAddress);
			}
		};
		try {
			this.mailSender.send(preparator);
		} catch (MailException ex) {
			logger.error("[mail] Cannot send mail: {}", ExceptionUtils.getRootCauseMessage(ex));
		}
	}

	protected void prepareMessage(MimeMessage mimeMessage, Message message, String destination, String replyToAddress) throws MessagingException, AddressException {
		mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(destination));
		mimeMessage.setFrom(new InternetAddress(replyToAddress));
		mimeMessage.setSubject(message.getTitle());
		mimeMessage.setText(message.getContent().getText());
	}

}
