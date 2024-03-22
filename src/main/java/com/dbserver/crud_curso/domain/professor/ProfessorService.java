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
import com.dbserver.crud_curso.domain.professorCurso.ProfessorCursoRepository;

@Service
public class ProfessorService {
    private static final String MENSAGEM_NAO_ENCONTRADO = "Professor não encontrado.";

    private AlunoRepository alunoRepository;
    private ProfessorRepository professorRepository;
    private ProfessorCursoRepository professorCursoRepository;

    public ProfessorService(AlunoRepository alunoRepository, ProfessorRepository professorRepository, ProfessorCursoRepository professorCursoRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.professorCursoRepository = professorCursoRepository;
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
        Professor professor = this.professorRepository.findByIdAndDesativadaFalse(professorId).orElseThrow(()-> new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO));
        professor.atualizarDadosProfessor(novosDados);
        this.professorRepository.save(professor);
        return professor;
    }

    public Professor deletarProfessor(Long professorId) {
        Professor professor = this.professorRepository.findByIdAndDesativadaFalse(professorId).orElseThrow(()-> new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO));
        professor.setDesativada(true);
        this.professorCursoRepository.findAllByProfessorId(professorId).forEach(professorCurso -> {
            if(professorCurso.isAtivo()){
                professorCurso.setAtivo(false);
                this.professorCursoRepository.save(professorCurso);
            }
        });
        this.professorRepository.save(professor);
        return professor;
    }

    public List<ProfessorRespostaDto> listarTodosProfessores(Pageable pageable) {
        Page<Professor> professores = this.professorRepository.findAllByDesativadaFalse(pageable);
        return professores.stream().map(ProfessorRespostaDto::new).toList();
    }

    public ProfessorRespostaDto pegarProfessor(Long professorId) {
        Professor professor = this.professorRepository.findByIdAndDesativadaFalse(professorId).orElseThrow(()-> new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO));
        return new ProfessorRespostaDto(professor);
    }

    public boolean verificarSeEmailExiste(String email) {
        Optional<Professor> professor = professorRepository.findByEmail(email);
        Optional<Aluno> alunoExistente = alunoRepository.findByEmail(email);
        return (professor.isPresent() || alunoExistente.isPresent());
    }
    public Professor reativarContaProfessor(long professorId){
        Professor professor = this.professorRepository.findByIdAndDesativadaTrue(professorId).orElseThrow(()->new NoSuchElementException("Professor não encontrado ou não possui conta desativada."));
        professor.setDesativada(false);
        this.professorRepository.save(professor);
        return professor;
    }
}
