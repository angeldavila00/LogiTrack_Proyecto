package com.example.proyecto_logitrack.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Operacion operacion;

    @Column(nullable = false)
    private Date fecha;

    @Column(nullable = true)
    private String valorAnterior;

    @Column(nullable = true)
    private String valorNuevo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id",nullable = true)
        private Usuario usuario;

}
