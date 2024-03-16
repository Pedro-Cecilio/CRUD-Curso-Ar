package com.dbserver.crud_curso.domain.aluno;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long>{
    Optional<Aluno> findByEmail(String email);
}
