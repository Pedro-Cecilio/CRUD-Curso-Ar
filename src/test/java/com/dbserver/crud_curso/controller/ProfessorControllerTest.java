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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.dbserver.crud_curso.controller.utils.TesteUtils;
import com.dbserver.crud_curso.domain.enums.GrauAcademico;
import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;
import com.dbserver.crud_curso.domain.professor.dto.AtualizarDadosProfessorDto;
import com.dbserver.crud_curso.domain.professor.dto.CriarProfessorDto;
import com.dbserver.crud_curso.domain.professor.dto.ProfessorRespostaDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfessorControllerTest {

    private MockMvc mockMvc;
    private PasswordEncoder passwordEncoder;
    private CriarProfessorDto criarProfessorDtoMock;
    private CriarProfessorDto criarProfessorDtoExistenteMock;
    private JacksonTester<CriarProfessorDto> criarProfessorDtoJson;

    private AtualizarDadosProfessorDto atualizarDadosProfessorDto;;
    private JacksonTester<AtualizarDadosProfessorDto> atualizarDadosProfessorDtoJson;

    private ProfessorRepository professorRepository;

    private Professor professorContaAtivada;
    private Professor professorContaDesativada;
    private List<Professor> professoresSalvos;

    private ObjectMapper objectMapper;

    private List<Professor> popularBanco() {
        Professor professor = new Professor("professorContaAtivada@email.com",
                "senha123",
                "João",
                "Silva",
                25L,
                "LICENCIATURA");
        Professor professor2 = new Professor("professorContaDesativada@email.com",
                "senha123",
                "Pedro",
                "Silva",
                25L,
                "MESTRE");
        professor2.setDesativada(true);

        return this.professorRepository.saveAll(List.of(professor, professor2));
    }

    @Autowired
    public ProfessorControllerTest(MockMvc mockMvc, JacksonTester<CriarProfessorDto> criarProfessorDtoJson,
            ProfessorRepository professorRepository, ObjectMapper objectMapper,
            JacksonTester<AtualizarDadosProfessorDto> atualizarDadosProfessorDtoJson,
            PasswordEncoder passwordEncoder) {
        this.mockMvc = mockMvc;
        this.criarProfessorDtoJson = criarProfessorDtoJson;
        this.professorRepository = professorRepository;
        this.objectMapper = objectMapper;
        this.atualizarDadosProfessorDtoJson = atualizarDadosProfessorDtoJson;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void prepararTeste() {
        this.criarProfessorDtoMock = new CriarProfessorDto("exemplo@email.com",
                "senha123",
                "João",
                "Silva",
                25L,
                "BACHAREL");
        this.criarProfessorDtoExistenteMock = new CriarProfessorDto("professorContaAtivada@email.com",
                "senha123",
                "João",
                "Silva",
                25L,
                "MESTRE");
        this.atualizarDadosProfessorDto = new AtualizarDadosProfessorDto("senhaAtualizada", "NomeAtualizado",
                "Novo", 28L,
                GrauAcademico.DOUTOR.toString());
        this.professoresSalvos = this.popularBanco();
        this.professorContaAtivada = this.professoresSalvos.get(0);
        this.professorContaDesativada = this.professoresSalvos.get(1);
    }

    @AfterEach
    void limparTeste() {
        this.professorRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve ser possível cadastrar um novo professor e retornar status 201")
    void givenTenhoUmCriarProfessorDtoNoFormatoJsonWhenEnvioEsseJsonProEndPointDeCriarProfessorThenRetornarStatus201()
            throws Exception {
        String json = this.criarProfessorDtoJson.write(criarProfessorDtoMock).getJson();
        mockMvc
                .perform(MockMvcRequestBuilders.post("/professor").contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value(this.criarProfessorDtoMock.email()))
                .andExpect(jsonPath("$.nome").value(this.criarProfessorDtoMock.nome()))
                .andExpect(jsonPath("$.sobrenome").value(this.criarProfessorDtoMock.sobrenome()))
                .andExpect(jsonPath("$.idade").value(this.criarProfessorDtoMock.idade()))
                .andExpect(jsonPath("$.grauAcademico")
                        .value(this.criarProfessorDtoMock.grauAcademico()));

        assertTrue(this.professorRepository.findByEmail(this.criarProfessorDtoMock.email()).isPresent());
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um novo professor com um email que já existe e retornar status 400")
    void givenTenhoUmCriarProfessorDtoComEmailExistenteNoFormatoJsonWhenEnvioEsseJsonProEndPointDeCriarProfessorThenRetornarStatus400()
            throws Exception {
        String json = this.criarProfessorDtoJson.write(this.criarProfessorDtoExistenteMock).getJson();
        mockMvc
                .perform(MockMvcRequestBuilders.post("/professor").contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Email não disponível"));
    }

    private static Stream<Arguments> argumentosInvalidosParaCriarProfessor() {
        return Stream.of(
                Arguments.of("exemploemail.com", "12345678", "João", "Silva", 25L,
                        "BACHAREL",
                        "Email com formato inválido"),
                Arguments.of(null, "12345678", "João", "Silva", 25L, "BACHAREL",
                        "Email deve ser informado"),
                Arguments.of("exemplo@email.com", "1234567", "João", "Silva", 25L,
                        "BACHAREL",
                        "Senha deve conter 8 caracteres no mínimo"),
                Arguments.of("exemplo@email.com", null, "João", "Silva", 25L,
                        "BACHAREL",
                        "Senha deve ser informada"),
                Arguments.of("exemplo@email.com", "12345678", "Ju", "Silva", 25L,
                        "BACHAREL",
                        "Nome deve conter 3 caracteres no mínimo e 20 no máximo"),
                Arguments.of("exemplo@email.com", "12345678", null, "Silva", 25L,
                        "BACHAREL",
                        "Nome deve ser informado"),
                Arguments.of("exemplo@email.com", "12345678", "João", " ", 25L,
                        "BACHAREL",
                        "Sobrenome deve conter 2 caracteres no mínimo e 20 no máximo"),
                Arguments.of("exemplo@email.com", "12345678", "João", null, 25L,
                        "BACHAREL",
                        "Sobrenome deve ser informado"),
                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", 6L,
                        "BACHAREL",
                        "Idade deve ser maior do que 6 e menor que 110"),
                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", null,
                        "BACHAREL",
                        "Idade deve ser informada"),
                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", 25L, "PHD",
                        "Grau acadêmico inválido"),
                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", 25L, null,
                        "Grau acadêmico deve ser informado"));
    }

    @ParameterizedTest
    @MethodSource("argumentosInvalidosParaCriarProfessor")
    @DisplayName("Deve retonar 400 ao terntar criar professor com dados inválidos")
    void givenTenhoUmCriarProfessorDtoNoFormatoJsonComDadosInvalidoWhenEnvioEsseJsonProEndPointDeCriarProfessorThenRetornarStatus400(
            String email, String senha, String nome, String sobrenome, Long idade, String escolaridade,
            String mensagem) throws Exception {

        this.criarProfessorDtoMock = new CriarProfessorDto(email, senha, nome, sobrenome, idade, escolaridade);

        String json = this.criarProfessorDtoJson.write(criarProfessorDtoMock).getJson();

        mockMvc
                .perform(MockMvcRequestBuilders.post("/professor").contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value(mensagem));

    }

    @Test
    @DisplayName("Deve ser possível reativar conta de um professor e retornar status 200")
    void givenTenhoUmIdDeUmProfessorDesativadoWhenChamoOEndPointParaReativarPassandoOIdThenRetonarStatus200()
            throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.post(
                        "/professor/reativar/%d".formatted(this.professorContaDesativada.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("Conta reativada com sucesso!"));
        this.professorContaDesativada = this.professorRepository.findById(this.professorContaDesativada.getId()).get();
        assertFalse(this.professorContaDesativada.isDesativada());
    }

    @Test
    @DisplayName("Deve falhar ao tentar reativar conta de um professor inexistente e retornar status 404")
    void givenTenhoUmIdDeUmProfessorInexistenteWhenChamoOEndPointParaReativarPassandoOIdThenRetonarStatus404()
            throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.post("/professor/reativar/%d".formatted(500)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Professor não encontrado ou não possui conta desativada."));

    }

    @Test
    @DisplayName("Deve falhar ao tentar reativar conta de um aluno com conta ativa e retornar status 404")
    void givenTenhoUmIdDeUmProfessorComContaAtivaWhenChamoOEndPointParaReativarPassandoOIdThenRetonarStatus404()
            throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/professor/reativar/%d".formatted(professorContaAtivada.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Professor não encontrado ou não possui conta desativada."));
    }

    @Test
    @DisplayName("Deve ser possível atualizar dados do aluno e retornar status 200")
    void givenTenhoUmAtualizarDadosProfessorDtoNoFormatoJsonWhenEnvioEsseJsonProEndPointDeAtualizarAlunoThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(professorContaAtivada);
        String json = this.atualizarDadosProfessorDtoJson.write(this.atualizarDadosProfessorDto).getJson();

        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/professor/%d".formatted(this.professorContaAtivada.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(this.professorContaAtivada.getId()))
                .andExpect(jsonPath("$.email").value(this.professorContaAtivada.getEmail()))
                .andExpect(jsonPath("$.nome").value(this.atualizarDadosProfessorDto.nome()))
                .andExpect(jsonPath("$.sobrenome").value(this.atualizarDadosProfessorDto.sobrenome()))
                .andExpect(jsonPath("$.idade").value(this.atualizarDadosProfessorDto.idade()))
                .andExpect(jsonPath("$.grauAcademico")
                        .value(this.atualizarDadosProfessorDto.grauAcademico()));

        Professor professorAtualizado = this.professorRepository.findById(this.professorContaAtivada.getId()).get();
        assertEquals(this.atualizarDadosProfessorDto.nome(),
                professorAtualizado.getNome());
        assertEquals(this.atualizarDadosProfessorDto.sobrenome(),
                professorAtualizado.getSobrenome());
        assertEquals(this.atualizarDadosProfessorDto.idade(),
                professorAtualizado.getIdade());
        assertEquals(this.atualizarDadosProfessorDto.grauAcademico(),
                professorAtualizado.getGrauAcademico().toString());
        assertTrue(passwordEncoder.matches(this.atualizarDadosProfessorDto.senha(),
                professorAtualizado.getSenha()));
    }

    private static Stream<Arguments> argumentosInvalidosParaAtualizarProfessor() {
        return Stream.of(
                Arguments.of("1234567", "João", "Silva", 25L, "BACHAREL",
                        "Senha deve conter 8 caracteres no mínimo"),
                Arguments.of("12345678", "Ju", "Silva", 25L, "BACHAREL",
                        "Nome deve conter 3 caracteres no mínimo e 20 no máximo"),
                Arguments.of("12345678", "João", "P", 25L, "BACHAREL",
                        "Sobrenome deve conter 2 caracteres no mínimo e 20 no máximo"),
                Arguments.of("12345678", "João", "Silva", 6L, "BACHAREL",
                        "Idade deve ser maior do que 6 e menor que 110"),
                Arguments.of("12345678", "João", "Silva", 25L, "PHD",
                        "Grau acadêmico inválido"));
    }

    @ParameterizedTest
    @MethodSource("argumentosInvalidosParaAtualizarProfessor")
    @DisplayName("Deve retonar 400 ao terntar criar professor com dados inválidos")
    void givenTenhoUmAtualizarDadosProfessorDtoNoFormatoJsonComDadosInvalidosWhenEnvioEsseJsonProEndPointDeAtualizarProfessorThenRetornarStatus400(
            String senha, String nome, String sobrenome, Long idade, String escolaridade,
            String mensagem) throws Exception {
        TesteUtils.login(this.professorContaAtivada);
        this.atualizarDadosProfessorDto = new AtualizarDadosProfessorDto(senha, nome,
                sobrenome, idade,
                escolaridade);

        String json = this.atualizarDadosProfessorDtoJson.write(atualizarDadosProfessorDto).getJson();
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/professor/%d".formatted(this.professorContaAtivada.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value(mensagem));

    }

    @Test
    @DisplayName("Deve ser possível deletar professor e retornar status 200")
    void givenTenhoIdDeUmProfessorExistenteWhenEnvioEndPointDeDeletarProfessorThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(professorContaAtivada);

        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/professor/%d".formatted(this.professorContaAtivada.getId())))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        Boolean professorFoiDeletado = this.professorRepository
                .findByIdAndDesativadaTrue(this.professorContaAtivada.getId())
                .isPresent();
        assertTrue(professorFoiDeletado);
    }

    @Test
    @DisplayName("Não deve ser possível deletar professor inexistente e retornar status 404")
    void givenTenhoIdDeUmProfessorInexistenteWhenEnvioEndPointDeDeletarProfessorThenRetornarStatus404()
            throws Exception {
        TesteUtils.login(professorContaAtivada);

        mockMvc
                .perform(MockMvcRequestBuilders.delete("/professor/%d".formatted(500)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Professor não encontrado."));
    }

    @Test
    @DisplayName("Não deve ser possível deletar professor que já está desativado e retornar status 404")
    void givenTenhoIdDeUmProfessorDeUmProfessorDesativadoWhenEnvioEndPointDeDeletarProfessorThenRetornarStatus404()
            throws Exception {
        TesteUtils.login(professorContaAtivada);

        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/professor/%d".formatted(this.professorContaDesativada.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Professor não encontrado."));
    }

    @Test
    @DisplayName("Deve ser possível listar todos professores cadastrados ativos e retornar status 200")

    void givenEstouLogadoNaAplicaçãoWhenEnvioEndPointDeListarTodosProfessoresThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(professorContaAtivada);

        MockHttpServletResponse resposta = mockMvc
                .perform(MockMvcRequestBuilders.get("/professor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse();
        List<ProfessorRespostaDto> professores = this.objectMapper.readValue(resposta.getContentAsString(),
                new TypeReference<List<ProfessorRespostaDto>>() {
                });
        List<Professor> professoresAtivos = this.professoresSalvos.stream()
                .filter(professor -> !professor.isDesativada()).toList();
        assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertEquals(professoresAtivos.size(), professores.size());
    }

    @Test
    @DisplayName("Deve buscar por professor ativo cadastrados e retornar status 200")
    void givenPossuoUmProfessorIdDeUmProfessorAtivadoWhenEnvioEndPointDePegarUmProfessorThenRetornarStatus200()
            throws Exception {
        TesteUtils.login(professorContaAtivada);
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/professor/%d".formatted(this.professorContaAtivada.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(this.professorContaAtivada.getId()))
                .andExpect(jsonPath("$.email").value(this.professorContaAtivada.getEmail()))
                .andExpect(jsonPath("$.nome").value(this.professorContaAtivada.getNome()))
                .andExpect(jsonPath("$.sobrenome").value(this.professorContaAtivada.getSobrenome()))
                .andExpect(jsonPath("$.idade").value(this.professorContaAtivada.getIdade()))
                .andExpect(jsonPath("$.grauAcademico")
                        .value(this.professorContaAtivada.getGrauAcademico().toString()));
    }

    @Test
    @DisplayName("Deve falhar ao buscar por professor desativado ou inexistente e retornar status 404")
    void givenPossuoUmProfessorIdInexistenteOuDeUmProfessorDesativadoEnvioEndPointDePegarUmProfessorThenRetornarStatus404()
            throws Exception {
        TesteUtils.login(professorContaAtivada);

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/professor/%d".formatted(this.professorContaDesativada.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Professor não encontrado."));

    }
}
