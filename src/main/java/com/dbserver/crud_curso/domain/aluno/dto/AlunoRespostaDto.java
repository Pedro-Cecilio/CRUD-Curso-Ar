package com.dbserver.crud_curso.domain.aluno.dto;

import com.dbserver.crud_curso.domain.aluno.Aluno;
import com.dbserver.crud_curso.domain.enums.GrauEscolaridade;


public record AlunoRespostaDto(

        Long id,

        String email,

        String nome,

        String sobrenome,

        Long idade,

        GrauEscolaridade grauEscolaridade) {
        public AlunoRespostaDto(Aluno aluno){
            this(aluno.getId(), aluno.getEmail(), aluno.getNome(), aluno.getSobrenome(), aluno.getIdade(), aluno.getGrauEscolaridade());
        }
}
