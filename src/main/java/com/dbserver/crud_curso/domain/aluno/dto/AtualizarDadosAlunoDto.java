package com.dbserver.crud_curso.domain.aluno.dto;


public record AtualizarDadosAlunoDto(

    String senha,

    
    String nome,

   
    String sobrenome,

    Long idade,

    String grauEscolaridade

) {
    
}
