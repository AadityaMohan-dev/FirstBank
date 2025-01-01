package com.aadi.bank.the_first_bank.services;

import com.aadi.bank.the_first_bank.dto.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailSvcImpl implements EmailSvc{
    private static final Logger log = LoggerFactory.getLogger(EmailSvcImpl.class);
    @Autowired
    JavaMailSender javaMailSender;

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

    @Override
    public void sendEmailWithAttachment(EmailDetail emailDetail) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try{
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setText(emailDetail.getMessageBody());
            mimeMessageHelper.setSubject(emailDetail.getSubject());

            FileSystemResource file = new FileSystemResource(new File(emailDetail.getAttachment()));
            mimeMessageHelper.addAttachment(file.getFilename(), file);
            javaMailSender.send(mimeMessage);

            log.info(file.getFilename() + "has been sent to user " + emailDetail.getRecipient());

        }
        catch (MessagingException e){
            throw new RuntimeException(e);
        }
    }
}
