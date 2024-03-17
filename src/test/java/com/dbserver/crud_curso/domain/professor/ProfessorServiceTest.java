package com.dbserver.crud_curso.domain.professor;

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

import com.dbserver.crud_curso.domain.aluno.AlunoRepository;
import com.dbserver.crud_curso.domain.professor.dto.AtualizarDadosProfessorDto;
import com.dbserver.crud_curso.domain.professor.dto.CriarProfessorDto;
import com.dbserver.crud_curso.domain.professor.dto.ProfessorRespostaDto;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ProfessorServiceTest {
    @InjectMocks
    private ProfessorService professorService;

    @Mock
    private ProfessorService professorServiceMock;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private Pageable pageable;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private CriarProfessorDto criarProfessorDto;
    private AtualizarDadosProfessorDto atualizarDadosProfessorDtoTodosDados;
    private AtualizarDadosProfessorDto atualizarDadosProfessorDtoUmDado;
    private Professor professorMock;

    @BeforeEach
    void prepararTeste() {
        this.criarProfessorDto = new CriarProfessorDto(
                "exemplo@email.com",
                "senha123",
                "João",
                "Silva",
                25L,
                "BACHAREL");

        this.atualizarDadosProfessorDtoTodosDados = new AtualizarDadosProfessorDto(
                "novasenha123",
                "Pedro",
                "Cecilio",
                23L,
                "MESTRE");
        this.atualizarDadosProfessorDtoUmDado = new AtualizarDadosProfessorDto(
                null,
                "Pedro",
                null,
                null,
                null);
        this.professorMock = new Professor(criarProfessorDto);
    }

    @Test
    @DisplayName("Deve ser possível criar um professor corretamente")
    void givenTenhoUmCriarProfessorDtoWhenExecutoMetoCriarProfessorThenCriarProfessorERetornarDadosCriados() {
        when(this.professorRepository.findByEmail(this.criarProfessorDto.email())).thenReturn(Optional.empty());
        when(this.alunoRepository.findByEmail(this.criarProfessorDto.email())).thenReturn(Optional.empty());

        Professor professor = this.professorService.criarProfessor(this.criarProfessorDto);
        verify(this.professorRepository).save(professor);
        assertEquals(this.criarProfessorDto.email(), professor.getEmail());
        assertTrue(this.passwordEncoder.matches(this.criarProfessorDto.senha(), professor.getSenha()));
        assertEquals(this.criarProfessorDto.nome(), professor.getNome());
        assertEquals(this.criarProfessorDto.sobrenome(), professor.getSobrenome());
        assertEquals(this.criarProfessorDto.idade(), professor.getIdade());
        assertEquals(this.criarProfessorDto.grauAcademico(), professor.getGrauAcademico().toString());
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar um professor com um email existente")
    void givenTenhoUmCriarProfessorDtoComEmailQueJaExisteWhenExecutoMetoCriarAlunoThenLancarUmErro() {
        when(this.professorRepository.findByEmail(this.criarProfessorDto.email())).thenReturn(Optional.of(this.professorMock));
        when(this.alunoRepository.findByEmail(this.criarProfessorDto.email())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> this.professorService.criarProfessor(this.criarProfessorDto));
    }

    private static Stream<Arguments> argumentosDadosInvalidosParaCriarProfessor() {
        return Stream.of(
                Arguments.of("exemploemail.com", "12345678", "João", "Silva", 25L, "BACHAREL"),
                Arguments.of("exemplo@email.com", "1234567", "João", "Silva", 25L, "BACHAREL"),
                Arguments.of("exemplo@email.com", "12345678", "Ju", "Silva", 25L, "BACHAREL"),
                Arguments.of("exemplo@email.com", "12345678", "João", " ", 25L, "BACHAREL"),
                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", 6L, "BACHAREL"),
                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", 25L, "PHD"));
    }

    @ParameterizedTest
    @MethodSource("argumentosDadosInvalidosParaCriarProfessor")
    @DisplayName("Deve falhar ao tentar criar um professor com dados incorretos")
    void givenTenhoUmCriarProfessorDtoComDadosInvalidosWhenExecutoCriarProfessorThenLancarUmErro(String email, String senha,
            String nome, String sobrenome, Long idade, String grauAcademico) {
        CriarProfessorDto criarProfessorDto2 = new CriarProfessorDto(email, senha, nome, sobrenome, idade, grauAcademico);
        when(this.professorRepository.findByEmail(criarProfessorDto2.email())).thenReturn(Optional.empty());
        when(this.alunoRepository.findByEmail(criarProfessorDto2.email())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> this.professorService.criarProfessor(criarProfessorDto2));
    }

    @Test
    @DisplayName("Deve ser possível atualizar os dados de um professor")
    void givenTenhoUmAtualizarDadosProfessorDtoWhenExecutoMetodoParaAtualizarThenAtualizarDados() {
        when(this.professorRepository.findById(1L)).thenReturn(Optional.of(this.professorMock));

        Professor professor = this.professorService.atualizarProfessor(atualizarDadosProfessorDtoTodosDados, 1L);

        assertEquals(this.atualizarDadosProfessorDtoTodosDados.nome(), professor.getNome());
        assertTrue(this.passwordEncoder.matches(this.atualizarDadosProfessorDtoTodosDados.senha(), professor.getSenha()));
        assertEquals(this.atualizarDadosProfessorDtoTodosDados.sobrenome(), professor.getSobrenome());
        assertEquals(this.atualizarDadosProfessorDtoTodosDados.idade(), professor.getIdade());
        assertEquals(this.atualizarDadosProfessorDtoTodosDados.grauAcademico(), professor.getGrauAcademico().toString());
    }

    @Test
    @DisplayName("Deve ser possível atualizar somente um dado de um professor")
    void givenTenhoUmAtualizarDadosProfessorDtoComApenasUmDadoWhenExecutoMetodoParaAtualizarThenAtualizarDados() {
        when(this.professorRepository.findById(1L)).thenReturn(Optional.of(this.professorMock));

        Professor professor = this.professorService.atualizarProfessor(atualizarDadosProfessorDtoUmDado, 1L);

        assertEquals(this.atualizarDadosProfessorDtoUmDado.nome(), professor.getNome());
        assertNotEquals(this.atualizarDadosProfessorDtoUmDado.senha(), professor.getSenha());
        assertNotEquals(this.atualizarDadosProfessorDtoUmDado.sobrenome(), professor.getSobrenome());
        assertNotEquals(this.atualizarDadosProfessorDtoUmDado.idade(), professor.getIdade());
        assertNotEquals(this.atualizarDadosProfessorDtoUmDado.grauAcademico(), professor.getGrauAcademico().toString());
    }
    @Test
    @DisplayName("Deve falhar ao tentar atualizar um professor inexistete")
    void givenTenhoUmAtualizarDadosProfessorDtoEProfessorIdInexistenteWhenExecutoMetodoParaAtualizarThenLancarErro() {
        when(this.professorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> this.professorService.atualizarProfessor(atualizarDadosProfessorDtoUmDado, 1L));
    }

    private static Stream<Arguments> argumentosDadosInvalidosParaAtualizarAluno() {
        return Stream.of(
                Arguments.of("8765432", "Pedro", "Cecilio", 23L, "MESTRE"),
                Arguments.of("87654321", "Ju", "Cecilio", 23L, "MESTRE"),
                Arguments.of("87654321", "Pedro", " ", 23L, "MESTRE"),
                Arguments.of("87654321", "Pedro", "Cecilio", 111L, "MESTRE"),
                Arguments.of("87654321", "Pedro", "Cecilio", 23L, "PHD"));
    }

    @ParameterizedTest
    @MethodSource("argumentosDadosInvalidosParaAtualizarAluno")
    @DisplayName("Deve falhar ao tentar atualizar um aluno com dados incorretos")
    void givenTenhoUmAtualizarDadosAlunoDtoComDadosInvalidosWhenExecutoAtualizarAlunoThenLancarUmErro(String senha,
            String nome, String sobrenome, Long idade, String grauAcademico) {
        when(this.professorRepository.findById(1L)).thenReturn(Optional.of(this.professorMock));
        AtualizarDadosProfessorDto atualizarDadosProfessorDto2 = new AtualizarDadosProfessorDto(senha, nome, sobrenome, idade,
                grauAcademico);

        assertThrows(IllegalArgumentException.class,
                () -> this.professorService.atualizarProfessor(atualizarDadosProfessorDto2, 1L));
    }

    @Test
    @DisplayName("Deve ser possível deletar um professor")
    void givenTenhoUmProfessorIdWhenExecutoMetodoParaDeletarProfessorThenDeletaAluno() {
        when(this.professorRepository.findById(1L)).thenReturn(Optional.of(this.professorMock));

        this.professorService.deletarProfessor(1L);

        verify(this.professorRepository).delete(this.professorMock);
    }

    @Test
    @DisplayName("Não deve ser possível deletar um professor inexistente")
    void givenTenhoUmProfessorIdQueNaoExisteWhenExecutoMetodoParaDeletarProfessorThenLancarUmErro() {
        when(this.professorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> this.professorService.deletarProfessor(1L));
    }

    @Test
    @DisplayName("Deve ser possível listar todos professores")
    void givenPossuoProfessoresCadastradosWhenExecutoMetodoParaListarTodosProfessoresThenRetornarListaDeProfessores() {
        List<Professor> listaDeProfessores = List.of(this.professorMock, this.professorMock);
        when(this.professorRepository.findAll(pageable)).thenReturn(new PageImpl<>(listaDeProfessores));

        List<ProfessorRespostaDto> resposta = this.professorService.listarTodosProfessores(pageable);

        assertEquals(listaDeProfessores.size(), resposta.size());
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver professores cadastrados, ao listar todos professores")
    void givenNaoPossuoProfessoresCadastradosWhenExecutoMetodoParaListarTodosProfessoresThenRetornarListaVazia() {
        List<Professor> listaDeProfessores = List.of();
        when(this.professorRepository.findAll(pageable)).thenReturn(new PageImpl<>(listaDeProfessores));

        List<ProfessorRespostaDto> resposta = this.professorService.listarTodosProfessores(pageable);

        assertEquals(listaDeProfessores.size(), resposta.size());
    }

    @Test
    @DisplayName("Deve ser possivel buscar aluno pelo id")
    void givenPossuoProfessorIdWhenExecutoMetodoParaPegarProfessorThenRetornarProfessor() {
        when(this.professorRepository.findById(1L)).thenReturn(Optional.of(this.professorMock));

        ProfessorRespostaDto resposta = this.professorService.pegarProfessor(1L);

        assertEquals(this.professorMock.getNome(), resposta.nome());
        assertEquals(this.professorMock.getSobrenome(), resposta.sobrenome());
        assertEquals(this.professorMock.getIdade(), resposta.idade());
        assertEquals(this.professorMock.getGrauAcademico(), resposta.grauAcademico());
    }

    @Test
    @DisplayName("Deve lançar um erro ao buscar pelo id um professor que não existe")
    void givenPossuoProfessorIdInexistenteWhenExecutoMetodoParaPegarProfessorThenLancarUmErro() {
        when(this.professorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> this.professorService.pegarProfessor(1L));
    }

    @Test
    @DisplayName("Deve retornar false ao não encontrar o email")
    void givenPossuoUmEmailNãoExistenteWhenExecutoVerificarSeEmailExisteThenRetornarFalse() {
        when(this.professorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(this.alunoRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertFalse(this.professorService.verificarSeEmailExiste(anyString()));
    }
    @Test
    @DisplayName("Deve retornar true ao encontrar o email")
    void givenPossuoUmEmailExistenteWhenExecutoVerificarSeEmailExisteThenRetornarTrue() {
        when(this.professorRepository.findByEmail(anyString())).thenReturn(Optional.of(this.professorMock));
        when(this.alunoRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertTrue(this.professorService.verificarSeEmailExiste(anyString()));
    }



}
