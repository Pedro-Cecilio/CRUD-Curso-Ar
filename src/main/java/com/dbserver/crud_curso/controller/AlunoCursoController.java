package com.dbserver.crud_curso.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbserver.crud_curso.domain.alunoCurso.AlunoCurso;
import com.dbserver.crud_curso.domain.alunoCurso.AlunoCursoService;
import com.dbserver.crud_curso.domain.alunoCurso.dto.DadosEntradaAlunoCurso;
import com.dbserver.crud_curso.domain.enums.StatusMatricula;
import com.dbserver.crud_curso.utils.Utils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.util.NoSuchElementException;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(value = "/alunoCurso")
public class AlunoCursoController {
    private static final String MENSAGEM_NAO_ENCONTRADO = "Curso não encontrado.";

    private AlunoCursoService alunoCursoService;
    private Utils utils;

    public AlunoCursoController(AlunoCursoService alunoCursoService, Utils utils) {
        this.alunoCursoService = alunoCursoService;
        this.utils = utils;
    }

    @SecurityRequirement(name = "bearer-key")
    @PostMapping("/{id}")
    public ResponseEntity<AlunoCurso> cadastrarAlunoNoCurso(@PathVariable("id") String cursoId) {
        try {
            Long cursoIdLong = Long.parseLong(cursoId);
            Long alunoid = this.utils.pegarIdDaPessoaLogada();
            AlunoCurso resposta = this.alunoCursoService.cadastrarAlunoNoCurso(alunoid, cursoIdLong);
            return ResponseEntity.status(HttpStatus.OK).body(resposta);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    @SecurityRequirement(name = "bearer-key")
    @PatchMapping("/formar")
    public ResponseEntity<AlunoCurso> formarAlunoNoCurso(@RequestBody @Valid DadosEntradaAlunoCurso dadosDto) {
        AlunoCurso resposta = this.alunoCursoService.atualizarStatusMatricula(dadosDto.alunoId(), dadosDto.cursoId(),
                StatusMatricula.FORMADO.toString());
        return ResponseEntity.status(HttpStatus.OK).body(resposta);

    }

    @SecurityRequirement(name = "bearer-key")
    @PatchMapping("/trancarMatricula")
    public ResponseEntity<AlunoCurso> trancarMatricula(@RequestBody @Valid DadosEntradaAlunoCurso dadosDto) {
        AlunoCurso resposta = this.alunoCursoService.atualizarStatusMatricula(dadosDto.alunoId(), dadosDto.cursoId(),
                StatusMatricula.INATIVO.toString());
        return ResponseEntity.status(HttpStatus.OK).body(resposta);

    }

    @SecurityRequirement(name = "bearer-key")
    @PatchMapping("/reativarMatricula")
    public ResponseEntity<AlunoCurso> reativarMatricula(@RequestBody @Valid DadosEntradaAlunoCurso dadosDto) {
        AlunoCurso resposta = this.alunoCursoService.atualizarStatusMatricula(dadosDto.alunoId(), dadosDto.cursoId(),
                StatusMatricula.ATIVO.toString());
        return ResponseEntity.status(HttpStatus.OK).body(resposta);
    }

    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/{id}")
    public ResponseEntity<List<AlunoCurso>> listarTodosAlunosDoCurso(@PathVariable("id") String cursoId,
            @ParameterObject Pageable pageable) {
        try {
            Long cursoIdLong = Long.parseLong(cursoId);
            List<AlunoCurso> resposta = this.alunoCursoService.listarTodosAlunosDoCurso(cursoIdLong, pageable);
            return ResponseEntity.status(HttpStatus.OK).body(resposta);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/aluno")
    public ResponseEntity<AlunoCurso> pegarAlunoDoCurso(@ParameterObject DadosEntradaAlunoCurso dadosDto) {
        AlunoCurso resposta = this.alunoCursoService.buscarAlunoDoCurso(dadosDto.alunoId(), dadosDto.cursoId());
        return ResponseEntity.status(HttpStatus.OK).body(resposta);
    }

}
