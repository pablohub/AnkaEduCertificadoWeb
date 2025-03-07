package com.pdev.AnkaEduCertificadoWeb.controller;

import com.pdev.AnkaEduCertificadoWeb.model.Estudiante;
import com.pdev.AnkaEduCertificadoWeb.model.Grupo;
import com.pdev.AnkaEduCertificadoWeb.model.response.ResponseGenerico;
import com.pdev.AnkaEduCertificadoWeb.service.IEmailService;
import com.pdev.AnkaEduCertificadoWeb.service.IEstudianteService;
import com.pdev.AnkaEduCertificadoWeb.service.IGrupoService;
import com.pdev.AnkaEduCertificadoWeb.serviceImpl.EmailServiceImpl;
import com.pdev.AnkaEduCertificadoWeb.util.ExcelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EstudianteController {

    private static final Logger logger = LoggerFactory.getLogger(EstudianteController.class);
    @Autowired
    private IEstudianteService estudianteService;

    @Autowired
    private IGrupoService grupoService;

    @Autowired
    private EmailServiceImpl emailService;

    @GetMapping("/anka")
    public String verPaginaEstudianteAnka(Model model){
        logger.info("verPaginaEstudianteAnka");

        List<Grupo> grupos = grupoService.listarGrupos("ANKA");
        if(grupos.size() > 0){
            model.addAttribute("listaGrupos", grupos);
            logger.info("GrupoID: " + model.getAttribute("grupoId"));
            Long grupoId = null;
            if(model.getAttribute("grupoId") != null){
                grupoId = Long.parseLong(model.getAttribute("grupoId").toString());
            }else {
                grupoId = grupos.get(0).getId();
            }
            model.addAttribute("grupoId", grupoId);
            model.addAttribute("listaEstudiantes", estudianteService.listarEstudiantes("ANKA-"+grupoId));
        }

        return "estudianteAnka";
    }

    @GetMapping("/estudianteAnka/grupo/{id}")
    public String cargarAnkaGrupo(RedirectAttributes redirectAttrs, @PathVariable Long id){
        redirectAttrs.addFlashAttribute("grupoId", id);
        return "redirect:/anka";
    }

    @GetMapping("/cip")
    public String verPaginaEstudianteCIP(Model model){
        logger.info("verPaginaEstudianteCIP");

        List<Grupo> grupos = grupoService.listarGrupos("CIP");
        if(grupos.size() > 0){
            model.addAttribute("listaGrupos", grupos);
            logger.info("GrupoID: " + model.getAttribute("grupoId"));
            Long grupoId = null;
            if(model.getAttribute("grupoId") != null){
                grupoId = Long.parseLong(model.getAttribute("grupoId").toString());
            }else {
                grupoId = grupos.get(0).getId();
            }
            model.addAttribute("grupoId", grupoId);
            model.addAttribute("listaEstudiantes", estudianteService.listarEstudiantes("CIP-"+grupoId));
        }
        return "estudianteCIP";
    }

    @GetMapping("/estudianteCIP/grupo/{id}")
    public String cargarCIPGrupo(RedirectAttributes redirectAttrs, @PathVariable Long id){
        redirectAttrs.addFlashAttribute("grupoId", id);
        return "redirect:/cip";
    }

    @PostMapping("/uploadExcelAnka")
    public String uploadExcelAnka(RedirectAttributes redirectAttrs, @RequestParam("file") MultipartFile file, @RequestParam("grupoId") String grupoId){
        logger.info("grupoID carga: " + grupoId);
        String message = uploadExcel(file, "ANKA-" + grupoId, Long.parseLong(grupoId));
        redirectAttrs.addFlashAttribute("message", message);
        redirectAttrs.addFlashAttribute("grupoId", grupoId);
        return "redirect:/anka";
    }

    @PostMapping("/uploadExcelCIP")
    public String uploadExcelCIP(RedirectAttributes redirectAttrs, @RequestParam("file") MultipartFile file, @RequestParam("grupoId") String grupoId){
        logger.info("grupoID carga: " + grupoId);
        String message = uploadExcel(file, "CIP-" + grupoId, Long.parseLong(grupoId));
        redirectAttrs.addFlashAttribute("message", message);
        redirectAttrs.addFlashAttribute("grupoId", grupoId);
        return "redirect:/cip";
    }

    @GetMapping("/autenticacionCertificado/{codigoEncriptado}")
    public String autenticacionCertificado(@PathVariable String codigoEncriptado, Model model){
        Estudiante estudiante = estudianteService.obtenerEstudiantePorCodigoEncriptado(codigoEncriptado);
        model.addAttribute("estudiante", estudiante);
        return "autenticacionCertificado";
    }

    @PostMapping("/eliminarEstudiantes")
    @ResponseBody
    public ResponseEntity eliminarEstudiantes(@RequestBody String ids){
        logger.info("eliminarEstudiantes " + ids);
        estudianteService.eliminarEstudiantesPorIds(ids);
        ResponseGenerico responseGenerico = new ResponseGenerico();
        responseGenerico.setCodError(0);
        responseGenerico.setMensaje("Eliminación exitosa");
        return new ResponseEntity(responseGenerico, HttpStatus.OK);
    }

    private String uploadExcel(MultipartFile file, String codigoCertificado, long grupoId){
        String message = "";
        logger.info("#######Upload " + file.getOriginalFilename());
        logger.info("#######Upload " + file.getSize());
        logger.info("#######Upload " + file.getContentType());

        if(ExcelHelper.hasExcelFormat(file)){
            try{
                estudianteService.cargarEstudiantesDesdeExcel(file, codigoCertificado);
                message = "Excel cargado correctamente!: " + file.getOriginalFilename();
            }catch (Exception e){
                logger.error(e.getMessage());
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            }
        }else{
            message = "Por favor, subir un archivo excel!";
        }

        return message;
    }

    @GetMapping("/listaEstudiantesAnka")
    @ResponseBody
    public ResponseEntity listaEstudiantesAnka(){
        logger.info("listaEstudiantesAnka");
        return new ResponseEntity(estudianteService.listarEstudiantes("ANKA"), HttpStatus.OK);
    }
}