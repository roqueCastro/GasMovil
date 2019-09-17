package com.example.gasmovil.Entidades;

public class Codigo {
    private static Codigo intanse;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static synchronized Codigo getIntanse() {
        if(intanse==null) {
            intanse = new Codigo();
        }
        return intanse;
    }
}
