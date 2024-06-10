package com.pdev.AnkaEduCertificadoWeb.service;

import com.pdev.AnkaEduCertificadoWeb.model.Estudiante;

import java.io.ByteArrayOutputStream;

public interface IPdfService {

    ByteArrayOutputStream generatePdfAnka(Estudiante estudiante, String nombreTemplatePdf);
    ByteArrayOutputStream generatePdfCIP(Estudiante estudiante, String nombreTemplatePdf);

}
