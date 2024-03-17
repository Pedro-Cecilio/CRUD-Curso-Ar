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
import com.dbserver.crud_curso.domain.professorCurso.ProfessorCurso;
import com.dbserver.crud_curso.domain.professorCurso.ProfessorCursoRepository;

@Service
public class CursoService {

    private CursoRepository cursoRepository;
    private ProfessorRepository professorRepository;
    private ProfessorCursoRepository professorCursoRepository;

    public CursoService(CursoRepository cursoRepository, ProfessorRepository professorRepository, ProfessorCursoRepository professorCursoRepository) {
        this.cursoRepository = cursoRepository;
        this.professorRepository = professorRepository;
    }

    public Curso criarCurso(CriarCursoDto cursoDto, Long professorId) {
        Professor professor = this.professorRepository.findById(professorId).orElseThrow(()->new NoSuchElementException("Professor não encontrado"));
        Curso curso = new Curso(cursoDto);
        this.cursoRepository.save(curso);
        ProfessorCurso professorCurso = new ProfessorCurso(professor, curso, true);
        this.professorCursoRepository.save(professorCurso);
        return curso;
    }

    public Curso atualizarCurso(AtualizarDadosCursoDto novosDados, Long cursoId) {
        Curso curso = this.cursoRepository.findById(cursoId).orElseThrow(()->new NoSuchElementException("Curso não encontrado"));
        curso.atualizarDados(novosDados);
        this.cursoRepository.save(curso);
        return curso;
    }

    public void deletarCurso(Long cursoId) {
        Curso curso = this.cursoRepository.findById(cursoId).orElseThrow(()->new NoSuchElementException("Curso não encontrado"));
        this.cursoRepository.delete(curso);
    }

    public List<Curso> listarTodosCursos(Pageable pageable) {
        Page<Curso> cursos = this.cursoRepository.findAll(pageable);
        return cursos.toList();
    }

    public Curso pegarCurso(Long cursoId) {
        Curso curso = this.cursoRepository.findById(cursoId).orElseThrow(()->new NoSuchElementException("Curso não encontrado"));
        return curso;
    }
}
