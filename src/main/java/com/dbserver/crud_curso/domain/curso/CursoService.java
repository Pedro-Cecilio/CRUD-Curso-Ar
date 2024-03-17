package com.dbserver.crud_curso.domain.curso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.List;
import com.dbserver.crud_curso.domain.curso.dto.AtualizarDadosCursoDto;
import com.dbserver.crud_curso.domain.curso.dto.CriarCursoDto;

@Service
public class CursoService {

    private CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public Curso criarCurso(CriarCursoDto cursoDto) {
        Curso curso = new Curso(cursoDto);
        this.cursoRepository.save(curso);
        return curso;
    }

    public Curso atualizarCurso(AtualizarDadosCursoDto novosDados, Long cursoId) {
        Optional<Curso> curso = this.cursoRepository.findById(cursoId);
        if (curso.isEmpty()) {
            throw new NoSuchElementException("Curso não encontrado.");
        }
        curso.get().atualizarDados(novosDados);
        this.cursoRepository.save(curso.get());
        return curso.get();
    }

    public void deletarCurso(Long cursoId) {
        Optional<Curso> curso = this.cursoRepository.findById(cursoId);
        if (curso.isEmpty()) {
            throw new NoSuchElementException("Curso não encontrado.");
        }
        this.cursoRepository.delete(curso.get());
    }

    public List<Curso> listarTodosCursos(Pageable pageable) {
        Page<Curso> cursos = this.cursoRepository.findAll(pageable);
        return cursos.stream().toList();
    }

    public Curso pegarCurso(Long cursoId) {
        Optional<Curso> curso = this.cursoRepository.findById(cursoId);
        if (curso.isEmpty()) {
            throw new NoSuchElementException("Curso não encontrado.");
        }
        return curso.get();
    }
}
