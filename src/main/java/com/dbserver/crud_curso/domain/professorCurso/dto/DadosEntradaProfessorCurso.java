package com.dbserver.crud_curso.domain.professorCurso.dto;

import jakarta.validation.constraints.NotNull;

public record DadosEntradaProfessorCurso(
        @NotNull(message = "id do curso deve ser informado")
        Long cursoId,
        
        @NotNull(message = "id do professor deve ser informado")
        Long professorId
        ) {

}
