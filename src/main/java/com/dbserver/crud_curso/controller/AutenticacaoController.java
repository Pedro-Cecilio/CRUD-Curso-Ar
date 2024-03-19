package com.dbserver.crud_curso.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.dbserver.crud_curso.domain.autenticacao.Autenticacao;
import com.dbserver.crud_curso.domain.autenticacao.AutenticacaoService;
import com.dbserver.crud_curso.domain.autenticacao.dto.AutenticacaoRespostaDto;

public class AutenticacaoController {
     private AutenticacaoService autenticacaoService;
    public AutenticacaoController(AutenticacaoService autenticacaoService){
        this.autenticacaoService = autenticacaoService;
    }
    
    @PostMapping
    public ResponseEntity<AutenticacaoRespostaDto> login(@RequestBody Autenticacao autenticacao){
        String token = this.autenticacaoService.autenticar(autenticacao);
        return ResponseEntity.ok(new AutenticacaoRespostaDto(token));
    }
}
