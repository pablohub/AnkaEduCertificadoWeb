package com.pdev.AnkaEduCertificadoWeb.util;

import org.springframework.web.multipart.MultipartFile;

public class PdfHelper {

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

}