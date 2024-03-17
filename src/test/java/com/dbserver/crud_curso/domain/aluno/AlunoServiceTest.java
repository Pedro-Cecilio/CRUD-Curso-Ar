package com.dbserver.crud_curso.domain.aluno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.dbserver.crud_curso.domain.aluno.dto.AlunoRespostaDto;
import com.dbserver.crud_curso.domain.aluno.dto.AtualizarDadosAlunoDto;
import com.dbserver.crud_curso.domain.aluno.dto.CriarAlunoDto;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AlunoServiceTest {
    @InjectMocks
    private AlunoService alunoService;

    @Mock
    private AlunoService alunoServiceMock;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private Pageable pageable;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private CriarAlunoDto criarAlunoDto;
    private AtualizarDadosAlunoDto atualizarDadosAlunoDtoTodosDados;
    private AtualizarDadosAlunoDto atualizarDadosAlunoDtoUmDado;
    private Aluno alunoMock;

    @BeforeEach
    void prepararTeste() {
        this.criarAlunoDto = new CriarAlunoDto(
                "exemplo@email.com",
                "senha123",
                "João",
                "Silva",
                25L,
                "ENSINO_SUPERIOR_COMPLETO");

        this.atualizarDadosAlunoDtoTodosDados = new AtualizarDadosAlunoDto(
                "novasenha123",
                "Pedro",
                "Cecilio",
                23L,
                "ENSINO_SUPERIOR_INCOMPLETO");
        this.atualizarDadosAlunoDtoUmDado = new AtualizarDadosAlunoDto(
                null,
                "Pedro",
                null,
                null,
                null);
        this.alunoMock = new Aluno(criarAlunoDto);
    }

    @Test
    @DisplayName("Deve ser possível criar um aluno corretamente")
    void givenTenhoUmCriarAlunoDtoWhenExecutoMetoCriarAlunoComOCriarAlunoDtoThenCriarAlunoERetornar() {
        when(this.professorRepository.findByEmail(this.criarAlunoDto.email())).thenReturn(Optional.empty());
        when(this.alunoRepository.findByEmail(this.criarAlunoDto.email())).thenReturn(Optional.empty());

        Aluno aluno = this.alunoService.criarAluno(this.criarAlunoDto);
        verify(this.alunoRepository).save(aluno);
        assertEquals(this.criarAlunoDto.email(), aluno.getEmail());
        assertTrue(this.passwordEncoder.matches(this.criarAlunoDto.senha(), aluno.getSenha()));
        assertEquals(this.criarAlunoDto.nome(), aluno.getNome());
        assertEquals(this.criarAlunoDto.sobrenome(), aluno.getSobrenome());
        assertEquals(this.criarAlunoDto.idade(), aluno.getIdade());
        assertEquals(this.criarAlunoDto.grauEscolaridade(), aluno.getGrauEscolaridade().toString());
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar um aluno com um email existente")
    void givenTenhoUmCriarAlunoDtoComEmailQueJaExisteWhenExecutoMetoCriarAlunoThenLancarUmErro() {
        when(this.professorRepository.findByEmail(this.criarAlunoDto.email())).thenReturn(Optional.empty());
        when(this.alunoRepository.findByEmail(this.criarAlunoDto.email())).thenReturn(Optional.of(this.alunoMock));

        assertThrows(IllegalArgumentException.class, () -> this.alunoService.criarAluno(this.criarAlunoDto));
    }

    private static Stream<Arguments> argumentosDadosInvalidosParaCriarAluno() {
        return Stream.of(
                Arguments.of("exemploemail.com", "12345678", "João", "Silva", 25L, "ENSINO_SUPERIOR_COMPLETO"),
                Arguments.of("exemplo@email.com", "1234567", "João", "Silva", 25L, "ENSINO_SUPERIOR_COMPLETO"),
                Arguments.of("exemplo@email.com", "12345678", "Ju", "Silva", 25L, "ENSINO_SUPERIOR_COMPLETO"),
                Arguments.of("exemplo@email.com", "12345678", "João", " ", 25L, "ENSINO_SUPERIOR_COMPLETO"),
                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", 6L, "ENSINO_SUPERIOR_COMPLETO"),
                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", 25L, "PHD"));
    }

    @ParameterizedTest
    @MethodSource("argumentosDadosInvalidosParaCriarAluno")
    @DisplayName("Deve falhar ao tentar criar um aluno com dados incorretos")
    void givenTenhoUmCriarAlunoDtoComDadosInvalidosWhenExecutoCriarAlunoThenLancarUmErro(String email, String senha,
            String nome, String sobrenome, Long idade, String grauEscolaridade) {
        CriarAlunoDto criarAlunoDto2 = new CriarAlunoDto(email, senha, nome, sobrenome, idade, grauEscolaridade);
        when(this.professorRepository.findByEmail(criarAlunoDto2.email())).thenReturn(Optional.empty());
        when(this.alunoRepository.findByEmail(criarAlunoDto2.email())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> this.alunoService.criarAluno(criarAlunoDto2));
    }

    @Test
    @DisplayName("Deve ser possível atualizar os dados de um aluno")
    void givenTenhoUmAtualizarDadosAlunoDtoWhenExecutoMetodoParaAtualizarThenAtualizarDados() {
        when(this.alunoRepository.findById(1L)).thenReturn(Optional.of(this.alunoMock));

        Aluno aluno = this.alunoService.atualizarAluno(atualizarDadosAlunoDtoTodosDados, 1L);

        assertEquals(this.atualizarDadosAlunoDtoTodosDados.nome(), aluno.getNome());
        assertTrue(this.passwordEncoder.matches(this.atualizarDadosAlunoDtoTodosDados.senha(), aluno.getSenha()));
        assertEquals(this.atualizarDadosAlunoDtoTodosDados.sobrenome(), aluno.getSobrenome());
        assertEquals(this.atualizarDadosAlunoDtoTodosDados.idade(), aluno.getIdade());
        assertEquals(this.atualizarDadosAlunoDtoTodosDados.grauEscolaridade(), aluno.getGrauEscolaridade().toString());
    }

    @Test
    @DisplayName("Deve ser possível atualizar somente um dado de um aluno")
    void givenTenhoUmAtualizarDadosAlunoDtoComApenasUmDadoWhenExecutoMetodoParaAtualizarThenAtualizarDados() {
        when(this.alunoRepository.findById(1L)).thenReturn(Optional.of(this.alunoMock));

        Aluno aluno = this.alunoService.atualizarAluno(atualizarDadosAlunoDtoUmDado, 1L);

        assertEquals(this.atualizarDadosAlunoDtoUmDado.nome(), aluno.getNome());
        assertNotEquals(this.atualizarDadosAlunoDtoUmDado.senha(), aluno.getSenha());
        assertNotEquals(this.atualizarDadosAlunoDtoUmDado.sobrenome(), aluno.getSobrenome());
        assertNotEquals(this.atualizarDadosAlunoDtoUmDado.idade(), aluno.getIdade());
        assertNotEquals(this.atualizarDadosAlunoDtoUmDado.grauEscolaridade(), aluno.getGrauEscolaridade().toString());
    }

    @Test
    @DisplayName("Deve falhar ao tentar atualizar um aluno inexistete")
    void givenTenhoUmAtualizarDadosAlunoDtoEAlunoIdInexistenteWhenExecutoMetodoParaAtualizarThenLancarErro() {
        when(this.alunoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> this.alunoService.atualizarAluno(atualizarDadosAlunoDtoTodosDados, 1L));
    }

    private static Stream<Arguments> argumentosDadosInvalidosParaAtualizarAluno() {
        return Stream.of(
                Arguments.of("8765432", "Pedro", "Cecilio", 23L, "ENSINO_SUPERIOR_INCOMPLETO"),
                Arguments.of("87654321", "Ju", "Cecilio", 23L, "ENSINO_SUPERIOR_INCOMPLETO"),
                Arguments.of("87654321", "Pedro", " ", 23L, "ENSINO_SUPERIOR_INCOMPLETO"),
                Arguments.of("87654321", "Pedro", "Cecilio", 111L, "ENSINO_SUPERIOR_INCOMPLETO"),
                Arguments.of("87654321", "Pedro", "Cecilio", 23L, "PHD"));
    }

    @ParameterizedTest
    @MethodSource("argumentosDadosInvalidosParaAtualizarAluno")
    @DisplayName("Deve falhar ao tentar atualizar um aluno com dados incorretos")
    void givenTenhoUmAtualizarDadosAlunoDtoComDadosInvalidosWhenExecutoAtualizarAlunoThenLancarUmErro(String senha,
            String nome, String sobrenome, Long idade, String grauEscolaridade) {
        when(this.alunoRepository.findById(1L)).thenReturn(Optional.of(this.alunoMock));
        AtualizarDadosAlunoDto AtualizarDadosAlunoDto2 = new AtualizarDadosAlunoDto(senha, nome, sobrenome, idade,
                grauEscolaridade);

        assertThrows(IllegalArgumentException.class,
                () -> this.alunoService.atualizarAluno(AtualizarDadosAlunoDto2, 1L));
    }

    @Test
    @DisplayName("Deve ser possível deletar um aluno")
    void givenTenhoUmAlunoIdWhenExecutoMetodoParaDeletarAlunoThenDeletaAluno() {
        when(this.alunoRepository.findById(1L)).thenReturn(Optional.of(this.alunoMock));

        this.alunoService.deletarAluno(1L);

        verify(this.alunoRepository).delete(this.alunoMock);
    }

    @Test
    @DisplayName("Não deve ser possível deletar um aluno inexistente")
    void givenTenhoUmAlunoIdQueNaoExisteWhenExecutoMetodoParaDeletarAlunoThenLancarUmErro() {
        when(this.alunoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> this.alunoService.deletarAluno(1L));
    }

    @Test
    @DisplayName("Deve ser possível listar todos alunos")
    void givenPossuoAlunosCadastradosWhenExecutoMetodoParaListarTodosAlunosThenRetornarListaDeAlunos() {
        List<Aluno> listaDeAlunos = List.of(this.alunoMock, this.alunoMock);
        when(this.alunoRepository.findAll(pageable)).thenReturn(new PageImpl<>(listaDeAlunos));

        List<AlunoRespostaDto> resposta = this.alunoService.listarTodosAlunos(pageable);

        assertEquals(listaDeAlunos.size(), resposta.size());
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver alunos cadastrados, ao listar todos alunos")
    void givenNaoPossuoAlunosCadastradosWhenExecutoMetodoParaListarTodosAlunosThenRetornarListaVazia() {
        List<Aluno> listaDeAlunos = List.of();
        when(this.alunoRepository.findAll(pageable)).thenReturn(new PageImpl<>(listaDeAlunos));

        List<AlunoRespostaDto> resposta = this.alunoService.listarTodosAlunos(pageable);

        assertEquals(listaDeAlunos.size(), resposta.size());
    }

    @Test
    @DisplayName("Deve ser possivel buscar aluno pelo id")
    void givenPossuoAlunoIdWhenExecutoMetodoParaPegarAlunoThenRetornarAluno() {
        when(this.alunoRepository.findById(1L)).thenReturn(Optional.of(this.alunoMock));

        AlunoRespostaDto resposta = this.alunoService.pegarAluno(1L);

        assertEquals(this.alunoMock.getNome(), resposta.nome());
        assertEquals(this.alunoMock.getSobrenome(), resposta.sobrenome());
        assertEquals(this.alunoMock.getIdade(), resposta.idade());
        assertEquals(this.alunoMock.getGrauEscolaridade(), resposta.grauEscolaridade());
    }

    @Test
    @DisplayName("Deve lançar um erro ao buscar pelo id um aluno que não existe")
    void givenPossuoAlunoIdInexistenteWhenExecutoMetodoParaPegarAlunoThenLancarUmErro() {
        when(this.alunoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> this.alunoService.pegarAluno(1L));
    }

    @Test
    @DisplayName("Deve retornar false ao não encontrar o email")
    void givenPossuoUmEmailNãoExistenteWhenExecutoVerificarSeEmailExisteThenRetornarFalse() {
        when(this.professorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(this.alunoRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertFalse(this.alunoService.verificarSeEmailExiste(anyString()));
    }
    @Test
    @DisplayName("Deve retornar true ao encontrar o email")
    void givenPossuoUmEmailExistenteWhenExecutoVerificarSeEmailExisteThenRetornarTrue() {
        when(this.professorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(this.alunoRepository.findByEmail(anyString())).thenReturn(Optional.of(this.alunoMock));

        assertTrue(this.alunoService.verificarSeEmailExiste(anyString()));
    }



}
