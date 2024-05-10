package com.pdev.AnkaEduCertificadoWeb.controller;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.pdev.AnkaEduCertificadoWeb.model.Estudiante;
import com.pdev.AnkaEduCertificadoWeb.service.IEstudianteService;
import com.pdev.AnkaEduCertificadoWeb.util.QRGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class PdfController {

    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);

    @Autowired
    private IEstudianteService estudianteService;

    private final ResourceLoader resourceLoader;

    public PdfController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/certificadoAnka/{id}")
    @ResponseBody
    public void certificadoAnka(@PathVariable long id, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Cargar el archivo PDF original

        Estudiante estudiante = estudianteService.obtenerEstudiantePorId(id);
        Resource resource = resourceLoader.getResource("classpath:static/files/TemplateCertificadoAnka.pdf");
        try{

            //ClassPathResource resourceHelvetica97 = new ClassPathResource("fonts/Helvetica_97.ttf");
            Resource resourceHelvetica97 = resourceLoader.getResource("classpath:static/fonts/Helvetica_97.ttf");
            //ClassPathResource resourceHelvetica47 = new ClassPathResource("fonts/Helvetica_47.ttf");
            //Resource resourceHelvetica47 = resourceLoader.getResource("classpath:fonts/Helvetica_47.ttf");
            //String fontHelvetica97 = resourceHelvetica97.getFile().getPath();
            //InputStream fontHelvetica97 = resourceHelvetica97.getInputStream();
            //String fontHelvetica47 = resourceHelvetica47.getFile().getPath();
            //logger.info("fontHelvetica97: " + fontHelvetica97);

            PdfReader pdfReader = new PdfReader(resource.getInputStream());
            PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
            String codigoEncriptado = estudiante.getCodigoEncriptado();
            String urlQrCode = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString() + "/autenticacionCertificado/" + codigoEncriptado;
            logger.info("urlQrCode: " + urlQrCode);
            //pdfStamper.setEncryption(codigo.getBytes(), codigo.getBytes(), PdfWriter.ALLOW_COPY, PdfWriter.STANDARD_ENCRYPTION_40);

            //Image to be added in existing pdf file.
            QRGenerator qrGenerator = new QRGenerator();
            Image image = Image.getInstance(qrGenerator.imageToBytes(qrGenerator.generarCodigoQR(urlQrCode)));
            image.scaleAbsolute(96, 96); //Scale image's width and height
            //image.setAbsolutePosition(711.75f, 45.75f); //Set position for image in PDF
            image.setAbsolutePosition(712.5f, 46.5f); //Set position for image in PDF

            String fontPath = "src/main/resources/fonts/YourCustomFont.ttf";

            // Crear un objeto PdfFont a partir de la fuente personalizada

            // loop on all the PDF pages
            // i is the pdfPageNumber
            System.out.println("pdfReader.getNumberOfPages(): " + pdfReader.getNumberOfPages());
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                //getOverContent() allows you to write pdfContentByte on TOP of existing pdf pdfContentByte.
                //getUnderContent() allows you to write pdfContentByte on BELOW of existing pdf pdfContentByte.
                float pageWidth = pdfStamper.getReader().getPageSize(i).getWidth();
                logger.info("pageWidth: " + pageWidth);
                float pageHeight = pdfStamper.getReader().getPageSize(i).getHeight();
                logger.info("pageHeight: " + pageHeight);
                String texto = estudiante.getNombres();

                //BaseFont baseFont = BaseFont.createFont(fontHelvetica97, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                //BaseFont baseFont = BaseFont.createFont("resources/static/fonts/Helvetica_97.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                BaseFont baseFont = BaseFont.createFont
                        (BaseFont.TIMES_BOLD, //Font name
                                BaseFont.CP1257, //Font encoding
                                BaseFont.EMBEDDED //Font embedded
                        );
                float fontSize = 32.85f;

                float textWidth = baseFont.getWidthPoint(texto, fontSize);
                float centerX = (pageHeight - textWidth) / 2;//Se utiliza pagHeight porque lo calcula en orientación horizontal el largo es como si fuera la altura

                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(baseFont, fontSize); // set font and size
                pdfContentByte.setTextMatrix(centerX, 245.75f); // set x and y co-ordinates
                pdfContentByte.setRGBColorFill(34, 34, 34);
                //pdfContentByte.setRGBColorFill(255, 0, 0);
                pdfContentByte.showText(texto);
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(baseFont
                        , 12.5f); // set font and size
                pdfContentByte.setTextMatrix(742, 27.5f); // set x and y co-ordinates
                pdfContentByte.setRGBColorFill(92, 92, 92);
                //pdfContentByte.setRGBColorFill(255, 0, 0);
                //0, 800 will write text on TOP LEFT of pdf page
                //0, 0 will write text on BOTTOM LEFT of pdf page
                pdfContentByte.showText(estudiante.getCodigo()); // add the text

                pdfContentByte.endText();

                pdfContentByte.addImage(image);

            }

            pdfStamper.close();
            pdfReader.close();
            //System.out.println("Modified PDF created in >> "+ outputFilePath);
            // Crear la respuesta HTTP con el PDF modificado
            /*HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);*/
            String nombrePdf = generarNombrePdf(estudiante.getCodigo(), estudiante.getNombres());
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
        }catch (Exception ex){
            ex.printStackTrace();
        }

        /*return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());*/
    }

    @GetMapping("/certificadoCIP/{id}")
    @ResponseBody
    public void certificadoCIP(@PathVariable long id, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Cargar el archivo PDF original

        Estudiante estudiante = estudianteService.obtenerEstudiantePorId(id);
        Resource resource = resourceLoader.getResource("classpath:static/files/TemplateCertificadoCIP.pdf");
        try{

            //ClassPathResource resourceHelvetica97 = new ClassPathResource("fonts/Helvetica_97.ttf");
            //Resource resourceHelvetica97 = resourceLoader.getResource("classpath:fonts/Helvetica_97.ttf");
            //ClassPathResource resourceHelvetica47 = new ClassPathResource("fonts/Helvetica_47.ttf");
            //Resource resourceHelvetica47 = resourceLoader.getResource("classpath:fonts/Helvetica_47.ttf");
            //String fontHelvetica97 = resourceHelvetica97.getFile().getPath();
            //String fontHelvetica47 = resourceHelvetica47.getFile().getPath();

            PdfReader pdfReader = new PdfReader(resource.getInputStream());
            PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);

            String codigoEncriptado = estudiante.getCodigoEncriptado();
            String urlQrCode = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString() + "/autenticacionCertificado/" + codigoEncriptado;
            logger.info("urlQrCode: " + urlQrCode);
            //pdfStamper.setEncryption(codigo.getBytes(), codigo.getBytes(), PdfWriter.ALLOW_COPY, PdfWriter.STANDARD_ENCRYPTION_40);

            //Image to be added in existing pdf file.
            QRGenerator qrGenerator = new QRGenerator();
            Image image = Image.getInstance(qrGenerator.imageToBytes(qrGenerator.generarCodigoQR(urlQrCode)));
            //image.scaleAbsolute(100, 100); //Scale image's width and height
            //image.setAbsolutePosition(710, 44); //Set position for image in PDF
            image.scaleAbsolute(96, 96); //Scale image's width and height
            image.setAbsolutePosition(712.5f, 46.5f); //Set position for image in PDF

            // loop on all the PDF pages
            // i is the pdfPageNumber
            System.out.println("pdfReader.getNumberOfPages(): " + pdfReader.getNumberOfPages());
            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                //getOverContent() allows you to write pdfContentByte on TOP of existing pdf pdfContentByte.
                //getUnderContent() allows you to write pdfContentByte on BELOW of existing pdf pdfContentByte.
                float pageWidth = pdfStamper.getReader().getPageSize(i).getWidth();
                logger.info("pageWidth: " + pageWidth);
                float pageHeight = pdfStamper.getReader().getPageSize(i).getHeight();
                logger.info("pageHeight: " + pageHeight);
                String texto = estudiante.getNombres();

                //BaseFont baseFont = BaseFont.createFont(fontHelvetica97, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

                BaseFont baseFont = BaseFont.createFont
                        (BaseFont.TIMES_BOLD, //Font name
                                BaseFont.CP1257, //Font encoding
                                BaseFont.EMBEDDED //Font embedded
                        );
                float fontSize = 32.85f;

                float textWidth = baseFont.getWidthPoint(texto, fontSize);
                float centerX = (pageHeight - textWidth) / 2;//Se utiliza pagHeight porque lo calcula en orientación horizontal el largo es como si fuera la altura

                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(baseFont, fontSize); // set font and size
                pdfContentByte.setTextMatrix(centerX, 313); // set x and y co-ordinates
                pdfContentByte.setRGBColorFill(34, 34, 34);
                //pdfContentByte.setRGBColorFill(255, 0, 0);
                pdfContentByte.showText(texto);
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(baseFont
                        , 12.5f); // set font and size
                pdfContentByte.setTextMatrix(640, 27.75f); // set x and y co-ordinates
                pdfContentByte.setRGBColorFill(34, 34, 34);
                //pdfContentByte.setRGBColorFill(255, 0, 0);
                //0, 800 will write text on TOP LEFT of pdf page
                //0, 0 will write text on BOTTOM LEFT of pdf page
                pdfContentByte.showText(estudiante.getCodigo()); // add the text

                pdfContentByte.endText();

                pdfContentByte.addImage(image);

            }

            pdfStamper.close();
            pdfReader.close();
            //System.out.println("Modified PDF created in >> "+ outputFilePath);
            // Crear la respuesta HTTP con el PDF modificado
            /*HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);*/
            String nombrePdf = generarNombrePdf(estudiante.getCodigo(), estudiante.getNombres());
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
        }catch (Exception ex){
            ex.printStackTrace();
        }

        /*return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());*/
    }

    private String generarNombrePdf(String codigo, String nombres){
        try{
            return codigo.substring(codigo.length() - 2) + nombres.substring(0, nombres.indexOf(" "));
        }catch (Exception e){
            return "FILE";
        }

    }
}