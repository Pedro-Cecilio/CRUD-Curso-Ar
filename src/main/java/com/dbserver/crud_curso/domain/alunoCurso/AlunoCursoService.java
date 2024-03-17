package com.dbserver.crud_curso.domain.alunoCurso;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dbserver.crud_curso.domain.aluno.Aluno;
import com.dbserver.crud_curso.domain.aluno.AlunoRepository;
import com.dbserver.crud_curso.domain.curso.Curso;
import com.dbserver.crud_curso.domain.curso.CursoRepository;

@Service
public class AlunoCursoService {
    private AlunoRepository alunoRepository;
    private CursoRepository cursoRepository;
    private AlunoCursoRepository alunoCursoRepository;

    public AlunoCursoService(AlunoRepository alunoRepository, CursoRepository cursoRepository,
            AlunoCursoRepository alunoCursoRepository) {
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
        this.alunoCursoRepository = alunoCursoRepository;
    }

    public AlunoCurso cadastrarAlunoNoCurso(Long alunoId, Long cursoId) {
        Optional<Curso> curso = this.cursoRepository.findById(cursoId);
        if (curso.isEmpty()) {
            throw new NoSuchElementException("Curso não encontrado");
        }
        Optional<Aluno> aluno = this.alunoRepository.findById(alunoId);
        if (aluno.isEmpty()) {
            throw new NoSuchElementException("Aluno não encontrado");
        }
        Optional<AlunoCurso> alunoCurso = this.alunoCursoRepository.findByAlunoIdAndCursoId(alunoId, cursoId);
        if (alunoCurso.isPresent()) {
            throw new IllegalArgumentException("O aluno já está cadastrado neste curso.");
        }

        AlunoCurso novoAlunoCurso = new AlunoCurso(aluno.get(), curso.get());
        this.alunoCursoRepository.save(novoAlunoCurso);
        return novoAlunoCurso;
    }

    public AlunoCurso atualizarStatusMatriculaFormado(Long alunoId, Long cursoId, String statusMatricula) {
        Optional<AlunoCurso> alunoCurso = this.alunoCursoRepository.findByAlunoIdAndCursoId(alunoId, cursoId);
        if (alunoCurso.isEmpty()) {
            throw new NoSuchElementException("O aluno informado não está cadastrado no curso");
        }
        alunoCurso.get().setStatusMatricula(statusMatricula);
        this.alunoCursoRepository.save(alunoCurso.get());
        return alunoCurso.get();
    }
    

}
