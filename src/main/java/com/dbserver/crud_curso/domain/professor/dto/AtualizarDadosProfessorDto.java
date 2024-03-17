package com.dbserver.crud_curso.domain.professor.dto;


public record AtualizarDadosProfessorDto(

    String senha,

    
    String nome,

   
    String sobrenome,

    Long idade,

    String grauAcademico

) {
    
}
