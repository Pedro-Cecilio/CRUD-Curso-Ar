package com.dbserver.crud_curso.domain.autenticacao;

import lombok.Getter;

@Getter
public class Autenticacao {
    private String email;
    private String senha;
    
    protected Autenticacao() {
    }

    public Autenticacao(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
