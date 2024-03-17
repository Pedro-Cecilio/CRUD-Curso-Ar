package com.dbserver.crud_curso.domain.professorCurso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;
import com.dbserver.crud_curso.domain.curso.Curso;
import com.dbserver.crud_curso.domain.curso.CursoRepository;
import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;

@Service
public class ProfessorCursoService {

    private ProfessorRepository professorRepository;
    private CursoRepository cursoRepository;
    private ProfessorCursoRepository professorCursoRepository;

    public ProfessorCursoService(ProfessorRepository professorRepository, CursoRepository cursoRepository,
            ProfessorCursoRepository professorCursoRepository) {
        this.professorRepository = professorRepository;
        this.cursoRepository = cursoRepository;
        this.professorCursoRepository = professorCursoRepository;
    }

    public ProfessorCurso cadastrarProfessorNoCurso(Long professorId, Long cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new NoSuchElementException("Curso não encontrado"));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new NoSuchElementException("Professor não encontrado"));

        if (this.verificarSeProfessoEstaCadastradoNoCurso(professorId, cursoId)) {
            throw new IllegalArgumentException("O Professor já está cadastrado neste curso.");
        }

        Optional<ProfessorCurso> professorCursoAtivoFalse = this.professorCursoRepository
                .findByProfessorIdAndCursoIdAndAtivoFalse(professorId, cursoId);
        if (professorCursoAtivoFalse.isPresent()) {
            professorCursoAtivoFalse.get().setAtivo(true);
            this.professorCursoRepository.save(professorCursoAtivoFalse.get());
            return professorCursoAtivoFalse.get();
        }
        ProfessorCurso novoProfessorCurso = new ProfessorCurso(professor, curso, false);
        this.professorCursoRepository.save(novoProfessorCurso);
        return novoProfessorCurso;
    }

    public void removerProfessorDoCurso(Long professorId, Long cursoId) {
        if (this.verificarSeProfessoEstaCadastradoNoCurso(professorId, cursoId)) {
            throw new NoSuchElementException("Professor não está cadastrado no curso");
        }
        ProfessorCurso professorCurso = this.professorCursoRepository.findById(cursoId).get();
        professorCurso.setAtivo(false);
        this.professorCursoRepository.save(professorCurso);
    }

    public List<ProfessorCurso> listarTodosProfessoresAtivosDoCurso(Long cursoId, Pageable pageable) {
        Page<ProfessorCurso> pageProfessorCurso = this.professorCursoRepository.findAllByCursoIdAndAtivoTrue(cursoId, pageable);
        return pageProfessorCurso.toList();
    }

    public ProfessorCurso pegarProfessorDoCurso(Long professorId, Long cursoId) {
        ProfessorCurso pageProfessorCurso = this.professorCursoRepository
                .findByProfessorIdAndCursoIdAndAtivoTrue(professorId, cursoId)
                .orElseThrow(() -> new NoSuchElementException("Professor não está cadastrado no curso"));
        return pageProfessorCurso;
    }

    public boolean verificarSeProfessoEstaCadastradoNoCurso(Long professorId, Long cursoId) {
        return this.professorCursoRepository.findByProfessorIdAndCursoIdAndAtivoTrue(professorId,
                cursoId).isPresent();

    }
}
