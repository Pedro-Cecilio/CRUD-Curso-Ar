package com.dbserver.crud_curso.domain.alunoCurso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlunoCursoRepository extends JpaRepository<AlunoCurso, Long>{
    Optional<AlunoCurso> findByAlunoIdAndCursoId(Long alunoId, Long cursoId);

    Page<AlunoCurso> findAllByCursoId(Long cursoId, Pageable pageable);
}
