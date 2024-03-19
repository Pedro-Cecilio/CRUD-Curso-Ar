package com.dbserver.crud_curso.domain.autenticacao;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

import com.dbserver.crud_curso.domain.aluno.Aluno;
import com.dbserver.crud_curso.domain.aluno.AlunoRepository;
import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;
import com.dbserver.crud_curso.infra.security.TokenService;

@Service
public class AutenticacaoService {
    private ProfessorRepository professorRepository;
    private AlunoRepository alunoRepository;
    private TokenService tokenService;
    private PasswordEncoder passwordEnconder;

    public AutenticacaoService(ProfessorRepository professorRepository, TokenService tokenService,
            PasswordEncoder passwordEnconder, AlunoRepository alunoRepository) {
        this.professorRepository = professorRepository;
        this.tokenService = tokenService;
        this.passwordEnconder = passwordEnconder;
        this.alunoRepository = alunoRepository;
    }

    public String autenticar(Autenticacao autenticacao) {
        Optional<Professor> professor = professorRepository.findByEmail(autenticacao.getEmail());
        if (professor.isPresent()) {
            return professor
                    .filter(pessoa -> this.compararSenha(autenticacao.getSenha(), pessoa.getSenha()))
                    .map(pessoa -> tokenService.gerarToken(pessoa))
                    .orElseThrow(() -> new BadCredentialsException("Dados de login inválidos"));
        }
        Optional<Aluno> aluno = alunoRepository.findByEmail(autenticacao.getEmail());
        if (aluno.isPresent()) {
            return aluno
                    .filter(pessoa -> this.compararSenha(autenticacao.getSenha(), pessoa.getSenha()))
                    .map(pessoa -> tokenService.gerarToken(pessoa))
                    .orElseThrow(() -> new BadCredentialsException("Dados de login inválidos"));
        }
        throw new BadCredentialsException("Dados de login inválidos");
    }

    public boolean compararSenha(String senhaEsperada, String atual) {
        return this.passwordEnconder.matches(senhaEsperada, atual);
    }
}
