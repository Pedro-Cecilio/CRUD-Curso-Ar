package com.dbserver.crud_curso.domain.aluno.dto;

import com.dbserver.crud_curso.domain.enums.GrauEscolaridade;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarAlunoDto(
    @Email(message = "Email com formato inválido")
    @NotNull(message = "Email deve ser informado")
    String email,

    @Size(min = 8, message = "Senha deve conter 8 caracteres no mínimo")
    @NotNull(message = "Senha deve ser informada")
    String senha,

    @Size(min = 3, max = 20, message = "Nome deve conter 3 caracteres no mínimo e 20 no máximo")
    @NotNull(message = "Nome deve ser informado")
    String nome,

    @Size(min = 2, max = 20, message = "Sobrenome deve conter 2 caracteres no mínimo e 20 no máximo")
    @NotNull(message = "Sobrenome deve ser informado")
    String sobrenome,

    @NotNull(message = "Idade deve ser informada")
    Long idade,

    @NotNull(message = "Grau de Escolaridade deve ser informado")
    String grauEscolaridade

) {
    
}
