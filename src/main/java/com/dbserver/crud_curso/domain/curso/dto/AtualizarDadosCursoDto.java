package com.dbserver.crud_curso.domain.curso.dto;


public record AtualizarDadosCursoDto(

        String titulo,

        Long duracaoMeses,

        String grauEscolarMinimo,

        String grauAcademicoMinimo) {

}
