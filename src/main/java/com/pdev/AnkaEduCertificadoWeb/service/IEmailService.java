package com.pdev.AnkaEduCertificadoWeb.service;

import java.io.ByteArrayOutputStream;

public interface IEmailService {

    void sendEmail(String to, String subject, String text);

    boolean sendEmailWithAttachment(String to, String subject, String text, String pathToAttachment);

    boolean sendEmailWithAttachment(String to, String subject, String text, ByteArrayOutputStream byteArrayOutputStream, String attachmentFilename);
}
