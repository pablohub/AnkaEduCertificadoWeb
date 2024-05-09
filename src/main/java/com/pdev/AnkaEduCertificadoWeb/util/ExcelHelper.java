package com.pdev.AnkaEduCertificadoWeb.util;

import org.springframework.web.multipart.MultipartFile;

public class ExcelHelper {

    public static String[] TYPES = {
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",//para peticiones por postman u otra herramienta
            "application/octet-stream",//para peticiones con CURL desde cmd
    };

    public static boolean hasExcelFormat(MultipartFile file){
        for(String type : TYPES){
            if(type.equals(file.getContentType())){
                return true;
            }
        }
        return false;
    }

}