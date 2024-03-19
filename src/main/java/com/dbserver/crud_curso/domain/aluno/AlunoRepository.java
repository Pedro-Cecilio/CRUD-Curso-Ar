package com.dbserver.crud_curso.domain.aluno;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long>{
    Optional<Aluno> findByEmail(String email);

    Optional<Aluno> findByIdAndDesativadaFalse(Long id);
    Optional<Aluno> findByIdAndDesativadaTrue(Long id);

    Page<Aluno> findAllByDesativadaFalse(Pageable pageable);
}
