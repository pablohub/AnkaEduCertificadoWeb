package com.pdev.AnkaEduCertificadoWeb.model.response;

import lombok.Data;

@Data
public class ResponseGenerico<T> {

    private int codError;
    private String mensaje;
    private T data;

}
