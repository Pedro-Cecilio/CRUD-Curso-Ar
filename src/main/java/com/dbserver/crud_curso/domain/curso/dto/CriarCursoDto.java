package com.dbserver.crud_curso.domain.curso.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarCursoDto(

    @NotNull(message = "Titulo deve ser informado")
    @Size(min = 3, message = "Titulo deve conter 3 caracteres no mínimo")
    String titulo,

    @NotNull(message = "Duração deve ser informada")
    Long duracaoMeses,

    @NotNull(message = "Grau escolar mínimo deve ser informado")
    String grauEscolarMinimo,

    @NotNull(message = "Grau acadêmico mínimo deve ser informado")
    String grauAcademicoMinimo


) {
    
}
