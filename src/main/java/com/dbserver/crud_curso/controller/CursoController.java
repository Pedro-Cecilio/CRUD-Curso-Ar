package com.dbserver.crud_curso.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.NoSuchElementException;
import java.util.List;

import com.dbserver.crud_curso.domain.curso.Curso;
import com.dbserver.crud_curso.domain.curso.CursoService;
import com.dbserver.crud_curso.domain.curso.dto.AtualizarDadosCursoDto;
import com.dbserver.crud_curso.domain.curso.dto.CriarCursoDto;
import com.dbserver.crud_curso.domain.curso.dto.CursoRespostaDto;
import com.dbserver.crud_curso.utils.Utils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/curso")
public class CursoController {
    private CursoService cursoService;
    private Utils utils;
    private static final String MENSAGEM_NAO_ENCONTRADO = "Curso não encontrado.";

    public CursoController(CursoService cursoService, Utils utils) {
        this.cursoService = cursoService;
        this.utils = utils;
    }

    @SecurityRequirement(name = "bearer-key")
    @PostMapping
    public ResponseEntity<CursoRespostaDto> criarCurso(@RequestBody @Valid CriarCursoDto cursoDto) {
        Long professorId = utils.pegarIdDaPessoaLogada();
        Curso curso = this.cursoService.criarCurso(cursoDto, professorId);
        CursoRespostaDto resposta = new CursoRespostaDto(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @SecurityRequirement(name = "bearer-key")
    @PutMapping("/{id}")
    public ResponseEntity<CursoRespostaDto> atualizarCurso(@RequestBody AtualizarDadosCursoDto cursoDto,
            @PathVariable("id") String id) {
        try {
            Long idLong = Long.parseLong(id);
            Curso curso = this.cursoService.atualizarCurso(cursoDto, idLong);
            CursoRespostaDto resposta = new CursoRespostaDto(curso);
            return ResponseEntity.status(HttpStatus.OK).body(resposta);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    @SecurityRequirement(name = "bearer-key")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarCurso(@PathVariable("id") String id) {
        try {
            Long idLong = Long.parseLong(id);
            this.cursoService.deletarCurso(idLong);
            return ResponseEntity.status(HttpStatus.OK).body("Curso deletado com sucesso");
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    @SecurityRequirement(name = "bearer-key")
    @GetMapping
    public ResponseEntity<List<CursoRespostaDto>> listarTodosCursos(@ParameterObject Pageable pageable) {
        List<Curso> listaCurso = this.cursoService.listarTodosCursos(pageable);
        List<CursoRespostaDto> listaResposta = listaCurso.stream().map(CursoRespostaDto::new).toList();
        return ResponseEntity.ok(listaResposta);
    }

    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/{id}")
    public ResponseEntity<CursoRespostaDto> buscarCurso(@PathVariable("id") String id) {
        try {
            Long idLong = Long.parseLong(id);
            return ResponseEntity.status(HttpStatus.OK).body(new CursoRespostaDto(this.cursoService.pegarCurso(idLong)));
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }
}
