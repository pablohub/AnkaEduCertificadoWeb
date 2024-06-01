package com.pdev.AnkaEduCertificadoWeb.repository;

import com.pdev.AnkaEduCertificadoWeb.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findByCodigoCertificado(String codigoCertificado);
    Estudiante findByCodigoEncriptado(String codigoEncriptado);
    Estudiante findByCodigo(String codigo);
}