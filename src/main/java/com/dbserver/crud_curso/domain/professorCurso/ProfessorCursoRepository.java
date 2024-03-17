package com.dbserver.crud_curso.domain.professorCurso;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProfessorCursoRepository extends JpaRepository<ProfessorCurso, Long>{

    Optional<ProfessorCurso> findByProfessorIdAndCursoId(Long professorId, Long cursoId);

    Optional<ProfessorCurso> findByProfessorIdAndCursoIdAndAtivoTrue(Long professorId, Long cursoId);

    Optional<ProfessorCurso> findByProfessorIdAndCursoIdAndAtivoFalse(Long professorId, Long cursoId);

    Page<ProfessorCurso> findAllByCursoId(Long cursoId, Pageable pageable);

    Page<ProfessorCurso> findAllByCursoIdAndAtivoTrue(Long cursoId, Pageable pageable);
    
}
