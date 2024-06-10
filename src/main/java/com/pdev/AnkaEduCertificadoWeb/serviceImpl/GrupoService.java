package com.pdev.AnkaEduCertificadoWeb.serviceImpl;

import com.pdev.AnkaEduCertificadoWeb.model.Estudiante;
import com.pdev.AnkaEduCertificadoWeb.model.Grupo;
import com.pdev.AnkaEduCertificadoWeb.model.response.ResponseGenerico;
import com.pdev.AnkaEduCertificadoWeb.repository.GrupoRepository;
import com.pdev.AnkaEduCertificadoWeb.service.IGrupoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.pdev.AnkaEduCertificadoWeb.util.Constantes.*;

@Service
public class GrupoService implements IGrupoService {

    private static final Logger logger = LoggerFactory.getLogger(GrupoService.class);

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private EstudianteService estudianteService;

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
        return grupoRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Grupo::getOrden, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Grupo> listarGrupos(String centroCapacitacion) {
        logger.info("Listar grupos");
        return grupoRepository.findByCentroCapacitacion(centroCapacitacion)
                .stream()
                .sorted(Comparator.comparing(Grupo::getOrden, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarGrupo(Long id) {
        logger.info("eliminarGrupo");
        Grupo grupo = grupoRepository.findById(id).get();
        List<Estudiante> estudiantes = estudianteService.listarEstudiantes(grupo.getCentroCapacitacion()+"-"+id);
        for(Estudiante estudiante : estudiantes){
            estudianteService.eliminarEstudiantesPorIds(estudiante.getId()+"");
        }
        grupoRepository.deleteById(id);
    }

    @Override
    public Grupo obtenerGrupoPorId(Long id) {
        return grupoRepository.getReferenceById(id);
    }
}
