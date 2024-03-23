package com.dbserver.crud_curso.domain.professorCurso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
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

    public ProfessorCurso cadastrarProfessorNoCurso(Long professorId, Long cursoId, boolean criador) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new NoSuchElementException("Curso não encontrado."));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new NoSuchElementException("Professor não encontrado"));

        if (this.verificarSeProfessorEstaCadastradoNoCurso(professorId, cursoId)) {
            ProfessorCurso professorCurso = this.professorCursoRepository
                    .findByProfessorIdAndCursoId(professorId, cursoId).get();
            if (professorCurso.isAtivo()) {
                throw new IllegalArgumentException("O Professor já está cadastrado neste curso.");
            }
            professorCurso.setAtivo(true);
            this.professorCursoRepository.save(professorCurso);
            return professorCurso;
        }
        if (professor.getGrauAcademico().getValor() < curso.getGrauAcademicoMinimo().getValor()) {
            throw new IllegalArgumentException("O professor não possui grau acadêmico mínimo para lecionar no curso.");

        }

        ProfessorCurso novoProfessorCurso = new ProfessorCurso(professor, curso, criador);
        this.professorCursoRepository.save(novoProfessorCurso);
        return novoProfessorCurso;
    }
    
    public ProfessorCurso atualizarStatusAtivoProfessor(Long professorId, Long cursoId, boolean ativo) {

        if (!this.verificarSeProfessorEstaCadastradoNoCurso(professorId, cursoId)) {
            throw new NoSuchElementException("Professor não está cadastrado no curso");
        }
        ProfessorCurso professorCurso = this.professorCursoRepository
                .findByProfessorIdAndCursoId(professorId, cursoId).get();
        if (ativo == professorCurso.isAtivo()) return professorCurso;
        professorCurso.setAtivo(ativo);
        this.professorCursoRepository.save(professorCurso);
        return professorCurso;
    }

    public void removerProfessorDoCurso(Long professorId, Long cursoId) {
        if (!this.verificarSeProfessorEstaAtivoNoCurso(professorId, cursoId)) {
            throw new NoSuchElementException("Professor não está cadastrado/ativo no curso");
        }
        ProfessorCurso professorCurso = this.professorCursoRepository.findByProfessorIdAndCursoId(professorId, cursoId)
                .get();
        professorCurso.setAtivo(false);
        this.professorCursoRepository.save(professorCurso);
    }

    public List<ProfessorCurso> listarTodosProfessoresAtivosDoCurso(Long cursoId, Pageable pageable) {
        Page<ProfessorCurso> pageProfessorCurso = this.professorCursoRepository.findAllByCursoIdAndAtivoTrue(cursoId,
                pageable);
        return pageProfessorCurso.toList();
    }

    public ProfessorCurso pegarProfessorDoCurso(Long professorId, Long cursoId) {
        return this.professorCursoRepository
                .findByProfessorIdAndCursoIdAndAtivoTrue(professorId, cursoId)
                .orElseThrow(() -> new NoSuchElementException("Professor não está cadastrado no curso"));
    }

    public boolean verificarSeProfessorEstaCadastradoNoCurso(Long professorId, Long cursoId) {
        return this.professorCursoRepository.findByProfessorIdAndCursoId(professorId,
                cursoId).isPresent();
    }
    public boolean verificarSeProfessorEstaAtivoNoCurso(Long professorId, Long cursoId) {
        return this.professorCursoRepository.findByProfessorIdAndCursoIdAndAtivoTrue(professorId,
                cursoId).isPresent();
    }
}
