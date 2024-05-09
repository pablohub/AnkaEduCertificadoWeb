package com.pdev.AnkaEduCertificadoWeb.controller;

import com.pdev.AnkaEduCertificadoWeb.model.Grupo;
import com.pdev.AnkaEduCertificadoWeb.service.IGrupoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GrupoController {

    private static final Logger logger = LoggerFactory.getLogger(EstudianteController.class);

    @Autowired
    private IGrupoService grupoService;

    @GetMapping("/grupos")
    public String grupos(Model model){
        logger.info("grupos");
        Grupo grupo = new Grupo();
        model.addAttribute("grupo", grupo);
        model.addAttribute("listaGrupos", grupoService.listarTodosLosGrupos());
        return "grupos";
    }

    @PostMapping("/crearGrupo")
    public String crearGrupo(@ModelAttribute("grupo") Grupo grupo){
        grupoService.crearGrupo(grupo);
        return "redirect:/grupos";
    }

    @GetMapping("/eliminarGrupo/{id}")
    public String eliminarGrupo(@PathVariable Long id){
        grupoService.eliminarGrupo(id);
        return "redirect:/grupos";
    }

}
