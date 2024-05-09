package com.pdev.AnkaEduCertificadoWeb.service;

import com.pdev.AnkaEduCertificadoWeb.model.Estudiante;
import com.pdev.AnkaEduCertificadoWeb.model.response.ResponseGenerico;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IEstudianteService {

    ResponseGenerico guardarEstudiante(Estudiante estudiante);
    void cargarEstudiantesDesdeExcel(MultipartFile file, String codigoCertificado);
    List<Estudiante> listarEstudiantes(String codigoCertificado);
    Estudiante obtenerEstudiantePorId(long id);
    Estudiante obtenerEstudiantePorCodigoEncriptado(String codigoEncriptado);
    void eliminarEstudiantesPorIds(String ids);

}