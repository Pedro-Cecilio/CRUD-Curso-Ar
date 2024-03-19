package com.dbserver.crud_curso.utils;

import java.util.regex.Pattern;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dbserver.crud_curso.domain.pessoa.Pessoa;

public class Utils {
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static final String REGEX_EMAIL = "^[a-zA-Z0-9._%]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$";
   

    public static boolean validarRegex(String regex, String conteudo) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(conteudo).matches();
    }


    public String encriptarSenha(String senha){
        return this.passwordEncoder.encode(senha);
    }

    public boolean validarSenha(String senhaEsperada, String senhaEncriptada){
        return this.passwordEncoder.matches(senhaEsperada, senhaEncriptada);
    }

    public Long pegarIdDaPessoaLogada() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Pessoa pessoaLogada = (Pessoa) authentication.getPrincipal();
        return pessoaLogada.getId();
    }
}