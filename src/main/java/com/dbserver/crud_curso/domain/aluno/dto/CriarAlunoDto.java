package com.dbserver.crud_curso.domain.aluno.dto;

import com.dbserver.crud_curso.domain.enums.GrauEscolaridade;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarAlunoDto(
    @Email
    @NotNull
    String email,

    @Size(min = 8, message = "Senha deve conter 8 caracteres no mínimo")
    @NotNull
    String senha,

    @Size(min = 3, max = 20, message = "Nome deve conter 3 caracteres no mínimo e 20 no máximo")
    @NotNull
    String nome,

    @Size(min = 2, max = 20, message = "Sobrenome deve conter 2 caracteres no mínimo e 20 no máximo")
    @NotNull
    String sobrenome,

    @NotNull
    Long idade,

    @NotNull
    GrauEscolaridade grauEscolaridade

) {
    
}
