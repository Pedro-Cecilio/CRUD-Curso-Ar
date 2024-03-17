package com.dbserver.crud_curso.domain.enums;

public enum GrauAcademico {
    LICENCIATURA(1),
    BACHAREL(2),
    MESTRE(3),
    DOUTOR(4);
    private final int valor;
    
    GrauAcademico(int valor) {
        this.valor = valor;
    }
    
    public int getValor() {
        return valor;
    }
}
