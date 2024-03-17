package com.dbserver.crud_curso.domain.professor.dto;

import com.dbserver.crud_curso.domain.enums.GrauAcademico;
import com.dbserver.crud_curso.domain.professor.Professor;


public record ProfessorRespostaDto(

        Long id,

        String email,

        String nome,

        String sobrenome,

        Long idade,

        GrauAcademico grauAcademico) {
        public ProfessorRespostaDto(Professor professor){
            this(professor.getId(), professor.getEmail(), professor.getNome(), professor.getSobrenome(), professor.getIdade(), professor.getGrauAcademico());
        }
}
