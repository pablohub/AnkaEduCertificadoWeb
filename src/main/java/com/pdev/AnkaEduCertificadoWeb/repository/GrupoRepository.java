package com.pdev.AnkaEduCertificadoWeb.repository;

import com.pdev.AnkaEduCertificadoWeb.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    List<Grupo> findByCentroCapacitacion(String centroCapacitacion);
}