package com.pdev.AnkaEduCertificadoWeb.service;

import com.pdev.AnkaEduCertificadoWeb.model.Grupo;
import com.pdev.AnkaEduCertificadoWeb.model.response.ResponseGenerico;

import java.util.List;

public interface IGrupoService {

    ResponseGenerico crearGrupo(Grupo grupo);
    List<Grupo> listarTodosLosGrupos();
    List<Grupo> listarGrupos(String centroCapacitacion);
    void eliminarGrupo(Long id);
    Grupo obtenerGrupoPorId(Long id);

}
