package com.dbserver.crud_curso.domain.pessoa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.dbserver.crud_curso.utils.Utils;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
public abstract class Pessoa implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @Size(min = 8, message = "Senha deve conter no mínimo 8 caracteres.")
    private String senha;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String sobrenome;

    @Column(nullable = false)
    private Long idade;

    @Setter
    @Column(nullable = false)
    private boolean desativada = false;



    @Transient
    private List<SimpleGrantedAuthority> autoridades = new ArrayList<>();

    public Pessoa(String email, String senha, SimpleGrantedAuthority autoridade, String nome, String sobrenome,
            Long idade) {
        this.setEmail(email);
        this.setSenha(senha);
        this.autoridades.add(autoridade);
        this.setNome(nome);
        this.setSobrenome(sobrenome);
        this.setIdade(idade);
    }

    protected void atualizarDados(String senha, String nome, String sobrenome,
            Long idade) {
        setSenha(senha != null && !senha.isEmpty() ? senha : this.senha);
        setNome(nome != null && !nome.isEmpty() ? nome : this.nome);
        setSobrenome(sobrenome != null && !sobrenome.isEmpty() ? sobrenome : this.sobrenome);
        setIdade(idade != null ? idade : this.idade);
    }

    public void setEmail(String email) {
        if(!Utils.validarRegex(Utils.REGEX_EMAIL, email)){
            throw new IllegalArgumentException("Email com formato inválido");
        }
        this.email = email;
    }

    public void setSenha(String senha) {
        Utils utils = new Utils();
        if(utils.validarSenha(senha, this.senha)) return;
        if (senha == null)
            throw new IllegalArgumentException("Senha deve ser informada");
        if (senha.trim().length() < 8)
            throw new IllegalArgumentException("Senha deve conter 8 caracteres no mínimo");
        
        this.senha = utils.encriptarSenha(senha);
    }

    public void setNome(String nome) {
        if (nome == null)
            throw new IllegalArgumentException("Nome deve ser informado");
        if (nome.trim().length() < 3 || nome.trim().length() > 20)
            throw new IllegalArgumentException("Nome deve conter 3 caracteres no mínimo e 20 no máximo");
        this.nome = nome.trim();
    }

    public void setSobrenome(String sobrenome) {
        if (sobrenome == null)
            throw new IllegalArgumentException("Sobrenome deve ser informado");
        if (sobrenome.trim().length() < 2 || sobrenome.trim().length() > 20)
            throw new IllegalArgumentException("Sobrenome deve conter 2 caracteres no mínimo e 20 no máximo");
        this.sobrenome = sobrenome.trim();
    }

    public void setIdade(Long idade) {
        if (idade == null)
            throw new IllegalArgumentException("idade deve ser informada");
        if (idade < 7 || idade > 110)
            throw new IllegalArgumentException("Idade deve ser maior do que 6 e menor que 110");
        this.idade = idade;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
