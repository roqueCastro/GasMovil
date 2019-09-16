package com.example.gasmovil.Entidades;

public class Movimiento {

    private Integer id_m;
    private String fecha_m;
    private String estado_m;
    private String nombre_t;
    private String apellido_t;
    private String codigo;


    public Integer getId_m() {
        return id_m;
    }

    public void setId_m(Integer id_m) {
        this.id_m = id_m;
    }

    public String getFecha_m() {
        return fecha_m;
    }

    public void setFecha_m(String fecha_m) {
        this.fecha_m = fecha_m;
    }

    public String getEstado_m() {
        return estado_m;
    }

    public void setEstado_m(String estado_m) {
        this.estado_m = estado_m;
    }

    public String getNombre_t() {
        return nombre_t;
    }

    public void setNombre_t(String nombre_t) {
        this.nombre_t = nombre_t;
    }

    public String getApellido_t() {
        return apellido_t;
    }

    public void setApellido_t(String apellido_t) {
        this.apellido_t = apellido_t;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
