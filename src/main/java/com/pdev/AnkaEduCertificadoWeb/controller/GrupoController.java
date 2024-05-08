package com.pdev.AnkaEduCertificadoWeb.controller;

import com.pdev.AnkaEduCertificadoWeb.model.Grupo;
import com.pdev.AnkaEduCertificadoWeb.repository.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grupo")
public class GrupoController {

    @Autowired
    private GrupoRepository grupoRepository;

    @PostMapping
    public String crear(){
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo 1");
        grupo.setCentroCapacitacion("ANKA");
        grupoRepository.save(grupo);
        return "Grupo creado!";
    }

}
