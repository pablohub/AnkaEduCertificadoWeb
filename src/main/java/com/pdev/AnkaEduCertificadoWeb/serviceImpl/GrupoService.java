package com.pdev.AnkaEduCertificadoWeb.serviceImpl;

import com.pdev.AnkaEduCertificadoWeb.model.Grupo;
import com.pdev.AnkaEduCertificadoWeb.model.response.ResponseGenerico;
import com.pdev.AnkaEduCertificadoWeb.repository.GrupoRepository;
import com.pdev.AnkaEduCertificadoWeb.service.IGrupoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.pdev.AnkaEduCertificadoWeb.util.Constantes.*;

@Service
public class GrupoService implements IGrupoService {

    private static final Logger logger = LoggerFactory.getLogger(GrupoService.class);

    @Autowired
    private GrupoRepository grupoRepository;

    @Override
    public ResponseGenerico crearGrupo(Grupo grupo) {
        logger.info("Guardar estudiante");
        ResponseGenerico responseGenerico = new ResponseGenerico();
        try{
            //grupo.setCentroCapacitacion(grupo.getCentroCapacitacion().toUpperCase());
            grupoRepository.save(grupo);
            responseGenerico.setCodError(COD_OK);
            responseGenerico.setMensaje("Grupo Registrado correctamente!");
            responseGenerico.setData(grupo);
        }catch (Exception ex){
            responseGenerico.setCodError(COD_ERRO);
            responseGenerico.setMensaje("Error al Registrar grupo");
            logger.error("Error registrar grupo: " + ex.getMessage());
        }
        return responseGenerico;
    }

    @Override
    public List<Grupo> listarTodosLosGrupos() {
        logger.info("listarTodosLosGrupos");
        return grupoRepository.findAll();
    }

    @Override
    public List<Grupo> listarGrupos(String centroCapacitacion) {
        logger.info("Listar grupos");
        return grupoRepository.findByCentroCapacitacion(centroCapacitacion);
    }

    @Override
    public void eliminarGrupo(Long id) {
        logger.info("eliminarGrupo");
        grupoRepository.deleteById(id);
    }
}
