package com.dbserver.crud_curso.domain.professor;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.dbserver.crud_curso.domain.enums.GrauAcademico;
import com.dbserver.crud_curso.domain.pessoa.Pessoa;
import com.dbserver.crud_curso.domain.professor.dto.AtualizarDadosProfessorDto;
import com.dbserver.crud_curso.domain.professor.dto.CriarProfessorDto;
import com.dbserver.crud_curso.domain.professorCurso.ProfessorCurso;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Professor extends Pessoa {
    private static final SimpleGrantedAuthority autoridade = new SimpleGrantedAuthority("PROFESSOR");
    @Column(nullable = false)
    private GrauAcademico grauAcademico;

    @JsonIgnore
    @OneToMany(mappedBy = "professor", cascade = CascadeType.REMOVE)
    private List<ProfessorCurso> cursosMinistrados;

    public Professor(String email, String senha, String nome, String sobrenome,
            Long idade, String grauAcademico) {
        super(email, senha, nome, sobrenome, idade);
        this.setGrauAcademico(grauAcademico);
    }

    public Professor(CriarProfessorDto dto) {
        super(dto.email(), dto.senha(), dto.nome(), dto.sobrenome(), dto.idade());
        this.setGrauAcademico(dto.grauAcademico());
    }

    public void atualizarDadosProfessor(AtualizarDadosProfessorDto dto) {
        super.atualizarDados(dto.senha(), dto.nome(), dto.sobrenome(), dto.idade());
        this.setGrauAcademico(dto.grauAcademico() != null && !dto.grauAcademico().isEmpty() ? dto.grauAcademico()
                : this.grauAcademico.toString());
    }

    public void setGrauAcademico(String grauAcademico) {
        try {
            this.grauAcademico = GrauAcademico.valueOf(grauAcademico);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Grau acadêmico inválido");
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(autoridade);
    }
}
