package com.dbserver.crud_curso.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.IncorrectClaimException;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.dbserver.crud_curso.domain.pessoa.Pessoa;
import com.dbserver.crud_curso.infra.excecao.novas.CriarJwtExeption;
import com.dbserver.crud_curso.infra.excecao.novas.ValidarJwtExeption;


@Service
public class TokenService {
    
    @Value("${api.security.token.senha}")
    private String senha;

    public String gerarToken(Pessoa pessoa) throws CriarJwtExeption {

        try {
            Algorithm algorithm = Algorithm.HMAC256(senha);
            return JWT.create()
                    .withIssuer("CRUD Unidade Ar")
                    .withSubject(pessoa.getUsername())
                    .withClaim("userId", pessoa.getId())
                    .withExpiresAt(this.gerarDataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new CriarJwtExeption("Erro ao gerar Token", e);
        } catch (IllegalArgumentException e) {
            throw new CriarJwtExeption("Erro com os argumentos na geração do token",  e);
        }
    }

    public String validarToken(String token) throws ValidarJwtExeption {
        try {
            Algorithm algorithm = Algorithm.HMAC256(senha);
            return JWT.require(algorithm)
                    .withIssuer("CRUD Unidade Ar")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (AlgorithmMismatchException e) {
            throw new ValidarJwtExeption("Algoritmo JWT não corresponde ao esperado", e);
        } catch (SignatureVerificationException e) {
            throw new ValidarJwtExeption("Assinatura do token JWT inválida", e);
        } catch (TokenExpiredException e) {
            throw new ValidarJwtExeption("Token JWT expirado", e);
        } catch (MissingClaimException e) {
            throw new ValidarJwtExeption("Reivindicação ausente no token JWT", e);
        } catch (IncorrectClaimException e) {
            throw new ValidarJwtExeption("Reivindicação incorreta no token JWT", e);
        }catch(JWTDecodeException e) {
            throw new ValidarJwtExeption("Erro ao validar token: "+ e.getMessage());
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    

}