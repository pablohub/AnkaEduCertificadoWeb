package com.pdev.AnkaEduCertificadoWeb.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "grupos")
@Data
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "centro_capacitacion")
    private String centroCapacitacion;

    @PrePersist
    @PreUpdate
    public void convertirAMayusculas() {
        if (centroCapacitacion != null) {
            centroCapacitacion = centroCapacitacion.toUpperCase();
        }
    }

    @Column(name = "nombre")
    private String nombre;

}