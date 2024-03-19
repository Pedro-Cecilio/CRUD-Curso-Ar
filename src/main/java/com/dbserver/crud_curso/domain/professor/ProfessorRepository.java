package com.dbserver.crud_curso.domain.professor;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long>{
        Optional<Professor> findByEmail(String email);

        Optional<Professor> findByIdAndDesativadaFalse(Long professorId);

        Page<Professor> findAllByDesativadaFalse(Pageable pageable);

        Optional<Professor> findByIdAndDesativadaTrue(Long professorId);

}
