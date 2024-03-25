package com.dbserver.crud_curso.controller;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.dbserver.crud_curso.controller.utils.TesteUtils;
import com.dbserver.crud_curso.domain.curso.Curso;
import com.dbserver.crud_curso.domain.curso.CursoRepository;
import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;
import com.dbserver.crud_curso.domain.professorCurso.ProfessorCurso;
import com.dbserver.crud_curso.domain.professorCurso.ProfessorCursoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfessorCursoControllerTest {

    private MockMvc mockMvc;
    private CursoRepository cursoRepository;
    private ProfessorRepository professorRepository;
    private ProfessorCursoRepository professorCursoRepository;
    private List<Curso> cursosSalvos;
    private List<Professor> professoresSalvos;
    private List<ProfessorCurso> professoresCurso;
    private ObjectMapper objectMapper;

    @Autowired
    public ProfessorCursoControllerTest(MockMvc mockMvc,
            CursoRepository cursoRepository, ProfessorCursoRepository professorCursoRepository, ProfessorRepository professorRepository,
            ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.cursoRepository = cursoRepository;
        this.professorCursoRepository = professorCursoRepository;
        this.professorRepository = professorRepository;
        this.objectMapper = objectMapper;
    }

    private void popularBanco() {
        Professor professorBacharel = new Professor(
                "exemplo@email.com",
                "senha123",
                "João",
                "Silva",
                25L,
                "BACHAREL");
        Professor professorMestre = new Professor(
                "exemplo2@email.com",
                "senha123",
                "Lucas",
                "Silva",
                16L,
                "MESTRE");

        Curso cursoMedioIncompleto = new Curso("Curso de Lógica de Programação",
                6L,
                "ENSINO_MEDIO_INCOMPLETO",
                "BACHAREL");
        Curso cursoSuperiorIncompleto = new Curso("Curso de QA",
                12L,
                "ENSINO_SUPERIOR_INCOMPLETO",
                "MESTRE");

        this.professoresSalvos = this.professorRepository.saveAll(List.of(professorBacharel, professorMestre));
        this.cursosSalvos = this.cursoRepository.saveAll(List.of(cursoMedioIncompleto, cursoSuperiorIncompleto));

        ProfessorCurso professorCadastradoNoCurso = new ProfessorCurso(this.professoresSalvos.get(0),
                this.cursosSalvos.get(0), false);
        ProfessorCurso professorCadastradoNoCurso2 = new ProfessorCurso(this.professoresSalvos.get(1),
                this.cursosSalvos.get(1), false);
        this.professoresCurso = this.professorCursoRepository
                .saveAll(List.of(professorCadastradoNoCurso, professorCadastradoNoCurso2));

    }

    @BeforeEach
    void prepararTeste() {
        this.popularBanco();
    }

    @AfterEach
    void limparTeste() {
        this.cursoRepository.deleteAll();
        this.professorRepository.deleteAll();
        this.professorCursoRepository.deleteAll();
    }

    @Test
    @DisplayName("Como professor, deve ser possível se cadastrar em um curso")
    void givenEstouLogadoComoProfessorEPossuoIdDoCursoWhenTentoMeCadastrarEmUmCursoThenRetornarStatus200()
            throws Exception {
        Professor professorLogado = TesteUtils.login(this.professoresSalvos.get(1));
        Curso cursoASerCadastrado = this.cursosSalvos.get(0);
        mockMvc
                .perform(MockMvcRequestBuilders.post("/professorCurso/%d".formatted(cursoASerCadastrado.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.professor.id").value(professorLogado.getId()))
                .andExpect(jsonPath("$.curso.id").value(cursoASerCadastrado.getId()))
                .andExpect(jsonPath("$.criador").value(false))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("Não deve ser possível se cadastrar em um curso inexistente")
    void givenEstouLogadoComoProfessorEPossuoIdDeUmCursoInexistenteWhenTentoMeCadastrarEmUmCursoThenRetornarStatus404()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));
        mockMvc
                .perform(MockMvcRequestBuilders.post("/professorCurso/%d".formatted(500)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Curso não encontrado."));
    }

    @Test
    @DisplayName("Como professor, deve ser possível sair do curso")
    void givenEstouLogadoComoProfessorEPossuoCursoIdWhenTentoSairDoCursoThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/professorCurso/%d".formatted(this.professoresCurso.get(0).getCurso().getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("Professor desativado do curso com sucesso!"));
    }

    @Test
    @DisplayName("Como professor, não deve ser possível sair de curso em que nã está cadastrado")
    void givenEstouLogadoComoProfessorEPossuoCursoIdQueNaoEstouCadastradoWhenTentoSairDoCursoThenRetornarStatus404()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        mockMvc
                .perform(MockMvcRequestBuilders.delete("/professorCurso/%d".formatted(500)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Professor não está cadastrado/ativo no curso"));
    }

    @Test
    @DisplayName("Como professor, não deve ser possível sair de curso ao passar cursoId com formato inválido")
    void givenEstouLogadoComoProfessorEPossuoCursoIdComFormatoInvalidoWhenTentoSairDoCursoThenRetornarStatus404()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        mockMvc
                .perform(MockMvcRequestBuilders.delete("/professorCurso/%s".formatted("abs514")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Curso não encontrado."));
    }

    @Test
    @DisplayName("Como professor, deve ser possível voltar para um curso")
    void givenEstouLogadoComoProfessorEPossuoCursoIdWhenTentoVoltarParaOCursoThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/professorCurso/reativar/%d".formatted(this.professoresCurso.get(0).getCurso().getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("Professor reativado no curso com sucesso!"));
    }

    @Test
    @DisplayName("Como professor, não deve ser possível voltar para um curso em que não está cadastrado")
    void givenEstouLogadoComoProfessorEPossuoCursoIdEmQueNaoEstouCadastradoWhenTentoVoltarParaOCursoThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/professorCurso/reativar/%d".formatted(500)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Professor não está cadastrado no curso"));
    }

    @Test
    @DisplayName("Como professor, não deve ser possível voltar para um curso ao passar cursoId com formato inválido")
    void givenEstouLogadoComoProfessorEPossuoCursoIdComFormatoInvalidoWhenTentoVoltarParaOCursoThenRetornarStatus404()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/professorCurso/reativar/%s".formatted("advc41")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Curso não encontrado."));
    }

    @Test
    @DisplayName("Deve ser possível listar todos professores cadastrados no curso retornar status 200")
    void givenPossuoCursoIdWhenEnvioEndPointDeListarTodosProfeDoCursoThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        MockHttpServletResponse resposta = mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/professorCurso/%d".formatted(professoresCurso.get(0).getCurso().getId())))
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse();
        List<ProfessorCurso> listaAlunoCurso = this.objectMapper.readValue(resposta.getContentAsString(),
                new TypeReference<List<ProfessorCurso>>() {
                });
        List<ProfessorCurso> professoresDoCurso = professoresCurso.stream().filter(professorCurso -> {
            return professorCurso.getCurso().getId() == professoresCurso.get(0).getCurso().getId();
        }).toList();
        assertEquals(professoresDoCurso.size(), listaAlunoCurso.size());

    }

    @Test
    @DisplayName("Não deve ser possível listar todos professores cadastrados no curso, ao passa cursoId com formato inválido e deve retornar status 404")
    void givenPossuoCursoIdComFormatoInvalidoWhenEnvioEndPointDeListarTodosProfeDoCursoThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        mockMvc
                .perform(MockMvcRequestBuilders.get("/professorCurso/%s".formatted("5615fa")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Curso não encontrado."));

    }

    @Test
    @DisplayName("Deve ser possível buscar por um professor cadastrado no curso retornar status 200")
    void givenPossuoUmDadosEntradaProfessorCursoWhenEnvioEndPointDePegarProfessorDoCursoThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        mockMvc
                .perform(MockMvcRequestBuilders.get("/professorCurso/professor")
                        .param("professorId",
                                String.valueOf(this.professoresSalvos.get(0).getId()))
                        .param("cursoId", String.valueOf(this.cursosSalvos.get(0).getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(this.professoresCurso.get(0).getId()))
                .andExpect(jsonPath("$.professor.id").value(this.professoresSalvos.get(0).getId()));
    }

    @Test
    @DisplayName("Não deve ser possível buscar por um professor que não esteja cadastrado no curso informado retornar status 404")
    void givenPossuoUmDadosEntradaProfessorCursoComProfessorIdNaoPertencenteAoCursoInformadoWhenEnvioEndPointDePegarProfessorDoCursoThenRetornarStatus404()
            throws Exception {
        TesteUtils.login(this.professoresSalvos.get(0));

        mockMvc
                .perform(MockMvcRequestBuilders.get("/professorCurso/professor")
                        .param("professorId",
                                String.valueOf(this.professoresSalvos.get(1).getId()))
                        .param("cursoId", String.valueOf(this.cursosSalvos.get(0).getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Professor não está cadastrado no curso"));
    }

}
