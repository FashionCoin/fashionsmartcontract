package fashion.coin.wallet.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;



    public void sendMail(String recipient, String subject, String message) {

        System.out.println("> Send email to: " + recipient);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(recipient);
            messageHelper.setSubject(subject);
            messageHelper.setText(message,true);
        };
        try {

            JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) mailSender;

            mailSender.send(messagePreparator);
        } catch (MailException e) {
            System.out.println("! FAIL ! Email didn't send");
            e.printStackTrace();
        }
    }


}

