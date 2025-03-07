package com.pdev.AnkaEduCertificadoWeb.controller;

import com.pdev.AnkaEduCertificadoWeb.model.Grupo;
import com.pdev.AnkaEduCertificadoWeb.service.IGrupoService;
import com.pdev.AnkaEduCertificadoWeb.util.PdfHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GrupoController {

    private static final Logger logger = LoggerFactory.getLogger(EstudianteController.class);
    private static final String UPLOAD_DIR = "src/main/resources/static/files/";
    //private static final String UPLOAD_DIR = "/appWeb/AnkaEduCertificadoWeb/templateCertificados/";

    @Autowired
    private IGrupoService grupoService;

    @GetMapping("/grupos")
    public String grupos(Model model){
        logger.info("grupos");
        List<String> options = new ArrayList<>();
        options.add("ANKA");
        options.add("CIP");

        Grupo grupo = new Grupo();
        if(model.getAttribute("grupoId") != null){
            Long id = Long.parseLong(model.getAttribute("grupoId").toString());
            grupo = grupoService.obtenerGrupoPorId(id);
        }

        model.addAttribute("options", options);
        model.addAttribute("grupo", grupo);
        model.addAttribute("listaGrupos", grupoService.listarTodosLosGrupos());
        return "grupos";
    }

    @PostMapping("/crearGrupo")
    public String crearGrupo(RedirectAttributes redirectAttrs, @ModelAttribute("grupo") Grupo grupo, @RequestParam("file") MultipartFile file){
        String message = uploadPdf(file, grupo);
        redirectAttrs.addFlashAttribute("message", message);
        return "redirect:/grupos";
    }

    @GetMapping("/verPdf/{id}")
    @ResponseBody
    public void verPdf(@PathVariable long id, HttpServletResponse response) throws IOException {
        Grupo grupo = grupoService.obtenerGrupoPorId(id);
        String nombrePdf = grupo.getNombreTemplatePdf();
        File file = new File(UPLOAD_DIR+nombrePdf);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado");
            return;
        }

        // Establecer los encabezados de la respuesta
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + nombrePdf + "\"");
        response.setContentLength((int) file.length());

        // Leer y escribir el archivo en la respuesta
        try (FileInputStream fis = new FileInputStream(file);
             ServletOutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }

    @GetMapping("/eliminarGrupo/{id}")
    public String eliminarGrupo(@PathVariable Long id){
        grupoService.eliminarGrupo(id);
        return "redirect:/grupos";
    }

    @GetMapping("/editarGrupo/{id}")
    public String editarGrupo(RedirectAttributes redirectAttrs, Model model, @PathVariable Long id){
        redirectAttrs.addFlashAttribute("grupoId", id);
        return "redirect:/grupos";
    }

    private String uploadPdf(MultipartFile file, Grupo grupo){
        String message = "";
        logger.info("#######Upload " + file.getOriginalFilename());
        logger.info("#######Upload " + file.getSize());
        logger.info("#######Upload " + file.getContentType());

        if(file.isEmpty()){
            grupoService.crearGrupo(grupo);
            message = "Grupo creado correctamente!";
        }else if(PdfHelper.hasPdfFormat(file)){
            try{
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Obtener la ruta completa del archivo a guardar
                Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());

                // Guardar el archivo en la ubicación especificada
                Files.write(path, file.getBytes());
                grupo.setNombreTemplatePdf(file.getOriginalFilename());
                //grupo.setPdfBase64(PdfHelper.Base64EncodePdf(file));
                grupoService.crearGrupo(grupo);
                message = "Pdf cargado correctamente!: " + file.getOriginalFilename();
            }catch (Exception e){
                logger.error(e.getMessage());
                message = "No se puede cargar el archivo: " + file.getOriginalFilename() + "!";
            }
        }else{
            message = "Por favor, subir un archivo pdf!";
        }

        return message;
    }

}
