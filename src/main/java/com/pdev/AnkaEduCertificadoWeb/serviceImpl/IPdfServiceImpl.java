package com.pdev.AnkaEduCertificadoWeb.serviceImpl;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.pdev.AnkaEduCertificadoWeb.model.Estudiante;
import com.pdev.AnkaEduCertificadoWeb.service.IPdfService;
import com.pdev.AnkaEduCertificadoWeb.util.QRGenerator;
import com.pdev.AnkaEduCertificadoWeb.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class IPdfServiceImpl implements IPdfService {

    private static final Logger logger = LoggerFactory.getLogger(IPdfServiceImpl.class);
    private final ResourceLoader resourceLoader;
    public IPdfServiceImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private static final String UPLOAD_DIR = "src/main/resources/static/files/";
    //private static final String UPLOAD_DIR = "/appWeb/AnkaEduCertificadoWeb/templateCertificados/";

    @Override
    public ByteArrayOutputStream generatePdfAnka(Estudiante estudiante, String nombreTemplatePdf) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String template = Util.isNullOrEmpty(nombreTemplatePdf) ? "TemplateCertificadoAnka.pdf" : nombreTemplatePdf;
        File file = new File(UPLOAD_DIR + template);
        //Resource resource = resourceLoader.getResource("classpath:static/files/" + template);
        try{

            ClassPathResource resourceHelvetica97 = new ClassPathResource("fonts/Helvetica_97.ttf");
            ClassPathResource resourceHelvetica47 = new ClassPathResource("fonts/Helvetica_47.ttf");
            //String fontHelvetica97 = resourceHelvetica97.getFile().getAbsolutePath();
            InputStream inputStreamHelvetica97 = resourceHelvetica97.getInputStream();
            //String fontHelvetica47 = resourceHelvetica47.getFile().getAbsolutePath();
            InputStream inputStreamHelvetica47 = resourceHelvetica47.getInputStream();

            ByteArrayOutputStream baosfontHelvetica97 = new ByteArrayOutputStream();
            byte[] bufferfontHelvetica97 = new byte[1024];
            int bytesReadfontHelvetica97;
            while ((bytesReadfontHelvetica97 = inputStreamHelvetica97.read(bufferfontHelvetica97)) != -1) {
                baosfontHelvetica97.write(bufferfontHelvetica97, 0, bytesReadfontHelvetica97);
            }
            byte[] fontBytes = baosfontHelvetica97.toByteArray();
            BaseFont baseFontHelvetica97 = BaseFont.createFont("Helvetica_97.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, false, fontBytes, null);

            ByteArrayOutputStream baosfontHelvetica47 = new ByteArrayOutputStream();
            byte[] bufferfontHelvetica47 = new byte[1024];
            int bytesReadfontHelvetica47;
            while ((bytesReadfontHelvetica47 = inputStreamHelvetica47.read(bufferfontHelvetica47)) != -1) {
                baosfontHelvetica47.write(bufferfontHelvetica47, 0, bytesReadfontHelvetica47);
            }
            byte[] fontBytesfontHelvetica47 = baosfontHelvetica47.toByteArray();
            BaseFont baseFontHelvetica47 = BaseFont.createFont("Helvetica_47.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, false, fontBytesfontHelvetica47, null);

            //PdfReader pdfReader = new PdfReader(resource.getInputStream());
            FileInputStream fis = new FileInputStream(file);
            PdfReader pdfReader = new PdfReader(fis);
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
            image.setAbsolutePosition(712.25f, 46.75f); //Set position for image in PDF

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
                float fontSize = 30.85f;

                float textWidth = baseFontHelvetica97.getWidthPoint(texto, fontSize);
                float centerX = (pageHeight - textWidth) / 2;//Se utiliza pagHeight porque lo calcula en orientación horizontal el largo es como si fuera la altura

                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(baseFontHelvetica97, fontSize); // set font and size
                pdfContentByte.setTextMatrix(centerX, 245.75f); // set x and y co-ordinates
                pdfContentByte.setRGBColorFill(34, 34, 34);
                //pdfContentByte.setRGBColorFill(255, 0, 0);
                pdfContentByte.showText(texto);
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(baseFontHelvetica47, 12.5f);
                pdfContentByte.setTextMatrix(745.25f, 27.5f); // set x and y co-ordinates
                pdfContentByte.setRGBColorFill(92, 92, 92);
                //pdfContentByte.setRGBColorFill(255, 0, 0);
                pdfContentByte.showText(estudiante.getCodigo()); // add the text
                pdfContentByte.setLeading(0.75f);
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(baseFontHelvetica47, 12.5f);
                pdfContentByte.setTextMatrix(745.75f, 27.5f); // set x and y co-ordinates
                pdfContentByte.setRGBColorFill(92, 92, 92);
                //pdfContentByte.setRGBColorFill(255, 0, 0);
                pdfContentByte.showText(estudiante.getCodigo()); // add the text
                pdfContentByte.setLeading(0.75f);
                pdfContentByte.endText();

                pdfContentByte.addImage(image);

            }

            pdfStamper.close();
            pdfReader.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return outputStream;
    }

    @Override
    public ByteArrayOutputStream generatePdfCIP(Estudiante estudiante, String nombreTemplatePdf) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String template = Util.isNullOrEmpty(nombreTemplatePdf) ? "TemplateCertificadoCIP.pdf" : nombreTemplatePdf;
        File file = new File(UPLOAD_DIR + template);
        //Resource resource = resourceLoader.getResource("classpath:static/files/" + template);
        try{

            ClassPathResource resourceHelvetica97 = new ClassPathResource("fonts/Helvetica_97.ttf");
            ClassPathResource resourceHelvetica47 = new ClassPathResource("fonts/Helvetica_47.ttf");
            //String fontHelvetica97 = resourceHelvetica97.getFile().getAbsolutePath();
            InputStream inputStreamHelvetica97 = resourceHelvetica97.getInputStream();
            //String fontHelvetica47 = resourceHelvetica47.getFile().getAbsolutePath();
            InputStream inputStreamHelvetica47 = resourceHelvetica47.getInputStream();

            ByteArrayOutputStream baosfontHelvetica97 = new ByteArrayOutputStream();
            byte[] bufferfontHelvetica97 = new byte[1024];
            int bytesReadfontHelvetica97;
            while ((bytesReadfontHelvetica97 = inputStreamHelvetica97.read(bufferfontHelvetica97)) != -1) {
                baosfontHelvetica97.write(bufferfontHelvetica97, 0, bytesReadfontHelvetica97);
            }
            byte[] fontBytes = baosfontHelvetica97.toByteArray();
            BaseFont baseFontHelvetica97 = BaseFont.createFont("Helvetica_97.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, false, fontBytes, null);

            ByteArrayOutputStream baosfontHelvetica47 = new ByteArrayOutputStream();
            byte[] bufferfontHelvetica47 = new byte[1024];
            int bytesReadfontHelvetica47;
            while ((bytesReadfontHelvetica47 = inputStreamHelvetica47.read(bufferfontHelvetica47)) != -1) {
                baosfontHelvetica47.write(bufferfontHelvetica47, 0, bytesReadfontHelvetica47);
            }
            byte[] fontBytesfontHelvetica47 = baosfontHelvetica47.toByteArray();
            BaseFont baseFontHelvetica47 = BaseFont.createFont("Helvetica_47.ttf", BaseFont.CP1257, BaseFont.EMBEDDED, false, fontBytesfontHelvetica47, null);

            //PdfReader pdfReader = new PdfReader(resource.getInputStream());
            FileInputStream fis = new FileInputStream(file);
            PdfReader pdfReader = new PdfReader(fis);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
            String codigoEncriptado = estudiante.getCodigoEncriptado();
            String urlQrCode = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString() + "/autenticacionCertificado/" + codigoEncriptado;
            logger.info("urlQrCode: " + urlQrCode);
            //pdfStamper.setEncryption(codigo.getBytes(), codigo.getBytes(), PdfWriter.ALLOW_COPY, PdfWriter.STANDARD_ENCRYPTION_40);

            //Image to be added in existing pdf file.
            QRGenerator qrGenerator = new QRGenerator();
            Image image = Image.getInstance(qrGenerator.imageToBytes(qrGenerator.generarCodigoQR(urlQrCode)));
            image.scaleAbsolute(96, 96); //Scale image's width and height
            image.setAbsolutePosition(712.25f, 46.75f); //Set position for image in PDF

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
                float fontSize = 34.85f;

                float textWidth = baseFontHelvetica97.getWidthPoint(texto, fontSize);
                float centerX = (pageHeight - textWidth) / 2;//Se utiliza pagHeight porque lo calcula en orientación horizontal el largo es como si fuera la altura

                PdfContentByte pdfContentByte = pdfStamper.getOverContent(i);
                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(baseFontHelvetica97, fontSize); // set font and size
                pdfContentByte.setTextMatrix(centerX, 313); // set x and y co-ordinates
                pdfContentByte.setRGBColorFill(34, 34, 34);
                //pdfContentByte.setRGBColorFill(255, 0, 0);
                pdfContentByte.showText(texto);
                //pdfContentByte.setLeading(10.25f);
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(baseFontHelvetica47, 12.5f);
                pdfContentByte.setTextMatrix(640, 27.75f); // set x and y co-ordinates
                pdfContentByte.setRGBColorFill(34, 34, 34);
                //pdfContentByte.setRGBColorFill(255, 0, 0);
                pdfContentByte.showText(estudiante.getCodigo()); // add the text
                pdfContentByte.setLeading(4.5f);
                pdfContentByte.endText();

                pdfContentByte.beginText();
                pdfContentByte.setFontAndSize(baseFontHelvetica47, 12.5f);
                pdfContentByte.setTextMatrix(640.5f, 27.75f); // set x and y co-ordinates
                pdfContentByte.setRGBColorFill(34, 34, 34);
                //pdfContentByte.setRGBColorFill(255, 0, 0);
                pdfContentByte.showText(estudiante.getCodigo()); // add the text
                pdfContentByte.setLeading(4.5f);
                pdfContentByte.endText();

                pdfContentByte.addImage(image);

            }

            pdfStamper.close();
            pdfReader.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return outputStream;
    }
}
