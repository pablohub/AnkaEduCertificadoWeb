package com.pdev.AnkaEduCertificadoWeb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

public class PdfHelper {

    private static final Logger logger = LoggerFactory.getLogger(PdfHelper.class);
    private static final String UPLOAD_DIR = "src/main/resources/static/files/";
    public static String[] TYPES = {
            "application/pdf"
    };

    public static boolean hasPdfFormat(MultipartFile file){
        for(String type : TYPES){
            if(type.equals(file.getContentType())){
                return true;
            }
        }
        return false;
    }

    public static String Base64EncodePdf(MultipartFile file){
        try {
            //byte [] bytes = Files.readAllBytes(file.toPath());
            byte [] bytes = file.getBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            logger.error("Error Base64EncodePdf: " + e.getMessage());
            return null;
        }
    }

    public static void Base64DecodePdf(String b64, String nombrePdf){
        File file = new File(UPLOAD_DIR + nombrePdf);

        try (FileOutputStream fos = new FileOutputStream(file); ) {
            byte[] decoder = Base64.getDecoder().decode(b64);
            fos.write(decoder);
        } catch (Exception e) {
            logger.error("Error Base64DecodePdf: " + e.getMessage());
        }
    }

}