package com.dbserver.crud_curso.domain.enums;

public enum GrauAcademico {
    BACHAREL(1),
    MESTRE(2),
    DOUTOR(3);
    private final int valor;
    
    GrauAcademico(int valor) {
        this.valor = valor;
    }
    
    public int getValor() {
        return valor;
    }
}
