package com.dbserver.crud_curso.controller;

import org.springframework.web.bind.annotation.RestController;

import com.dbserver.crud_curso.domain.aluno.Aluno;
import com.dbserver.crud_curso.domain.aluno.AlunoService;
import com.dbserver.crud_curso.domain.aluno.dto.AlunoRespostaDto;
import com.dbserver.crud_curso.domain.aluno.dto.AtualizarDadosAlunoDto;
import com.dbserver.crud_curso.domain.aluno.dto.CriarAlunoDto;
import com.dbserver.crud_curso.domain.pessoa.Pessoa;

import jakarta.validation.Valid;

import java.util.NoSuchElementException;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping(value = "/aluno")
public class AlunoController {
    private AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    public ResponseEntity<AlunoRespostaDto> criarAluno(@RequestBody @Valid CriarAlunoDto alunoDto) {
        Aluno aluno = this.alunoService.criarAluno(alunoDto);
        AlunoRespostaDto resposta = new AlunoRespostaDto(aluno);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoRespostaDto> atualizarAluno(@RequestBody AtualizarDadosAlunoDto alunoDto,
            @PathVariable("id") String id) {
        try {
            Long idLong = Long.parseLong(id);
            Aluno aluno = this.alunoService.atualizarAluno(alunoDto, idLong);
            AlunoRespostaDto resposta = new AlunoRespostaDto(aluno);
            return ResponseEntity.status(HttpStatus.OK).body(resposta);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("Aluno não encontrado.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirAluno(@PathVariable("id") String id) {
        try {
            Long idLong = Long.parseLong(id);
            this.alunoService.deletarAluno(idLong);
            return ResponseEntity.status(HttpStatus.OK).body("Aluno deletado com sucesso");
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("Aluno não encontrado.");
        }
    }
    
    @GetMapping()
    public ResponseEntity<List<AlunoRespostaDto>> listarTodosAlunos(Pageable pageable) {
        List<AlunoRespostaDto> listaDeAlunos = this.alunoService.listarTodosAlunos(pageable);
        return ResponseEntity.ok(listaDeAlunos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AlunoRespostaDto> buscarAluno(@PathVariable("id") String id) {
        try {
            Long idLong = Long.parseLong(id);
            return ResponseEntity.status(HttpStatus.OK).body(this.alunoService.pegarAluno(idLong));
        } catch (NumberFormatException e) {
            throw new NoSuchElementException("Aluno não encontrado.");
        }
    }
    
}
