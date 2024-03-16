package com.dbserver.crud_curso.infra.security;

import java.io.IOException;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dbserver.crud_curso.domain.aluno.Aluno;
import com.dbserver.crud_curso.domain.aluno.AlunoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;
import com.dbserver.crud_curso.infra.excecao.RespostaErro;
import com.dbserver.crud_curso.infra.excecao.novas.ValidarJwtExeption;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private TokenService tokenService;
    private AlunoRepository alunoRepository;
    private ProfessorRepository professorRepository;

    public SecurityFilter(TokenService tokenService, AlunoRepository alunoRepository,
            ProfessorRepository professorRepository) {
        this.tokenService = tokenService;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var token = this.recuperarToken(request);

        if (token != null) {
            try {
                String email = tokenService.validarToken(token);

                Optional<Aluno> aluno = this.alunoRepository.findByEmail(email);
                Optional<Professor> professor = this.professorRepository.findByEmail(email);
                
                if (aluno.isPresent()) {
                    var authentication = new UsernamePasswordAuthenticationToken(aluno.get(), null,
                            aluno.get().getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                if (professor.isPresent()) {
                    var authentication = new UsernamePasswordAuthenticationToken(professor.get(), null,
                            professor.get().getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

                if(aluno.isEmpty() && professor.isEmpty()) {
                    this.respostaErro(response);
                    return;
                }
            } catch (ValidarJwtExeption e) {
                this.respostaErro(response);
                return;
            }
        }
        filterChain.doFilter(request, response);

    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }

    public void respostaErro(HttpServletResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RespostaErro responseError = new RespostaErro("Token inválido");
        String tokenErrorResponse = mapper.writeValueAsString(responseError);

        response.setStatus(401);
        response.addHeader("Content-type", "application/json");
        response.getWriter().write(tokenErrorResponse);
    }
}