package com.dbserver.crud_curso.controller;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.dbserver.crud_curso.controller.utils.TesteUtils;
import com.dbserver.crud_curso.domain.curso.Curso;
import com.dbserver.crud_curso.domain.curso.CursoRepository;
import com.dbserver.crud_curso.domain.curso.dto.AtualizarDadosCursoDto;
import com.dbserver.crud_curso.domain.curso.dto.CriarCursoDto;
import com.dbserver.crud_curso.domain.curso.dto.CursoRespostaDto;
import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CursoControllerTest {

    private MockMvc mockMvc;
    private CriarCursoDto criarCursoDtoMock;
    private JacksonTester<CriarCursoDto> criarCursoDtoJson;

    private AtualizarDadosCursoDto atualizarDadosCursoDtoMock;
    private JacksonTester<AtualizarDadosCursoDto> atualizarDadosCursoDtoJson;

    private CursoRepository cursoRepository;

    private List<Curso> cursosSalvos;
    private List<Professor> professoresSalvos;

    private ObjectMapper objectMapper;

    private ProfessorRepository professorRepository;

    private void popularBanco() {
        Professor professor = new Professor("professorContaAtivada@email.com",
                "senha123",
                "João",
                "Silva",
                25L,
                "BACHAREL");
        Professor professor2 = new Professor("professorContaAtivada2@email.com",
                "senha123",
                "João",
                "Silva",
                25L,
                "LICENCIATURA");
        Curso curso = new Curso("Curso de Lógica de Programação",
                6L,
                "ENSINO_MEDIO_INCOMPLETO",
                "BACHAREL");
        Curso curso2 = new Curso("Curso de QA",
                12L,
                "ENSINO_MEDIO_COMPLETO",
                "MESTRE");
        this.professoresSalvos = this.professorRepository.saveAll(List.of(professor, professor2));
        this.cursosSalvos = this.cursoRepository.saveAll(List.of(curso, curso2));

    }

    @Autowired
    public CursoControllerTest(MockMvc mockMvc, JacksonTester<CriarCursoDto> criarCursoDtoJson,
            CursoRepository cursoRepository, ObjectMapper objectMapper,
            JacksonTester<AtualizarDadosCursoDto> atualizarDadosCursoDtoJson, ProfessorRepository professorRepository) {
        this.mockMvc = mockMvc;
        this.criarCursoDtoJson = criarCursoDtoJson;
        this.cursoRepository = cursoRepository;
        this.objectMapper = objectMapper;
        this.atualizarDadosCursoDtoJson = atualizarDadosCursoDtoJson;
        this.professorRepository = professorRepository;
    }

    @BeforeEach
    void prepararTeste() {
        this.criarCursoDtoMock = new CriarCursoDto(
                "Curso de Algoritmo",
                6L,
                "ENSINO_MEDIO_INCOMPLETO",
                "BACHAREL");

        this.atualizarDadosCursoDtoMock = new AtualizarDadosCursoDto(
                "Curso de Desenvolvimento Web",
                8L,
                "ENSINO_MEDIO_COMPLETO",
                "LICENCIATURA");
        this.popularBanco();
    }

    @AfterEach
    void limparTeste() {
        this.professorRepository.deleteAll();
        this.cursoRepository.deleteAll();
    }

    @Test
    @DisplayName("Como professor, deve ser possível criar um novo curso corretamente e retornar status 201")
    void givenEstouLogadoComoProfessoEPossuoUmCriarCursoDtoJsonWhenEnvioEsseJsonProEndPointDeCriarCursoThenRetornarStatus201()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));
        String json = this.criarCursoDtoJson.write(criarCursoDtoMock).getJson();
        mockMvc
                .perform(MockMvcRequestBuilders.post("/curso").contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value(this.criarCursoDtoMock.titulo()))
                .andExpect(jsonPath("$.duracaoMeses").value(this.criarCursoDtoMock.duracaoMeses()))
                .andExpect(jsonPath("$.grauEscolarMinimo").value(this.criarCursoDtoMock.grauEscolarMinimo()))
                .andExpect(jsonPath("$.grauAcademicoMinimo").value(this.criarCursoDtoMock.grauAcademicoMinimo()));
    }

    @Test
    @DisplayName("Como professor, não deve ser possível criar um curso com grauAcademico menor do que eu possuo")
    void givenTenhoUmCriarCursoDtoComGrauAcademicoMaiorQueOProfessorLogadoPossuiWhenEnvioEsseJsonProEndPointDeCriarAlunoThenRetornarStatus400()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(1));
        String json = this.criarCursoDtoJson.write(this.criarCursoDtoMock).getJson();
        mockMvc
                .perform(MockMvcRequestBuilders.post("/curso").contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro")
                        .value("O professor não possui grau acadêmico mínimo para lecionar no curso."));
    }

    private static Stream<Arguments> argumentosDadosInvalidosParaCriarCurso() {
        return Stream.of(
                Arguments.of("PG", 6L, "ENSINO_MEDIO_INCOMPLETO", "BACHAREL",
                        "Titulo deve conter 3 caracteres no mínimo"),
                Arguments.of(null, 6L, "ENSINO_MEDIO_INCOMPLETO", "BACHAREL", "Titulo deve ser informado"),
                Arguments.of("Curso de Lógica de Programação", 0L, "ENSINO_MEDIO_INCOMPLETO", "BACHAREL",
                        "Duração deve ser maior do que zero"),
                Arguments.of("Curso de Lógica de Programação", null, "ENSINO_MEDIO_INCOMPLETO", "BACHAREL",
                        "Duração deve ser informada"),
                Arguments.of("Curso de Lógica de Programação", 6L, "ENSINO_MEDIO", "BACHAREL",
                        "Grau escolar mínimo inválido"),
                Arguments.of("Curso de Lógica de Programação", 6L, null, "BACHAREL",
                        "Grau escolar mínimo deve ser informado"),
                Arguments.of("Curso de Lógica de Programação", 6L, "ENSINO_MEDIO_INCOMPLETO", "PHD",
                        "Grau acadêmico mínimo inválido"),
                Arguments.of("Curso de Lógica de Programação", 6L, "ENSINO_MEDIO_INCOMPLETO", null,
                        "Grau acadêmico mínimo deve ser informado"));
    }

    @ParameterizedTest
    @MethodSource("argumentosDadosInvalidosParaCriarCurso")
    @DisplayName("Deve retonar 400 ao tentar criar curso com dados inválidos")
    void givenTenhoUmCursoThenRetornarStatus400(
            String titulo,
            Long duracaoEmMeses,
            String grauEscolaridadeMinimo, String grauAcademicoMinimo, String mensagem) throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        this.criarCursoDtoMock = new CriarCursoDto(titulo, duracaoEmMeses, grauEscolaridadeMinimo,
                grauAcademicoMinimo);

        String json = this.criarCursoDtoJson.write(criarCursoDtoMock).getJson();

        mockMvc
                .perform(MockMvcRequestBuilders.post("/curso").contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value(mensagem));

    }

    @Test
    @DisplayName("Deve ser possível atualizar dados do curso e retornar status 200")
    void givenTenhoUmAtualizarDadosCursoDtoNoFormatoJsonWhenEnvioEsseJsonProEndPointDeAtualizarCursoThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));
        String json = this.atualizarDadosCursoDtoJson.write(this.atualizarDadosCursoDtoMock).getJson();
        Curso cursoASerAtualizado = this.cursosSalvos.get(0);
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/curso/%d".formatted(cursoASerAtualizado.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cursoASerAtualizado.getId()))
                .andExpect(jsonPath("$.titulo").value(this.atualizarDadosCursoDtoMock.titulo()))
                .andExpect(jsonPath("$.duracaoMeses").value(this.atualizarDadosCursoDtoMock.duracaoMeses()))
                .andExpect(jsonPath("$.grauEscolarMinimo").value(this.atualizarDadosCursoDtoMock.grauEscolarMinimo()))
                .andExpect(jsonPath("$.grauAcademicoMinimo")
                        .value(this.atualizarDadosCursoDtoMock.grauAcademicoMinimo()));
    }

    private static Stream<Arguments> argumentosInvalidosParaAtualizarAluno() {
        return Stream.of(
                Arguments.of("PG", 6L, "ENSINO_MEDIO_INCOMPLETO", "BACHAREL",
                        "Titulo deve conter 3 caracteres no mínimo"),
                Arguments.of("Curso de Lógica de Programação", 0L, "ENSINO_MEDIO_INCOMPLETO", "BACHAREL",
                        "Duração deve ser maior do que zero"),
                Arguments.of("Curso de Lógica de Programação", 6L, "ENSINO_MEDIO", "BACHAREL",
                        "Grau escolar mínimo inválido"),
                Arguments.of("Curso de Lógica de Programação", 6L, "ENSINO_MEDIO_INCOMPLETO", "PHD",
                        "Grau acadêmico mínimo inválido"));
    }

    @ParameterizedTest
    @MethodSource("argumentosInvalidosParaAtualizarAluno")
    @DisplayName("Deve retonar 400 ao tentar atualizar curso com dados inválidos")
    void givenTenhoUmAtualizarDadosCursoDtoNoFormatoJsonComDadosInvalidosWhenEnvioEsseJsonProEndPointDeAtualizarCursoThenRetornarStatus400(
            String titulo,
            Long duracaoEmMeses,
            String grauEscolaridadeMinimo, String grauAcademicoMinimo, String mensagem) throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));
        Curso cursoASerAtualizado = this.cursosSalvos.get(0);

        this.atualizarDadosCursoDtoMock = new AtualizarDadosCursoDto(titulo, duracaoEmMeses, grauEscolaridadeMinimo,
                grauAcademicoMinimo);

        String json = this.atualizarDadosCursoDtoJson.write(this.atualizarDadosCursoDtoMock).getJson();

        mockMvc
                .perform(MockMvcRequestBuilders.put("/curso/%d".formatted(cursoASerAtualizado.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value(mensagem));

    }

    @Test
    @DisplayName("Deve ser possível deletar curso e retornar status 200")
    void givenTenhoIdDeUmCursoExistenteWhenEnvioEndPointDeDeletarCursoThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));
        Curso cursoASerDeletado = this.cursosSalvos.get(0);

        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/curso/%d".formatted(cursoASerDeletado.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("Curso deletado com sucesso"));

        Boolean cursoFoiDeletado = this.cursoRepository
                .findById(cursoASerDeletado.getId())
                .isEmpty();
        assertTrue(cursoFoiDeletado);
    }

    @Test
    @DisplayName("Não deve ser possível deletar curso inexistente e retornar status 200")
    void givenTenhoIdDeUmCursoInexistenteWhenEnvioEndPointDeDeletarCursoThenRetornarStatus404()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/curso/%d".formatted(500)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Curso não encontrado."));
    }

    @Test
    @DisplayName("Deve ser possível listar todos cursos cadastrados e retornar status 200")
    void givenEstouLogadoNaAplicaçãoWhenEnvioEndPointDeListarTodosCursosThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        MockHttpServletResponse resposta = mockMvc
                .perform(MockMvcRequestBuilders.get("/curso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse();
        List<CursoRespostaDto> cursos = this.objectMapper.readValue(resposta.getContentAsString(),
                new TypeReference<List<CursoRespostaDto>>() {
                });
        assertEquals(this.cursosSalvos.size(), cursos.size());
    }

    @Test
    @DisplayName("Deve buscar por curso cadastrados e retornar status 200")
    void givenPossuoUmCursoIdWhenEnvioEndPointDePegarUmCursoThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));
        Curso cursoASerBuscado = this.cursosSalvos.get(0);

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/curso/%d".formatted(cursoASerBuscado.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cursoASerBuscado.getId()))
                .andExpect(jsonPath("$.titulo").value(cursoASerBuscado.getTitulo()))
                .andExpect(jsonPath("$.duracaoMeses").value(cursoASerBuscado.getDuracaoMeses()))
                .andExpect(jsonPath("$.grauEscolarMinimo").value(cursoASerBuscado.getGrauEscolarMinimo().toString()))
                .andExpect(jsonPath("$.grauAcademicoMinimo")
                        .value(cursoASerBuscado.getGrauAcademicoMinimo().toString()));
    }

    @Test
    @DisplayName("Não deve ser possivel buscar por curso inexistente e retornar status 404")
    void givenPossuoUmCursoIdInexistenteWhenEnvioEndPointDePegarUmCursoThenRetornarStatus404()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/curso/%d".formatted(500)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Curso não encontrado."));
    }

}
