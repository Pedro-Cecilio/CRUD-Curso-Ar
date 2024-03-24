package com.dbserver.crud_curso.domain.alunoCurso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dbserver.crud_curso.domain.enums.StatusMatricula;

import java.util.List;
import java.util.Optional;

public interface AlunoCursoRepository extends JpaRepository<AlunoCurso, Long>{
    Optional<AlunoCurso> findByAlunoIdAndCursoId(Long alunoId, Long cursoId);

    Page<AlunoCurso> findAllByCursoId(Long cursoId, Pageable pageable);

    List<AlunoCurso> findAllByAlunoId(Long alunoId);

    List<AlunoCurso> findAllByCursoIdAndStatusMatricula(Long cursoId, StatusMatricula statusMatricula, Pageable pageable);

    List<AlunoCurso> findAllByCursoId(Long cursoId);
}
