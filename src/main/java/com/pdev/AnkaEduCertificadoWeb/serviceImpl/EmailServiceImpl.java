package com.pdev.AnkaEduCertificadoWeb.serviceImpl;

import com.pdev.AnkaEduCertificadoWeb.service.IEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Service
public class EmailServiceImpl implements IEmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("anka.education.info@gmail.com");

        mailSender.send(message);
    }

    @Override
    public boolean sendEmailWithAttachment(String to, String subject, String text, String pathToAttachment) {

        try{
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.setFrom("anka.education.info@gmail.com");

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
            return true;
        }catch (MessagingException ex){
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean sendEmailWithAttachment(String to, String subject, String text, ByteArrayOutputStream byteArrayOutputStream, String attachmentFilename) {
        try{
            logger.info("sendEmailWithAttachment to: " + to);
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.setFrom("anka.education.info@gmail.com");

            ByteArrayResource byteArrayResource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
            helper.addAttachment(attachmentFilename, byteArrayResource);

            mailSender.send(message);
            logger.info("Email enviado");
            return true;
        }catch (Exception e){
            //e.printStackTrace();
            logger.error("Error sendEmailWithAttachment: " + e.getMessage());
        }
        return false;
    }
}
