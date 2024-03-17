package com.dbserver.crud_curso.domain.alunoCurso;

import java.util.NoSuchElementException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        Curso curso = this.cursoRepository.findById(cursoId).orElseThrow(()-> new NoSuchElementException("Curso não encontrado"));
    
        Aluno aluno = this.alunoRepository.findById(alunoId).orElseThrow(()-> new NoSuchElementException("Aluno não encontrado"));
        
        if (verificarSeAlunoEstaCadastradoNoCurso(alunoId, cursoId)) {
            throw new IllegalArgumentException("O aluno já está cadastrado neste curso.");
        }

        AlunoCurso novoAlunoCurso = new AlunoCurso(aluno, curso);
        this.alunoCursoRepository.save(novoAlunoCurso);
        return novoAlunoCurso;
    }

    public AlunoCurso atualizarStatusMatricula(Long alunoId, Long cursoId, String statusMatricula) {
        AlunoCurso alunoCurso = this.alunoCursoRepository.findByAlunoIdAndCursoId(alunoId, cursoId).orElseThrow(()->new NoSuchElementException("O aluno informado não está cadastrado no curso"));
        alunoCurso.setStatusMatricula(statusMatricula);
        this.alunoCursoRepository.save(alunoCurso);
        return alunoCurso;
    }

    public List<AlunoCurso> listarTodosAlunosDoCurso(Long cursoId, Pageable pageable){
        Page<AlunoCurso> alunoCurso = this.alunoCursoRepository.findAllByCursoId(cursoId, pageable);
        return alunoCurso.toList();
    }
    public AlunoCurso buscarAlunoDoCurso(Long alunoId, Long cursoId){
        return this.alunoCursoRepository.findByAlunoIdAndCursoId(alunoId, cursoId).orElseThrow(()-> new NoSuchElementException("Aluno não encontrado"));
    }

    public boolean verificarSeAlunoEstaCadastradoNoCurso(Long alunoId, Long cursoId){
        return this.alunoCursoRepository.findByAlunoIdAndCursoId(alunoId, cursoId).isPresent();
    }
}
