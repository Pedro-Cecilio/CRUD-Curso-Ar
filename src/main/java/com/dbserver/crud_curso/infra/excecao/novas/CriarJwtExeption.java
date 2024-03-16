package com.dbserver.crud_curso.infra.excecao.novas;

public class CriarJwtExeption extends RuntimeException {
     // Construtor padrão
     public CriarJwtExeption() {
        super();
    }

    // Construtor com mensagem de erro
    public CriarJwtExeption(String mensagem) {
        super(mensagem);
    }

    // Construtor com mensagem de erro e causa
    public CriarJwtExeption(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}