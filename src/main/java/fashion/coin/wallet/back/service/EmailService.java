package fashion.coin.wallet.back.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;


    public void sendMail(String recipient, String subject, String message) {

        logger.info("Try send email to: " + recipient);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipient);
            messageHelper.setSubject(subject);
            messageHelper.setText(message, true);
        };

        logger.info("Mime prepare");
        try {
            JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) mailSender;
            logger.error("Email disable! EmailService.java line:37");
//            mailSender.send(messagePreparator);
            logger.info("Mail to " + recipient + " sended");
        } catch (MailException e) {
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error("Email didn't send");
            e.printStackTrace();
        }
    }


}

