package com.dbserver.crud_curso.domain.aluno;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.Collection;
import com.dbserver.crud_curso.domain.aluno.dto.AtualizarDadosAlunoDto;
import com.dbserver.crud_curso.domain.aluno.dto.CriarAlunoDto;
import com.dbserver.crud_curso.domain.alunoCurso.AlunoCurso;
import com.dbserver.crud_curso.domain.enums.GrauEscolaridade;
import com.dbserver.crud_curso.domain.pessoa.Pessoa;
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
public class Aluno extends Pessoa {
    private static final SimpleGrantedAuthority autoridade = new SimpleGrantedAuthority("ALUNO");

    @Column(nullable = false)
    private GrauEscolaridade grauEscolaridade;

    @JsonIgnore
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.REMOVE)
    private List<AlunoCurso> cursosCadastrado;

    public Aluno(String email, String senha, String nome, String sobrenome,
            Long idade, String grauEscolaridade) {
        super(email, senha, nome, sobrenome, idade);
        setGrauEscolaridade(grauEscolaridade);
    }

    public Aluno(CriarAlunoDto dto) {
        super(dto.email(), dto.senha(), dto.nome(), dto.sobrenome(), dto.idade());
        this.setGrauEscolaridade(dto.grauEscolaridade());
    }

    public void atualizarDadosAluno(AtualizarDadosAlunoDto dto) {
        super.atualizarDados(dto.senha(), dto.nome(), dto.sobrenome(), dto.idade());
        this.setGrauEscolaridade(
                dto.grauEscolaridade() != null && !dto.grauEscolaridade().isEmpty() ? dto.grauEscolaridade()
                        : this.grauEscolaridade.toString());
    }

    public void setGrauEscolaridade(String grauEscolaridade) {
        try {
            this.grauEscolaridade = GrauEscolaridade.valueOf(grauEscolaridade);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Grau de escolaridade inválido");
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(autoridade);
    }
}
