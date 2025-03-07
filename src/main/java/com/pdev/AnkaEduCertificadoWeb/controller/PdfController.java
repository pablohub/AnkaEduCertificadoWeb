package com.pdev.AnkaEduCertificadoWeb.controller;

import com.pdev.AnkaEduCertificadoWeb.model.Estudiante;
import com.pdev.AnkaEduCertificadoWeb.model.Grupo;
import com.pdev.AnkaEduCertificadoWeb.model.response.ResponseGenerico;
import com.pdev.AnkaEduCertificadoWeb.service.IEmailService;
import com.pdev.AnkaEduCertificadoWeb.service.IEstudianteService;
import com.pdev.AnkaEduCertificadoWeb.service.IGrupoService;
import com.pdev.AnkaEduCertificadoWeb.serviceImpl.EmailServiceImpl;
import com.pdev.AnkaEduCertificadoWeb.serviceImpl.IPdfServiceImpl;
import com.pdev.AnkaEduCertificadoWeb.util.EmailHelper;
import com.pdev.AnkaEduCertificadoWeb.util.PdfHelper;
import com.pdev.AnkaEduCertificadoWeb.util.QRGenerator;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import com.pdev.AnkaEduCertificadoWeb.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class PdfController {

    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);

    @Autowired
    private IEstudianteService estudianteService;

    @Autowired
    private IGrupoService grupoService;

    @Autowired
    private IPdfServiceImpl pdfService;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private EmailHelper emailHelper;

    @GetMapping("/certificadoAnka/{id}")
    @ResponseBody
    public void certificadoAnka(@PathVariable long id, HttpServletResponse response) throws IOException {
        Estudiante estudiante = estudianteService.obtenerEstudiantePorId(id);
        String nombrePdfTemplate = null;
        String[] args = estudiante.getCodigoCertificado().split("-");
        if(args.length == 2) {
            Grupo grupo = grupoService.obtenerGrupoPorId(Long.parseLong(args[1]));
            nombrePdfTemplate = grupo.getNombreTemplatePdf();
            if(!Util.isNullOrEmpty(grupo.getPdfBase64())){
                PdfHelper.Base64DecodePdf(grupo.getPdfBase64(), nombrePdfTemplate);
            }
        }

        ByteArrayOutputStream outputStream = pdfService.generatePdfAnka(estudiante, nombrePdfTemplate);
        String nombrePdf = estudiante.getNombrePdf();
        //headers.setContentDispositionFormData("inline", nombrePdf + ".pdf");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + nombrePdf + ".pdf");
        response.setContentLength(outputStream.size());

        // Escribir el PDF en la respuesta
        ByteArrayInputStream bis = new ByteArrayInputStream(outputStream.toByteArray());
        int c;
        while ((c = bis.read()) != -1) {
            response.getOutputStream().write(c);
        }
        response.flushBuffer();
    }

    @GetMapping("/certificadoCIP/{id}")
    @ResponseBody
    public void certificadoCIP(@PathVariable long id, HttpServletResponse response) throws IOException {
        Estudiante estudiante = estudianteService.obtenerEstudiantePorId(id);
        String nombrePdfTemplate = null;
        String[] args = estudiante.getCodigoCertificado().split("-");
        if(args.length == 2) {
            Grupo grupo = grupoService.obtenerGrupoPorId(Long.parseLong(args[1]));
            nombrePdfTemplate = grupo.getNombreTemplatePdf();
            if(!Util.isNullOrEmpty(grupo.getPdfBase64())){
                PdfHelper.Base64DecodePdf(grupo.getPdfBase64(), nombrePdfTemplate);
            }
        }
        ByteArrayOutputStream outputStream = pdfService.generatePdfCIP(estudiante, nombrePdfTemplate);
        String nombrePdf = estudiante.getNombrePdf();
        //headers.setContentDispositionFormData("inline", nombrePdf + ".pdf");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + nombrePdf + ".pdf");
        response.setContentLength(outputStream.size());

        // Escribir el PDF en la respuesta
        ByteArrayInputStream bis = new ByteArrayInputStream(outputStream.toByteArray());
        int c;
        while ((c = bis.read()) != -1) {
            response.getOutputStream().write(c);
        }
        response.flushBuffer();
    }

    @PostMapping("/exportarPdfs/{pathEstudiante}")
    @ResponseBody
    public ResponseEntity<byte[]> exportarPdfs(@PathVariable String pathEstudiante, @RequestBody String ids) throws IOException{
        logger.info("exportarPdfs " + pathEstudiante + ": " + ids);
        List<Estudiante> estudiantes = estudianteService.listarEstudiantesPorIds(ids);
        List<byte[]> pdfs = new ArrayList<>();
        boolean pdfGenerado = false;
        if(pathEstudiante.equalsIgnoreCase("estudianteAnka")){
            for(Estudiante estudiante : estudiantes) {
                String nombrePdfTemplate = null;
                if(!pdfGenerado){
                    String[] args = estudiante.getCodigoCertificado().split("-");
                    if(args.length == 2) {
                        Grupo grupo = grupoService.obtenerGrupoPorId(Long.parseLong(args[1]));
                        nombrePdfTemplate = grupo.getNombreTemplatePdf();
                        if(!Util.isNullOrEmpty(grupo.getPdfBase64())){
                            PdfHelper.Base64DecodePdf(grupo.getPdfBase64(), nombrePdfTemplate);
                            pdfGenerado = true;
                        }
                    }
                }

                ByteArrayOutputStream outputStream = pdfService.generatePdfAnka(estudiante, nombrePdfTemplate);
                pdfs.add(outputStream.toByteArray());
            }
        }
        if(pathEstudiante.equalsIgnoreCase("estudianteCIP")){
            for(Estudiante estudiante : estudiantes) {
                String nombrePdfTemplate = null;
                if(!pdfGenerado){
                    String[] args = estudiante.getCodigoCertificado().split("-");
                    if(args.length == 2) {
                        Grupo grupo = grupoService.obtenerGrupoPorId(Long.parseLong(args[1]));
                        nombrePdfTemplate = grupo.getNombreTemplatePdf();
                        if(!Util.isNullOrEmpty(grupo.getPdfBase64())){
                            PdfHelper.Base64DecodePdf(grupo.getPdfBase64(), nombrePdfTemplate);
                            pdfGenerado = true;
                        }
                    }
                }
                ByteArrayOutputStream outputStream = pdfService.generatePdfCIP(estudiante, nombrePdfTemplate);
                pdfs.add(outputStream.toByteArray());
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (int i = 0; i < pdfs.size(); i++) {
                String nombrePdf = estudiantes.get(i).getNombrePdf();
                ZipEntry entry = new ZipEntry(nombrePdf + ".pdf");
                entry.setSize(pdfs.get(i).length);
                zos.putNextEntry(entry);
                zos.write(pdfs.get(i));
                zos.closeEntry();
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Certificados.zip");

        return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
    }

    @PostMapping("/enviarEmail/{pathEstudiante}")
    @ResponseBody
    public ResponseEntity enviarEmail(@PathVariable String pathEstudiante, @RequestBody String ids) throws IOException{
        logger.info("enviarEmail " + pathEstudiante + ": " + ids);
        ResponseGenerico responseGenerico = new ResponseGenerico();
        responseGenerico.setCodError(0);
        responseGenerico.setMensaje("Proceso enviado");
        List<Estudiante> estudiantes = estudianteService.listarEstudiantesPorIds(ids);
        boolean pdfGenerado = false;
        if(pathEstudiante.equalsIgnoreCase("estudianteAnka")){
            for(Estudiante estudiante : estudiantes) {
                String nombrePdfTemplate = null;
                if(!pdfGenerado){
                    String[] args = estudiante.getCodigoCertificado().split("-");
                    if(args.length == 2) {
                        Grupo grupo = grupoService.obtenerGrupoPorId(Long.parseLong(args[1]));
                        nombrePdfTemplate = grupo.getNombreTemplatePdf();
                        if(!Util.isNullOrEmpty(grupo.getPdfBase64())){
                            PdfHelper.Base64DecodePdf(grupo.getPdfBase64(), nombrePdfTemplate);
                            pdfGenerado = true;
                        }
                    }
                }

                ByteArrayOutputStream outputStream = pdfService.generatePdfAnka(estudiante, nombrePdfTemplate);
                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("nombreEstudiante", estudiante.getNombres());
                String body = emailHelper.generarHtmlFromThymeleafTemplate("/emailTemplates/anka", templateModel);
                boolean emailEnviado = emailService.sendEmailWithAttachment(
                        estudiante.getEmail(),
                        "ANKA - CERTIFICADO MODELADOR BIM",
                        body,
                        outputStream,
                        estudiante.getNombrePdf() + ".pdf"
                );
                estudiante.setEmailEnviado(emailEnviado);
                estudianteService.guardarEstudiante(estudiante);
            }
        }
        if(pathEstudiante.equalsIgnoreCase("estudianteCIP")){
            for(Estudiante estudiante : estudiantes) {
                String nombrePdfTemplate = null;
                if(!pdfGenerado){
                    String[] args = estudiante.getCodigoCertificado().split("-");
                    if(args.length == 2) {
                        Grupo grupo = grupoService.obtenerGrupoPorId(Long.parseLong(args[1]));
                        nombrePdfTemplate = grupo.getNombreTemplatePdf();
                        if(!Util.isNullOrEmpty(grupo.getPdfBase64())){
                            PdfHelper.Base64DecodePdf(grupo.getPdfBase64(), nombrePdfTemplate);
                            pdfGenerado = true;
                        }
                    }
                }
                ByteArrayOutputStream outputStream = pdfService.generatePdfCIP(estudiante, nombrePdfTemplate);
                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("nombreEstudiante", estudiante.getNombres());
                String body = emailHelper.generarHtmlFromThymeleafTemplate("/emailTemplates/cip", templateModel);
                boolean emailEnviado = emailService.sendEmailWithAttachment(
                        estudiante.getEmail(),
                        "CIP-CERTIFICADO",
                        body,
                        outputStream,
                        estudiante.getNombrePdf() + ".pdf"
                );
                estudiante.setEmailEnviado(emailEnviado);
                estudianteService.guardarEstudiante(estudiante);
            }
        }

        return new ResponseEntity(responseGenerico, HttpStatus.OK);
    }

    private String generarNombrePdf(String codigo, String nombres){
        try{
            return codigo.substring(codigo.length() - 2) + "_" + nombres.substring(0, nombres.indexOf(" "));
        }catch (Exception e){
            return "FILE";
        }

    }
}


