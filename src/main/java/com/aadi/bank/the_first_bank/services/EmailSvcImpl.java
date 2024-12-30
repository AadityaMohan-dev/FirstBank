package com.aadi.bank.the_first_bank.services;

import com.aadi.bank.the_first_bank.dto.EmailDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSvcImpl implements EmailSvc{
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    EmailDetail emailDetail;
    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmailAlert(EmailDetail emailDetail) {
        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(senderEmail);
            simpleMailMessage.setTo(emailDetail.getRecipient());
            simpleMailMessage.setText(emailDetail.getMessageBody());
            simpleMailMessage.setSubject(emailDetail.getSubject());
            javaMailSender.send(simpleMailMessage);
            System.out.println("mail sent successfully to " + emailDetail.getRecipient());
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }
}
