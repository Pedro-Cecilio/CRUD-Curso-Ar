package com.dbserver.crud_curso.controller;

import org.springframework.web.bind.annotation.RestController;

import com.dbserver.crud_curso.domain.professorCurso.ProfessorCurso;
import com.dbserver.crud_curso.domain.professorCurso.ProfessorCursoService;
import com.dbserver.crud_curso.domain.professorCurso.dto.DadosEntradaProfessorCurso;
import com.dbserver.crud_curso.utils.Utils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.NoSuchElementException;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping(value = "/professorCurso")
public class ProfessorCursoController {
    private static final String MENSAGEM_NAO_ENCONTRADO = "Curso não encontrado.";

    private ProfessorCursoService professorCursoService;
    private Utils utils;

    public ProfessorCursoController(ProfessorCursoService professorCursoService, Utils utils) {
        this.utils = utils;
        this.professorCursoService = professorCursoService;
    }

    @SecurityRequirement(name = "bearer-key")
    @PostMapping("/{id}")
    public ResponseEntity<ProfessorCurso> cadastrarProfessorNoCurso(@PathVariable("id") String cursoId) {
        try {
            Long professorId = utils.pegarIdDaPessoaLogada();
            Long cursoIdLong = Long.parseLong(cursoId);
            ProfessorCurso resposta = this.professorCursoService.cadastrarProfessorNoCurso(professorId, cursoIdLong,
                    false);
            return ResponseEntity.status(HttpStatus.OK).body(resposta);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    @SecurityRequirement(name = "bearer-key")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> desativarProfessorNoCurso(@PathVariable("id") String cursoId) {
        try {
            Long professorId = utils.pegarIdDaPessoaLogada();
            Long cursoIdLong = Long.parseLong(cursoId);
            this.professorCursoService.removerProfessorDoCurso(professorId, cursoIdLong);
            return ResponseEntity.status(HttpStatus.OK).body("Professor desativado do curso com sucesso!");
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    @SecurityRequirement(name = "bearer-key")
    @PutMapping("reativar/{id}")
    public ResponseEntity<String> reativarProfessorNoCurso(@PathVariable("id") String cursoId) {
        try {
            Long professorId = utils.pegarIdDaPessoaLogada();
            Long cursoIdLong = Long.parseLong(cursoId);
            this.professorCursoService.atualizarStatusAtivoProfessor(professorId, cursoIdLong, true);
            return ResponseEntity.status(HttpStatus.OK).body("Professor removido do curso com sucesso!");
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/{id}")
    public ResponseEntity<List<ProfessorCurso>> listarTodosProfessoresNoCurso(@PathVariable("id") String cursoId,
            @ParameterObject Pageable pageable) {
        try {
            Long cursoIdLong = Long.parseLong(cursoId);
            List<ProfessorCurso> resposta = this.professorCursoService.listarTodosProfessoresAtivosDoCurso(cursoIdLong,
                    pageable);
            return ResponseEntity.status(HttpStatus.OK).body(resposta);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    @SecurityRequirement(name = "bearer-key")
    @GetMapping
    public ResponseEntity<ProfessorCurso> pegarProfessorDoCurso(@ParameterObject DadosEntradaProfessorCurso query) {
        ProfessorCurso resposta = this.professorCursoService.pegarProfessorDoCurso(query.professorId(),
                query.cursoId());
        return ResponseEntity.status(HttpStatus.OK).body(resposta);

    }

}
