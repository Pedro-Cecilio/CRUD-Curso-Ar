package com.dbserver.crud_curso.domain.alunoCurso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlunoCursoRepository extends JpaRepository<AlunoCurso, Long>{
    Optional<AlunoCurso> findByAlunoIdAndCursoId(Long alunoId, Long cursoId);

    Page<AlunoCurso> findAllByCursoId(Long cursoId, Pageable pageable);

    List<AlunoCurso> findAllByAlunoId(Long alunoId);

    Optional<AlunoCurso> findByAlunoIdAndCursoIdAndDesativadaFalse(Long alunoId, Long cursoId);
    Optional<AlunoCurso> findByAlunoIdAndCursoIdAndDesativadaTrue(Long alunoId, Long cursoId);

    Page<AlunoCurso> findAllByCursoIdAndDesativadaFalse(Long cursoId, Pageable pageable);
}
