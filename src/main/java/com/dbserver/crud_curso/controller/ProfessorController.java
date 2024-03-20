package com.dbserver.crud_curso.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorService;
import com.dbserver.crud_curso.domain.professor.dto.AtualizarDadosProfessorDto;
import com.dbserver.crud_curso.domain.professor.dto.CriarProfessorDto;
import com.dbserver.crud_curso.domain.professor.dto.ProfessorRespostaDto;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(value = "/professor")
public class ProfessorController {
    private ProfessorService professorService;
    private static final String MENSAGEM_NAO_ENCONTRADO = "Professor não encontrado.";
    
    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping
    public ResponseEntity<ProfessorRespostaDto> criarProfessor(@RequestBody @Valid CriarProfessorDto professorDto) {
        Professor professor = this.professorService.criarProfessor(professorDto);
        ProfessorRespostaDto resposta = new ProfessorRespostaDto(professor);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @SecurityRequirement(name = "bearer-key")
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorRespostaDto> atualizarProfessor(@RequestBody AtualizarDadosProfessorDto professorDto,
            @PathVariable("id") String id) {
        try {
            Long idLong = Long.parseLong(id);
            Professor professor = this.professorService.atualizarProfessor(professorDto, idLong);
            ProfessorRespostaDto resposta = new ProfessorRespostaDto(professor);
            return ResponseEntity.status(HttpStatus.OK).body(resposta);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    @SecurityRequirement(name = "bearer-key")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarProfessor(@PathVariable("id") String id) {
        try {
            Long idLong = Long.parseLong(id);
            this.professorService.deletarProfessor(idLong);
            return ResponseEntity.status(HttpStatus.OK).body("Professor deletado com sucesso");
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    @SecurityRequirement(name = "bearer-key")
    @GetMapping()
    public ResponseEntity<List<ProfessorRespostaDto>> listarTodosProfessores(@ParameterObject Pageable pageable) {
        List<ProfessorRespostaDto> listaDeProfessores = this.professorService.listarTodosProfessores
        (pageable);
        return ResponseEntity.ok(listaDeProfessores);
    }

    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorRespostaDto> buscarAluno(@PathVariable("id") String id) {
        try {
            Long idLong = Long.parseLong(id);
            return ResponseEntity.status(HttpStatus.OK).body(this.professorService.pegarProfessor(idLong));
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO);
        }
    }
    
}
