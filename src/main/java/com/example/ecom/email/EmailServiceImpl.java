package com.example.ecom.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.exception.BadSqlException;
import com.example.ecom.log.AppLogger;
import com.example.ecom.log.LoggerFactory;
import com.example.ecom.log.LoggerType;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    protected AppLogger APP_LOGGER = LoggerFactory.getLogger(LoggerType.APPLICATION);

    @Override
    public void sendSimpleMail(EmailDetail details) {
        try {

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            APP_LOGGER.error(e.getMessage());
            throw new BadSqlException(LanguageMessageKey.SERVER_ERROR);
        }
    }

}
