package com.pdev.AnkaEduCertificadoWeb.exception;

import com.pdev.AnkaEduCertificadoWeb.model.response.ResponseGenerico;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static com.pdev.AnkaEduCertificadoWeb.util.Constantes.*;

@ControllerAdvice
public class FileUploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseGenerico> handleMaxSizeException(MaxUploadSizeExceededException ext){
        System.out.println(ext.getMaxUploadSize());
        ResponseGenerico responseGenerico = new ResponseGenerico();
        responseGenerico.setCodError(COD_ERRO);
        responseGenerico.setMensaje("Archivo demasiado grande!");
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseGenerico);
    }

}