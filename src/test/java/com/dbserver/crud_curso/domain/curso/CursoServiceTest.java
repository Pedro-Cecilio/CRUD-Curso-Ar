package com.dbserver.crud_curso.domain.curso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.stream.Stream;
import java.util.Optional;
import java.util.List;
import java.util.NoSuchElementException;
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
import com.dbserver.crud_curso.domain.curso.dto.AtualizarDadosCursoDto;
import com.dbserver.crud_curso.domain.curso.dto.CriarCursoDto;
import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;
import com.dbserver.crud_curso.domain.professorCurso.ProfessorCurso;
import com.dbserver.crud_curso.domain.professorCurso.ProfessorCursoRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CursoServiceTest {
    @InjectMocks
    private CursoService cursoService;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private ProfessorRepository professorRepository;
    @Mock
    private ProfessorCursoRepository professorCursoRepository;

    @Mock
    private Pageable pageable;

    private Professor professorMock;
    private Curso cursoMock;
    private CriarCursoDto criarCursoDto;
    private AtualizarDadosCursoDto atualizarDadosCursoDtoTodosDados;
    private AtualizarDadosCursoDto atualizarDadosCursoDtoUmDado;

    @BeforeEach
    void prepararTeste() {
        this.criarCursoDto = new CriarCursoDto(
                "Curso de Lógica de Programação",
                6L,
                "ENSINO_MEDIO_INCOMPLETO",
                "BACHAREL");

        this.atualizarDadosCursoDtoTodosDados = new AtualizarDadosCursoDto(
                "Curso de Desenvolvimento Web",
                8L,
                "ENSINO_MEDIO_COMPLETO",
                "LICENCIATURA");
        this.atualizarDadosCursoDtoUmDado = new AtualizarDadosCursoDto(
                "Curso de Desenvolvimento Web",
                null,
                null,
                null);
        this.cursoMock = new Curso(criarCursoDto);

        this.professorMock = new Professor("exemplo@email.com",
                "senha123",
                "João",
                "Silva",
                25L,
                "BACHAREL");
    }

    @Test
    @DisplayName("Deve ser possível criar um curso corretamente")
    void givenTenhoUmCriarCursoDtoWhenExecutoMetoCriarCursoThenCriarCursoERetornar() {
        when(this.professorRepository.findById(1L)).thenReturn(Optional.of(this.professorMock));
        Curso curso = this.cursoService.criarCurso(this.criarCursoDto, 1L);
        verify(this.cursoRepository).save(curso); 
        verify(this.professorCursoRepository).save(any(ProfessorCurso.class));
        assertEquals(this.criarCursoDto.titulo(), curso.getTitulo());
        assertEquals(this.criarCursoDto.duracaoMeses(), curso.getDuracaoMeses());
        assertEquals(this.criarCursoDto.grauAcademicoMinimo(), curso.getGrauAcademicoMinimo().toString());
        assertEquals(this.criarCursoDto.grauEscolarMinimo(), curso.getGrauEscolarMinimo().toString());
    }

    private static Stream<Arguments> argumentosDadosInvalidosParaCriarCurso() {
        return Stream.of(
                Arguments.of("PG", 6L, "ENSINO_MEDIO_INCOMPLETO", "BACHAREL"),
                Arguments.of("Curso de Lógica de Programação", 0L, "ENSINO_MEDIO_INCOMPLETO", "BACHAREL"),
                Arguments.of("Curso de Lógica de Programação", 6L, "ENSINO_MEDIO", "BACHAREL"),
                Arguments.of("Curso de Lógica de Programação", 6L, "ENSINO_MEDIO_INCOMPLETO", "PHD"));
    }

    @ParameterizedTest
    @MethodSource("argumentosDadosInvalidosParaCriarCurso")
    @DisplayName("Deve falhar ao tentar criar um curso com dados incorretos")
    void givenTenhoUmCriarCursoDtoComDadosInvalidosWhenExecutoCriarCursoThenLancarUmErro(String titulo,
            Long duracaoEmMeses,
            String grauEscolaridadeMinimo, String grauAcademicoMinimo) {
        when(this.professorRepository.findById(1L)).thenReturn(Optional.of(this.professorMock));

        CriarCursoDto criarCursoDto2 = new CriarCursoDto(titulo, duracaoEmMeses, grauEscolaridadeMinimo,
                grauAcademicoMinimo);

        assertThrows(IllegalArgumentException.class, () -> this.cursoService.criarCurso(criarCursoDto2, 1L));
    }

    @Test
    @DisplayName("Deve ser possível atualizar os dados de um curso")
    void givenTenhoUmAtualizarDadosCursoDtoWhenExecutoMetodoParaAtualizarThenAtualizarDados() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoMock));

        Curso curso = this.cursoService.atualizarCurso(atualizarDadosCursoDtoTodosDados, 1L);
        assertEquals(this.atualizarDadosCursoDtoTodosDados.titulo(), curso.getTitulo());
        assertEquals(this.atualizarDadosCursoDtoTodosDados.duracaoMeses(), curso.getDuracaoMeses());
        assertEquals(this.atualizarDadosCursoDtoTodosDados.grauAcademicoMinimo(),
                curso.getGrauAcademicoMinimo().toString());
        assertEquals(this.atualizarDadosCursoDtoTodosDados.grauEscolarMinimo(),
                curso.getGrauEscolarMinimo().toString());
    }

    @Test
    @DisplayName("Deve ser possível atualizar somente um dado de um curso")
    void givenTenhoUmAtualizarDadosCursoDtoComApenasUmDadoWhenExecutoMetodoParaAtualizarThenAtualizarDados() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoMock));

        Curso curso = this.cursoService.atualizarCurso(atualizarDadosCursoDtoUmDado,
                1L);

        assertEquals(this.atualizarDadosCursoDtoUmDado.titulo(), curso.getTitulo());
        assertNotEquals(this.atualizarDadosCursoDtoUmDado.duracaoMeses(), curso.getDuracaoMeses());
        assertNotEquals(this.atualizarDadosCursoDtoUmDado.grauAcademicoMinimo(),
                curso.getGrauAcademicoMinimo().toString());
        assertNotEquals(this.atualizarDadosCursoDtoUmDado.grauEscolarMinimo(),
                curso.getGrauEscolarMinimo().toString());
    }

    @Test
    @DisplayName("Deve falhar ao tentar atualizar um curso inexistete")
    void givenTenhoUmAtualizarDadosCursoDtoECursoIdInexistenteWhenExecutoMetodoParaAtualizarThenLancarErro() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> this.cursoService.atualizarCurso(atualizarDadosCursoDtoTodosDados,
                        1L));
    }

    private static Stream<Arguments> argumentosDadosInvalidosParaAtualizarCurso() {
        return Stream.of(
                Arguments.of("PG", 6L, "ENSINO_MEDIO_INCOMPLETO", "BACHAREL"),
                Arguments.of("Curso de Lógica de Programação", 0L, "ENSINO_MEDIO_INCOMPLETO", "BACHAREL"),
                Arguments.of("Curso de Lógica de Programação", 6L, "ENSINO_MEDIO", "BACHAREL"),
                Arguments.of("Curso de Lógica de Programação", 6L, "ENSINO_MEDIO_INCOMPLETO", "PHD"));
    }

    @ParameterizedTest
    @MethodSource("argumentosDadosInvalidosParaAtualizarCurso")
    @DisplayName("Deve falhar ao tentar atualizar um curso com dados incorretos")
    void givenTenhoUmAtualizarDadosCursoDtoComDadosInvalidosWhenExecutoAtualizarCursoThenLancarUmErro(String titulo,
            Long duracaoEmMeses,
            String grauEscolaridadeMinimo, String grauAcademicoMinimo) {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoMock));
        AtualizarDadosCursoDto AtualizarDadosCursoDto2 = new AtualizarDadosCursoDto(titulo, duracaoEmMeses,
                grauEscolaridadeMinimo,
                grauAcademicoMinimo);

        assertThrows(IllegalArgumentException.class,
                () -> this.cursoService.atualizarCurso(AtualizarDadosCursoDto2, 1L));
    }

    @Test
    @DisplayName("Deve ser possível deletar um curso")
    void givenTenhoUmCursoIdWhenExecutoMetodoParaDeletarCursoThenDeletaCurso() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoMock));

        this.cursoService.deletarCurso(1L);

        verify(this.cursoRepository).delete(this.cursoMock);
    }

    @Test
    @DisplayName("Não deve ser possível deletar um curso inexistente")
    void givenTenhoUmCursoIdQueNaoExisteWhenExecutoMetodoParaDeletarCursoThenLancarUmErro() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> this.cursoService.deletarCurso(1L));
    }

    @Test
    @DisplayName("Deve ser possível listar todos cursos")
    void givenPossuoCursosCadastradosWhenExecutoMetodoParaListarTodosCursosThenRetornarListaDeCursos() {
        List<Curso> listaDeCursos = List.of(this.cursoMock, this.cursoMock);
        when(this.cursoRepository.findAll(pageable)).thenReturn(new PageImpl<>(listaDeCursos));

        List<Curso> resposta = this.cursoService.listarTodosCursos(pageable);

        assertEquals(listaDeCursos.size(), resposta.size());
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver cursos cadastrados, ao listar todos cursos")
    void givenNaoPossuoCursosCadastradosWhenExecutoMetodoParaListarTodosCursosThenRetornarListaVazia() {
        List<Curso> listaDeCursos = List.of();
        when(this.cursoRepository.findAll(pageable)).thenReturn(new PageImpl<>(listaDeCursos));

        List<Curso> resposta = this.cursoService.listarTodosCursos(pageable);

        assertEquals(listaDeCursos.size(), resposta.size());
    }

    @Test
    @DisplayName("Deve ser possivel buscar curso pelo id")
    void givenPossuoCursoIdWhenExecutoMetodoParaPegarCursoThenRetornarCurso() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoMock));

        Curso resposta = this.cursoService.pegarCurso(1L);

        assertEquals(this.cursoMock.getTitulo(), resposta.getTitulo());
        assertEquals(this.cursoMock.getDuracaoMeses(), resposta.getDuracaoMeses());
        assertEquals(this.cursoMock.getGrauAcademicoMinimo().toString(),
                resposta.getGrauAcademicoMinimo().toString());
        assertEquals(this.cursoMock.getGrauEscolarMinimo().toString(),
                resposta.getGrauEscolarMinimo().toString());
    }

    @Test
    @DisplayName("Deve lançar um erro ao buscar pelo id de um curso que não existe")
    void givenPossuoCursoIdInexistenteWhenExecutoMetodoParaPegarCursoThenLancarUmErro() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> this.cursoService.pegarCurso(1L));
    }
}
