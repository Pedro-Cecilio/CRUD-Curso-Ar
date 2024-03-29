package com.dbserver.crud_curso.controller.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dbserver.crud_curso.domain.aluno.Aluno;
import com.dbserver.crud_curso.domain.professor.Professor;


public class TesteUtils {
    public static Aluno login(Aluno aluno) {
        var auth = new UsernamePasswordAuthenticationToken(aluno, null,
                aluno.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return aluno;
    }
    public static Professor login(Professor professor) {
        var auth = new UsernamePasswordAuthenticationToken(professor, null,
                professor.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return professor;
    }
}