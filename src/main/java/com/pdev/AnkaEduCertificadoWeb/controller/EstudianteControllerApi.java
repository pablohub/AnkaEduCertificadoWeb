package com.pdev.AnkaEduCertificadoWeb.controller;

import com.pdev.AnkaEduCertificadoWeb.model.Estudiante;
import com.pdev.AnkaEduCertificadoWeb.model.Grupo;
import com.pdev.AnkaEduCertificadoWeb.model.response.ResponseGenerico;
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

@RestController
@RequestMapping("/v1/estudiantes")
public class EstudianteControllerApi {

    private static final Logger logger = LoggerFactory.getLogger(EstudianteControllerApi.class);
    @Autowired
    private IEstudianteService estudianteService;

    @Autowired
    private IGrupoService grupoService;

    @Autowired
    private EmailServiceImpl emailService;

    @GetMapping("/anka")
    public ResponseEntity verPaginaEstudianteAnka(){
        logger.info("verPaginaEstudianteAnka");
        return new ResponseEntity(estudianteService.listarEstudiantes("ANKA-1"), HttpStatus.OK);
    }


}