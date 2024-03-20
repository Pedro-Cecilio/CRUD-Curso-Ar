package com.dbserver.crud_curso.domain.curso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.List;
import com.dbserver.crud_curso.domain.curso.dto.AtualizarDadosCursoDto;
import com.dbserver.crud_curso.domain.curso.dto.CriarCursoDto;
import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;
import com.dbserver.crud_curso.domain.professorCurso.ProfessorCursoService;

import jakarta.transaction.Transactional;

@Service
public class CursoService {
    private static final String MENSAGEM_NAO_ENCONTRADO = "Curso não encontrado.";

    private CursoRepository cursoRepository;
    private ProfessorCursoService professorCursoService;
    private ProfessorRepository professorRepository;

    public CursoService(CursoRepository cursoRepository, ProfessorRepository professorRepository, ProfessorCursoService professorCursoService) {
        this.cursoRepository = cursoRepository;
        this.professorRepository = professorRepository;
        this.professorCursoService = professorCursoService;
    }
    
    @Transactional
    public Curso criarCurso(CriarCursoDto cursoDto, Long professorId) {
        Professor professor = this.professorRepository.findById(professorId).orElseThrow(()->new NoSuchElementException("Professor não encontrado"));
        Curso curso = new Curso(cursoDto);
        this.cursoRepository.save(curso);
        this.professorCursoService.cadastrarProfessorNoCurso(professor.getId(), curso.getId(), true);
        return curso;
    }

    public Curso atualizarCurso(AtualizarDadosCursoDto novosDados, Long cursoId) {
        Curso curso = this.cursoRepository.findById(cursoId).orElseThrow(()->new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO));
        curso.atualizarDados(novosDados);
        this.cursoRepository.save(curso);
        return curso;
    }

    public void deletarCurso(Long cursoId) {
        Curso curso = this.cursoRepository.findById(cursoId).orElseThrow(()->new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO));
        this.cursoRepository.delete(curso);
    }

    public List<Curso> listarTodosCursos(Pageable pageable) {
        Page<Curso> cursos = this.cursoRepository.findAll(pageable);
        return cursos.toList();
    }

    public Curso pegarCurso(Long cursoId) {
        return this.cursoRepository.findById(cursoId).orElseThrow(()->new NoSuchElementException(MENSAGEM_NAO_ENCONTRADO));
    }
}
