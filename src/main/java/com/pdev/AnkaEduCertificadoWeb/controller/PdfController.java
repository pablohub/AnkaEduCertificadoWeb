package com.pdev.AnkaEduCertificadoWeb.controller;

import com.pdev.AnkaEduCertificadoWeb.model.Estudiante;
import com.pdev.AnkaEduCertificadoWeb.model.response.ResponseGenerico;
import com.pdev.AnkaEduCertificadoWeb.service.IEstudianteService;
import com.pdev.AnkaEduCertificadoWeb.serviceImpl.IPdfServiceImpl;
import com.pdev.AnkaEduCertificadoWeb.util.QRGenerator;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class PdfController {

    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);

    @Autowired
    private IEstudianteService estudianteService;

    @Autowired
    private IPdfServiceImpl pdfService;

    @GetMapping("/certificadoAnka/{id}")
    @ResponseBody
    public void certificadoAnka(@PathVariable long id, HttpServletResponse response) throws IOException {
        Estudiante estudiante = estudianteService.obtenerEstudiantePorId(id);
        ByteArrayOutputStream outputStream = pdfService.generatePdfAnka(estudiante);
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
        ByteArrayOutputStream outputStream = pdfService.generatePdfCIP(estudiante);
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
        if(pathEstudiante.equalsIgnoreCase("estudianteAnka")){
            for(Estudiante estudiante : estudiantes) {
                ByteArrayOutputStream outputStream = pdfService.generatePdfAnka(estudiante);
                pdfs.add(outputStream.toByteArray());
            }
        }
        if(pathEstudiante.equalsIgnoreCase("estudianteCIP")){
            for(Estudiante estudiante : estudiantes) {
                ByteArrayOutputStream outputStream = pdfService.generatePdfCIP(estudiante);
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
    private String generarNombrePdf(String codigo, String nombres){
        try{
            return codigo.substring(codigo.length() - 2) + "_" + nombres.substring(0, nombres.indexOf(" "));
        }catch (Exception e){
            return "FILE";
        }

    }
}


