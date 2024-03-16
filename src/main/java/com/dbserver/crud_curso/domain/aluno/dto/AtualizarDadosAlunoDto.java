package com.dbserver.crud_curso.domain.aluno.dto;

import com.dbserver.crud_curso.domain.enums.GrauEscolaridade;


public record AtualizarDadosAlunoDto(

    String senha,

    
    String nome,

   
    String sobrenome,

    Long idade,

    GrauEscolaridade grauEscolaridade

) {
    
}
