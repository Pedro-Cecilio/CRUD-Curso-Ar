package com.dbserver.crud_curso.domain.aluno;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dbserver.crud_curso.domain.aluno.dto.AlunoRespostaDto;
import com.dbserver.crud_curso.domain.aluno.dto.AtualizarDadosAlunoDto;
import com.dbserver.crud_curso.domain.aluno.dto.CriarAlunoDto;
import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;

@Service
public class AlunoService {
    private AlunoRepository alunoRepository;
    private ProfessorRepository professorRepository;

    public AlunoService(AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.alunoRepository = alunoRepository;
    }

    public void criarAluno(CriarAlunoDto alunoDto){
        Optional<Professor> professor = professorRepository.findByEmail(alunoDto.email());
        if(professor.isPresent()){
            throw new IllegalArgumentException("Email não disponível");
        }
        Aluno aluno = new Aluno(alunoDto);
        this.alunoRepository.save(aluno);
    }

    public Aluno atualizarAluno(AtualizarDadosAlunoDto novosDados, Aluno aluno){
        aluno.atualizarDadosAluno(novosDados);
        return this.alunoRepository.save(aluno);
    }

    public void deletarAluno(Long alunoId){
        Optional<Aluno> aluno = this.alunoRepository.findById(alunoId);
        if(aluno.isEmpty()){
            throw new NoSuchElementException("Aluno não encontrado.");
        }
        this.alunoRepository.delete(aluno.get());
    }

    public List<AlunoRespostaDto> listarTodosAlunos(Pageable pageable){
        Page<Aluno> pessoas = this.alunoRepository.findAll(pageable);
        return pessoas.stream().map(AlunoRespostaDto::new).toList();
    }

    public AlunoRespostaDto pegarAluno(Long alunoId){
        Optional<Aluno> aluno = this.alunoRepository.findById(alunoId);
        if(aluno.isEmpty()){
            throw new NoSuchElementException("Aluno não encontrado.");
        }
        return new AlunoRespostaDto(aluno.get());
    }
}
