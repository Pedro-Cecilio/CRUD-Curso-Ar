package com.dbserver.crud_curso.domain.enums;

public enum GrauEscolaridade {
    ENSINO_MEDIO_IMCOMPLETO(1),
    ENSINO_MEDIO_COMPLETO(2),
    ENSINO_SUPERIOR_INCOMPLETO(3),
    ENSINO_SUPERIOR_COMPLETO(4);

    private final int valor;
    
    GrauEscolaridade(int valor) {
        this.valor = valor;
    }
    
    public int getValor() {
        return valor;
    }
    
}
