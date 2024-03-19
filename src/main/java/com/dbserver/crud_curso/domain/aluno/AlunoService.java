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

    public Aluno criarAluno(CriarAlunoDto alunoDto) {
        boolean emailJaExiste = this.verificarSeEmailExiste(alunoDto.email());
        if (emailJaExiste) {
            throw new IllegalArgumentException("Email não disponível");
        }
        Aluno aluno = new Aluno(alunoDto);
        this.alunoRepository.save(aluno);
        return aluno;
    }

    public Aluno atualizarAluno(AtualizarDadosAlunoDto novosDados, Long alunoId) {
        Aluno aluno = this.alunoRepository.findByIdAndDesativadaFalse(alunoId).orElseThrow(()->new NoSuchElementException("Aluno não encontrado."));
        aluno.atualizarDadosAluno(novosDados);
        this.alunoRepository.save(aluno);
        return aluno;
    }

    public Aluno deletarAluno(Long alunoId) {
        Aluno aluno = this.alunoRepository.findByIdAndDesativadaFalse(alunoId).orElseThrow(()->new NoSuchElementException("Aluno não encontrado."));
        aluno.setDesativada(true);
        this.alunoRepository.save(aluno);
        return aluno;
    }

    public List<AlunoRespostaDto> listarTodosAlunos(Pageable pageable) {
        Page<Aluno> pessoas = this.alunoRepository.findAllByDesativadaFalse(pageable);
        return pessoas.stream().map(AlunoRespostaDto::new).toList();
    }

    public AlunoRespostaDto pegarAluno(Long alunoId) {
        Aluno aluno = this.alunoRepository.findByIdAndDesativadaFalse(alunoId).orElseThrow(()->new NoSuchElementException("Aluno não encontrado."));

        return new AlunoRespostaDto(aluno);
    }

    public boolean verificarSeEmailExiste(String email) {
        Optional<Professor> professor = professorRepository.findByEmail(email);
        Optional<Aluno> alunoExistente = alunoRepository.findByEmail(email);
        if (professor.isPresent() || alunoExistente.isPresent()) {
            return true;
        }
        return false;
    }

    public Aluno reativarContaAluno(long alunoId){
        Aluno aluno = this.alunoRepository.findByIdAndDesativadaTrue(alunoId).orElseThrow(()->new NoSuchElementException("Aluno não encontrado ou não possui conta ativa."));
        aluno.setDesativada(false);
        this.alunoRepository.save(aluno);
        return aluno;
    }
}
