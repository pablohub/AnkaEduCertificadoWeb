package com.pdev.AnkaEduCertificadoWeb.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "estudiantes")
@Data
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "codigo", unique = true)
    private String codigo;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "curso")
    private String curso;

    @Column(name = "fecha_inicio")
    private String fechaInicio;

    @Column(name = "fecha_finalizacion")
    private String fechaFinalizacion;

    @Column(name = "centro_capacitacion")
    private String centroCapacitacion;

    @Column(name = "fecha_creacion", columnDefinition = "DATE")
    @CreationTimestamp
    private LocalDate fechaCreacion;

    private String codigoCertificado;

    private String codigoEncriptado;

    //@Column(columnDefinition = )
    private String nombrePdf;
}