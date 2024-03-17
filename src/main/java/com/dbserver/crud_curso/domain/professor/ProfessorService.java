package com.dbserver.crud_curso.domain.professor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.List;
import com.dbserver.crud_curso.domain.aluno.Aluno;
import com.dbserver.crud_curso.domain.aluno.AlunoRepository;
import com.dbserver.crud_curso.domain.professor.dto.AtualizarDadosProfessorDto;
import com.dbserver.crud_curso.domain.professor.dto.CriarProfessorDto;
import com.dbserver.crud_curso.domain.professor.dto.ProfessorRespostaDto;

@Service
public class ProfessorService {
    private AlunoRepository alunoRepository;
    private ProfessorRepository professorRepository;

    public ProfessorService(AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.alunoRepository = alunoRepository;
    }

    public Professor criarProfessor(CriarProfessorDto professorDto) {
        boolean emailJaExiste = this.verificarSeEmailExiste(professorDto.email());
        if (emailJaExiste) {
            throw new IllegalArgumentException("Email não disponível");
        }
        Professor professor = new Professor(professorDto);
        this.professorRepository.save(professor);
        return professor;
    }

    public Professor atualizarProfessor(AtualizarDadosProfessorDto novosDados, Long professorId) {
        Professor professor = this.professorRepository.findById(professorId).orElseThrow(()-> new NoSuchElementException("Professor não encontrado."));
        professor.atualizarDadosProfessor(novosDados);
        this.professorRepository.save(professor);
        return professor;
    }

    public void deletarProfessor(Long professorId) {
        Professor professor = this.professorRepository.findById(professorId).orElseThrow(()-> new NoSuchElementException("Professor não encontrado."));
        this.professorRepository.delete(professor);
    }

    public List<ProfessorRespostaDto> listarTodosProfessores(Pageable pageable) {
        Page<Professor> professores = this.professorRepository.findAll(pageable);
        return professores.stream().map(ProfessorRespostaDto::new).toList();
    }

    public ProfessorRespostaDto pegarProfessor(Long professorId) {
        Professor professor = this.professorRepository.findById(professorId).orElseThrow(()-> new NoSuchElementException("Professor não encontrado."));
        return new ProfessorRespostaDto(professor);
    }

    public boolean verificarSeEmailExiste(String email) {
        Optional<Professor> professor = professorRepository.findByEmail(email);
        Optional<Aluno> alunoExistente = alunoRepository.findByEmail(email);
        if (professor.isPresent() || alunoExistente.isPresent()) {
            return true;
        }
        return false;
    }
}
