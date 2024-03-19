package com.dbserver.crud_curso.domain.alunoCurso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.dbserver.crud_curso.domain.aluno.Aluno;
import com.dbserver.crud_curso.domain.aluno.AlunoRepository;
import com.dbserver.crud_curso.domain.curso.Curso;
import com.dbserver.crud_curso.domain.curso.CursoRepository;
import com.dbserver.crud_curso.domain.enums.StatusMatricula;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AlunoCursoServiceTest {

    @InjectMocks
    private AlunoCursoService alunoCursoService;

    @Mock
    private AlunoCursoRepository alunoCursoRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private Pageable pageable;

    private Curso cursoEnsinoMedioIncompletoMock;
    private Curso cursoEnsinoSuperiorCompletoMock;
    private Aluno alunoMock;
    private AlunoCurso alunoCursoMock;

    @BeforeEach
    void configuracao() {
        this.alunoMock = new Aluno("exemplo@email.com",
                "senha123",
                "João",
                "Silva",
                25L,
                "ENSINO_SUPERIOR_INCOMPLETO");
        this.cursoEnsinoMedioIncompletoMock = new Curso("Curso de Lógica de Programação",
                6L,
                "ENSINO_MEDIO_INCOMPLETO",
                "BACHAREL");
        this.cursoEnsinoSuperiorCompletoMock = new Curso("Curso de Lógica de Programação",
                6L,
                "ENSINO_SUPERIOR_COMPLETO",
                "BACHAREL");

        this.alunoCursoMock = new AlunoCurso(alunoMock, cursoEnsinoMedioIncompletoMock);
    }

    @Test
    @DisplayName("Deve ser possível cadastrar um aluno em um curso corretamente")
    void givenTenhoUmAlunoIdEUmCursoIdExistentesWhenCadastroAlunoNoCursoThenRetornarCadastroDoAlunoNoCurso() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoEnsinoMedioIncompletoMock));
        when(this.alunoRepository.findByIdAndDesativadaFalse(1L)).thenReturn(Optional.of(this.alunoMock));
        when(this.alunoCursoRepository.findByAlunoIdAndCursoId(1L, 1L)).thenReturn(Optional.empty());

        AlunoCurso resposta = this.alunoCursoService.cadastrarAlunoNoCurso(1L, 1L);

        assertEquals(this.alunoMock.getEmail(), resposta.getAluno().getEmail());
        assertEquals(this.alunoMock.getSenha(), resposta.getAluno().getSenha());
        assertEquals(this.alunoMock.getNome(), resposta.getAluno().getNome());
        assertEquals(this.alunoMock.getSobrenome(), resposta.getAluno().getSobrenome());
        assertEquals(this.alunoMock.getIdade(), resposta.getAluno().getIdade());
        assertEquals(this.alunoMock.getGrauEscolaridade().toString(),
                resposta.getAluno().getGrauEscolaridade().toString());

        assertEquals(this.cursoEnsinoMedioIncompletoMock.getTitulo(), resposta.getCurso().getTitulo());
        assertEquals(this.cursoEnsinoMedioIncompletoMock.getDuracaoMeses(), resposta.getCurso().getDuracaoMeses());
        assertEquals(this.cursoEnsinoMedioIncompletoMock.getGrauAcademicoMinimo().toString(),
                resposta.getCurso().getGrauAcademicoMinimo().toString());
        assertEquals(this.cursoEnsinoMedioIncompletoMock.getGrauEscolarMinimo().toString(),
                resposta.getCurso().getGrauEscolarMinimo().toString());

        assertEquals(StatusMatricula.ATIVO, resposta.getStatusMatricula());
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno em um curso inexistente")
    void givenTenhoUmAlunoIdExistenteEUmCursoIdInexisteteWhenCadastroAlunoNoCursoThenRetornarUmErro() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> this.alunoCursoService.cadastrarAlunoNoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno inexistente em um curso")
    void givenTenhoUmAlunoIdInexistenteEUmCursoIdExistenteWhenCadastroAlunoNoCursoThenRetornarUmErro() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoEnsinoMedioIncompletoMock));
        when(this.alunoRepository.findByIdAndDesativadaFalse(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> this.alunoCursoService.cadastrarAlunoNoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno já cadastrado no curso")
    void givenTenhoUmAlunoJaCadastradoNoCursoWhenCadastroAlunoNoCursoThenRetornarUmErro() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoEnsinoMedioIncompletoMock));
        when(this.alunoRepository.findByIdAndDesativadaFalse(1L)).thenReturn(Optional.of(this.alunoMock));
        when(this.alunoCursoRepository.findByAlunoIdAndCursoId(1L, 1L)).thenReturn(Optional.of(this.alunoCursoMock));

        assertThrows(IllegalArgumentException.class, () -> this.alunoCursoService.cadastrarAlunoNoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno que não possua grau escolar mínimo suficiente")
    void givenTenhoUmAlunoComGrauEscolarInferiorNoCursoWhenCadastroAlunoNoCursoThenRetornarUmErro() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoEnsinoSuperiorCompletoMock));
        when(this.alunoRepository.findByIdAndDesativadaFalse(1L)).thenReturn(Optional.of(this.alunoMock));
        when(this.alunoCursoRepository.findByAlunoIdAndCursoId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> this.alunoCursoService.cadastrarAlunoNoCurso(1L, 1L));
    }

    private static Stream<Arguments> argumentosDadosValidosAtualizarMatricula() {
        return Stream.of(
                Arguments.of("ATIVO"),
                Arguments.of("INATIVO"),
                Arguments.of("FORMADO"));
    }

    @ParameterizedTest
    @MethodSource("argumentosDadosValidosAtualizarMatricula")
    @DisplayName("Deve ser possível atualizar status matricula corretamente")
    void givenAlunoEstaCadastradoNoCursoWhenAtualizoStatusMatriculaCorretamenteThenRetornarAlunoCursoAtualizado(
            String statusMatricula) {
        when(this.alunoCursoRepository.findByAlunoIdAndCursoId(1L, 1L))
                .thenReturn(Optional.of(this.alunoCursoMock));
        AlunoCurso alunoCurso = this.alunoCursoService.atualizarStatusMatricula(1L, 1L, statusMatricula);
        assertEquals(statusMatricula, alunoCurso.getStatusMatricula().toString());
    }

    private static Stream<Arguments> argumentosDadosInalidosAtualizarMatricula() {
        return Stream.of(
                Arguments.of("ABERTO"),
                Arguments.of("FECHADO"),
                Arguments.of("EXPULSO"));
    }

    @ParameterizedTest
    @MethodSource("argumentosDadosInalidosAtualizarMatricula")
    @DisplayName("Deve falhar ao atualizar status matricula com dados inválidos")
    void givenAlunoEstaCadastradoNoCursoWhenAtualizoStatusMatriculaComDadosinválidosThenRetornarLancarUmErro(
            String statusMatricula) {
        when(this.alunoCursoRepository.findByAlunoIdAndCursoId(1L, 1L))
                .thenReturn(Optional.of(this.alunoCursoMock));
        assertThrows(IllegalArgumentException.class,
                () -> this.alunoCursoService.atualizarStatusMatricula(1L, 1L, statusMatricula));
    }

    @Test
    @DisplayName("Deve ser possível listar todos alunos do curso")
    void givenPossuoUmCursoIdWhenListoTodosAlunosDoCursoThenRetornarListaComAlunosDoCurso() {
        List<AlunoCurso> listaDeAlunos = List.of(this.alunoCursoMock, alunoCursoMock);
        when(this.alunoCursoRepository.findAllByCursoId(1L, this.pageable))
                .thenReturn(new PageImpl<>(listaDeAlunos));
        List<AlunoCurso> alunos = this.alunoCursoService.listarTodosAlunosDoCurso(1L, pageable);

        assertEquals(listaDeAlunos.size(), alunos.size());
    }

    @Test
    @DisplayName("Deve ser retonar lista vazia quando não houver nenhum aluno no curso")
    void givenPossuoUmCursoIdDeUmCursoSemALunosWhenListoTodosAlunosDoCursoThenRetornarListaVazia() {
        List<AlunoCurso> listaDeAlunos = List.of();
        when(this.alunoCursoRepository.findAllByCursoId(1L, this.pageable))
                .thenReturn(new PageImpl<>(listaDeAlunos));
        List<AlunoCurso> alunos = this.alunoCursoService.listarTodosAlunosDoCurso(1L, pageable);

        assertEquals(listaDeAlunos.size(), alunos.size());
    }

    @Test
    @DisplayName("Deve ser possivel buscar um aluno do curso")
    void givenPossuoUmCursoIdEAlunoIdWhenBuscoPeloAlunoDoCursoThenRetornarAlunoDoCurso() {
        when(this.alunoCursoRepository.findByAlunoIdAndCursoId(1L, 1L))
                .thenReturn(Optional.of(this.alunoCursoMock));
        AlunoCurso alunoCurso = this.alunoCursoService.buscarAlunoDoCurso(1L, 1L);

        assertEquals(this.alunoMock.getEmail(), alunoCurso.getAluno().getEmail());
        assertEquals(this.alunoMock.getSenha(), alunoCurso.getAluno().getSenha());
        assertEquals(this.alunoMock.getNome(), alunoCurso.getAluno().getNome());
        assertEquals(this.alunoMock.getSobrenome(), alunoCurso.getAluno().getSobrenome());
        assertEquals(this.alunoMock.getIdade(), alunoCurso.getAluno().getIdade());
        assertEquals(this.alunoMock.getGrauEscolaridade().toString(),
                alunoCurso.getAluno().getGrauEscolaridade().toString());
    }

    @Test
    @DisplayName("Deve falhar ao buscar aluno que não está cadastrado no curso")
    void givenPossuoUmCursoIdEAlunoIdInexisteteNocursoWhenBuscoPeloAlunoDoCursoThenlancarUmErro() {
        when(this.alunoCursoRepository.findByAlunoIdAndCursoId(1L, 1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> this.alunoCursoService.buscarAlunoDoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Deve retornar true ao encontrar aluno no curso")
    void givenPossuoUmCursoIdEAlunoIdExistentesWhenVerificoSeAlunoEstaNoCursoThenRetornarTrue() {
        when(this.alunoCursoRepository.findByAlunoIdAndCursoId(1L, 1L)).thenReturn(Optional.of(this.alunoCursoMock));
        assertTrue(this.alunoCursoService.verificarSeAlunoPossuiCadastroNoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Deve retornar false ao não encontrar aluno no curso")
    void givenPossuoUmCursoIdEAlunoIdInexistentesWhenVerificoSeAlunoEstaNoCursoThenRetornarFalse() {
        when(this.alunoCursoRepository.findByAlunoIdAndCursoId(1L, 1L)).thenReturn(Optional.empty());
        assertFalse(this.alunoCursoService.verificarSeAlunoPossuiCadastroNoCurso(1L, 1L));
    }

}
