package com.kata.release.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "release_request")
public class ReleaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String equipo;

    @Column(nullable = false, length = 10)
    private String tipo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "pr_id")
    private String prId;

    @Column(columnDefinition = "NUMERIC(5,2)")
    private Double cobertura;

    private String stack;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(name = "tipo_aprobacion", nullable = false, length = 10)
    private String tipoAprobacion;

    @Column(name = "razones_rechazo", columnDefinition = "TEXT")
    private String razonesRechazo;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getEquipo() { return equipo; }
    public void setEquipo(String equipo) { this.equipo = equipo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPrId() { return prId; }
    public void setPrId(String prId) { this.prId = prId; }

    public Double getCobertura() { return cobertura; }
    public void setCobertura(Double cobertura) { this.cobertura = cobertura; }

    public String getStack() { return stack; }
    public void setStack(String stack) { this.stack = stack; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipoAprobacion() { return tipoAprobacion; }
    public void setTipoAprobacion(String tipoAprobacion) { this.tipoAprobacion = tipoAprobacion; }

    public String getRazonesRechazo() { return razonesRechazo; }
    public void setRazonesRechazo(String razonesRechazo) { this.razonesRechazo = razonesRechazo; }
}
