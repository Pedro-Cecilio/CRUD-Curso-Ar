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
import com.dbserver.crud_curso.domain.aluno.Aluno;
import com.dbserver.crud_curso.domain.aluno.AlunoRepository;
import com.dbserver.crud_curso.domain.aluno.dto.AlunoRespostaDto;
import com.dbserver.crud_curso.domain.aluno.dto.AtualizarDadosAlunoDto;
import com.dbserver.crud_curso.domain.aluno.dto.CriarAlunoDto;
import com.dbserver.crud_curso.domain.enums.GrauEscolaridade;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlunoControllerTest {

        private MockMvc mockMvc;
        private PasswordEncoder passwordEncoder;
        private CriarAlunoDto criarAlunoDtoMock;
        private CriarAlunoDto criarAlunoDtoAlunoDesativadoMock;
        private JacksonTester<CriarAlunoDto> criarAlunoDtoJson;

        private AtualizarDadosAlunoDto atualizarDadosAlunoDtoMock;
        private JacksonTester<AtualizarDadosAlunoDto> atualizarDadosAlunoDtoJson;

        private AlunoRepository alunoRepository;

        private Aluno alunoContaAtivada;
        private Aluno alunoContaDesativada;
        private List<Aluno> alunosSalvos;

        private ObjectMapper objectMapper;

        private List<Aluno> popularBanco() {
                Aluno aluno = new Aluno("alunoContaAtivada@email.com",
                                "senha123",
                                "João",
                                "Silva",
                                25L,
                                "ENSINO_SUPERIOR_COMPLETO");
                Aluno aluno2 = new Aluno("alunoContaDesativada@email.com",
                                "senha123",
                                "Pedro",
                                "Silva",
                                25L,
                                "ENSINO_SUPERIOR_INCOMPLETO");
                aluno2.setDesativada(true);

                return this.alunoRepository.saveAll(List.of(aluno, aluno2));
        }

        @Autowired
        public AlunoControllerTest(MockMvc mockMvc, JacksonTester<CriarAlunoDto> criarAlunoDtoJson,
                        AlunoRepository alunoRepository, ObjectMapper objectMapper,
                        JacksonTester<AtualizarDadosAlunoDto> atualizarDadosAlunoDtoJson,
                        PasswordEncoder passwordEncoder) {
                this.mockMvc = mockMvc;
                this.criarAlunoDtoJson = criarAlunoDtoJson;
                this.alunoRepository = alunoRepository;
                this.objectMapper = objectMapper;
                this.atualizarDadosAlunoDtoJson = atualizarDadosAlunoDtoJson;
                this.passwordEncoder = passwordEncoder;
        }

        @BeforeEach
        void prepararTeste() {
                this.criarAlunoDtoMock = new CriarAlunoDto("exemplo@email.com",
                                "senha123",
                                "João",
                                "Silva",
                                25L,
                                "ENSINO_SUPERIOR_COMPLETO");
                this.criarAlunoDtoAlunoDesativadoMock = new CriarAlunoDto("alunoContaAtivada@email.com",
                                "senha123",
                                "João",
                                "Silva",
                                25L,
                                "ENSINO_SUPERIOR_COMPLETO");
                this.atualizarDadosAlunoDtoMock = new AtualizarDadosAlunoDto("senhaAtualizada", "NomeAtualizado",
                                "Novo", 28L,
                                GrauEscolaridade.ENSINO_MEDIO_INCOMPLETO.toString());
                this.alunosSalvos = this.popularBanco();
                this.alunoContaAtivada = this.alunosSalvos.get(0);
                this.alunoContaDesativada = this.alunosSalvos.get(1);
        }

        @AfterEach
        void limparTeste() {
                this.alunoRepository.deleteAll();
        }

        @Test
        @DisplayName("Deve ser possível cadastrar um novo aluno e retornar status 201")
        void givenTenhoUmCriarAlunoDtoNoFormatoJsonWhenEnvioEsseJsonProEndPointDeCriarAlunoThenRetornarStatus201()
                        throws Exception {
                String json = this.criarAlunoDtoJson.write(criarAlunoDtoMock).getJson();
                mockMvc
                                .perform(MockMvcRequestBuilders.post("/aluno").contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").isNumber())
                                .andExpect(jsonPath("$.email").value(this.criarAlunoDtoMock.email()))
                                .andExpect(jsonPath("$.nome").value(this.criarAlunoDtoMock.nome()))
                                .andExpect(jsonPath("$.sobrenome").value(this.criarAlunoDtoMock.sobrenome()))
                                .andExpect(jsonPath("$.idade").value(this.criarAlunoDtoMock.idade()))
                                .andExpect(jsonPath("$.grauEscolaridade")
                                                .value(this.criarAlunoDtoMock.grauEscolaridade()));

                assertTrue(this.alunoRepository.findByEmail(this.criarAlunoDtoMock.email()).isPresent());
        }

        @Test
        @DisplayName("Não deve ser possível cadastrar um novo aluno com um email que já existe e retornar status 400")
        void givenTenhoUmCriarAlunoDtoComEmailExistenteNoFormatoJsonWhenEnvioEsseJsonProEndPointDeCriarAlunoThenRetornarStatus400()
                        throws Exception {
                String json = this.criarAlunoDtoJson.write(this.criarAlunoDtoAlunoDesativadoMock).getJson();
                mockMvc
                                .perform(MockMvcRequestBuilders.post("/aluno").contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.erro").value("Email não disponível"));
        }

        private static Stream<Arguments> argumentosInvalidosParaCriarAluno() {
                return Stream.of(
                                Arguments.of("exemploemail.com", "12345678", "João", "Silva", 25L,
                                                "ENSINO_SUPERIOR_COMPLETO",
                                                "Email com formato inválido"),
                                Arguments.of(null, "12345678", "João", "Silva", 25L, "ENSINO_SUPERIOR_COMPLETO",
                                                "Email deve ser informado"),
                                Arguments.of("exemplo@email.com", "1234567", "João", "Silva", 25L,
                                                "ENSINO_SUPERIOR_COMPLETO",
                                                "Senha deve conter 8 caracteres no mínimo"),
                                Arguments.of("exemplo@email.com", null, "João", "Silva", 25L,
                                                "ENSINO_SUPERIOR_COMPLETO",
                                                "Senha deve ser informada"),
                                Arguments.of("exemplo@email.com", "12345678", "Ju", "Silva", 25L,
                                                "ENSINO_SUPERIOR_COMPLETO",
                                                "Nome deve conter 3 caracteres no mínimo e 20 no máximo"),
                                Arguments.of("exemplo@email.com", "12345678", null, "Silva", 25L,
                                                "ENSINO_SUPERIOR_COMPLETO",
                                                "Nome deve ser informado"),
                                Arguments.of("exemplo@email.com", "12345678", "João", " ", 25L,
                                                "ENSINO_SUPERIOR_COMPLETO",
                                                "Sobrenome deve conter 2 caracteres no mínimo e 20 no máximo"),
                                Arguments.of("exemplo@email.com", "12345678", "João", null, 25L,
                                                "ENSINO_SUPERIOR_COMPLETO",
                                                "Sobrenome deve ser informado"),
                                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", 6L,
                                                "ENSINO_SUPERIOR_COMPLETO",
                                                "Idade deve ser maior do que 6 e menor que 110"),
                                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", null,
                                                "ENSINO_SUPERIOR_COMPLETO",
                                                "Idade deve ser informada"),
                                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", 25L, "BACHAREL",
                                                "Grau de escolaridade inválido"),
                                Arguments.of("exemplo@email.com", "12345678", "João", "Silva", 25L, null,
                                                "Grau de escolaridade deve ser informado"));
        }

        @ParameterizedTest
        @MethodSource("argumentosInvalidosParaCriarAluno")
        @DisplayName("Deve retonar 400 ao terntar criar aluno com dados inválidos")
        void givenTenhoUmCriarAlunoDtoNoFormatoJsonComDadosInvalidoWhenEnvioEsseJsonProEndPointDeCriarAlunoThenRetornarStatus400(
                        String email, String senha, String nome, String sobrenome, Long idade, String escolaridade,
                        String mensagem) throws Exception {

                this.criarAlunoDtoMock = new CriarAlunoDto(email, senha, nome, sobrenome, idade, escolaridade);

                String json = this.criarAlunoDtoJson.write(criarAlunoDtoMock).getJson();

                mockMvc
                                .perform(MockMvcRequestBuilders.post("/aluno").contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.erro").value(mensagem));

        }

        @Test
        @DisplayName("Deve ser possível reativar conta de um aluno e retornar status 200")
        void givenTenhoUmIdDeUmAlunoDesativadoWhenChamoOEndPointParaReativarPassandoOIdThenRetonarStatus200()
                        throws Exception {
                mockMvc
                                .perform(MockMvcRequestBuilders.post(
                                                "/aluno/reativar/%d".formatted(this.alunoContaDesativada.getId())))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Conta reativada com sucesso!"));
                this.alunoContaDesativada = this.alunoRepository.findById(this.alunoContaDesativada.getId()).get();
                assertFalse(this.alunoContaDesativada.isDesativada());
        }

        @Test
        @DisplayName("Deve falhar ao tentar reativar conta de um aluno inexistente e retornar status 404")
        void givenTenhoUmIdDeUmAlunoInexistenteWhenChamoOEndPointParaReativarPassandoOIdThenRetonarStatus404()
                        throws Exception {
                mockMvc
                                .perform(MockMvcRequestBuilders.post("/aluno/reativar/%d".formatted(500)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro")
                                                .value("Aluno não encontrado ou não possui conta desativada."));

        }

        @Test
        @DisplayName("Deve falhar ao tentar reativar conta de um aluno com conta ativa e retornar status 404")
        void givenTenhoUmIdDeUmAlunoComContaAtivaWhenChamoOEndPointParaReativarPassandoOIdThenRetonarStatus404()
                        throws Exception {
                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .post("/aluno/reativar/%d".formatted(alunoContaAtivada.getId())))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro")
                                                .value("Aluno não encontrado ou não possui conta desativada."));
        }

        @Test
        @DisplayName("Deve ser possível atualizar dados do aluno e retornar status 200")
        void givenTenhoUmAtualizarDadosAlunoDtoNoFormatoJsonWhenEnvioEsseJsonProEndPointDeAtualizarAlunoThenRetornarStatus200()
                        throws Exception {
                TesteUtils.login(alunoContaAtivada);
                String json = this.atualizarDadosAlunoDtoJson.write(this.atualizarDadosAlunoDtoMock).getJson();

                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .put("/aluno/%d".formatted(this.alunoContaAtivada.getId()))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(this.alunoContaAtivada.getId()))
                                .andExpect(jsonPath("$.email").value(this.alunoContaAtivada.getEmail()))
                                .andExpect(jsonPath("$.nome").value(this.atualizarDadosAlunoDtoMock.nome()))
                                .andExpect(jsonPath("$.sobrenome").value(this.atualizarDadosAlunoDtoMock.sobrenome()))
                                .andExpect(jsonPath("$.idade").value(this.atualizarDadosAlunoDtoMock.idade()))
                                .andExpect(jsonPath("$.grauEscolaridade")
                                                .value(this.atualizarDadosAlunoDtoMock.grauEscolaridade()));

                Aluno alunoAtualizado = this.alunoRepository.findById(this.alunoContaAtivada.getId()).get();
                assertEquals(this.atualizarDadosAlunoDtoMock.nome(), alunoAtualizado.getNome());
                assertEquals(this.atualizarDadosAlunoDtoMock.sobrenome(), alunoAtualizado.getSobrenome());
                assertEquals(this.atualizarDadosAlunoDtoMock.idade(), alunoAtualizado.getIdade());
                assertEquals(this.atualizarDadosAlunoDtoMock.grauEscolaridade(),
                                alunoAtualizado.getGrauEscolaridade().toString());
                assertTrue(passwordEncoder.matches(this.atualizarDadosAlunoDtoMock.senha(),
                                alunoAtualizado.getSenha()));
        }

        private static Stream<Arguments> argumentosInvalidosParaAtualizarAluno() {
                return Stream.of(
                                Arguments.of("1234567", "João", "Silva", 25L, "ENSINO_SUPERIOR_COMPLETO",
                                                "Senha deve conter 8 caracteres no mínimo"),
                                Arguments.of("12345678", "Ju", "Silva", 25L, "ENSINO_SUPERIOR_COMPLETO",
                                                "Nome deve conter 3 caracteres no mínimo e 20 no máximo"),
                                Arguments.of("12345678", "João", "P", 25L, "ENSINO_SUPERIOR_COMPLETO",
                                                "Sobrenome deve conter 2 caracteres no mínimo e 20 no máximo"),
                                Arguments.of("12345678", "João", "Silva", 6L, "ENSINO_SUPERIOR_COMPLETO",
                                                "Idade deve ser maior do que 6 e menor que 110"),
                                Arguments.of("12345678", "João", "Silva", 25L, "BACHAREL",
                                                "Grau de escolaridade inválido"));
        }

        @ParameterizedTest
        @MethodSource("argumentosInvalidosParaAtualizarAluno")
        @DisplayName("Deve retonar 400 ao terntar criar aluno com dados inválidos")
        void givenTenhoUmAtualizarDadosAlunoDtoNoFormatoJsonComDadosInvalidosWhenEnvioEsseJsonProEndPointDeAtualizarAlunoThenRetornarStatus400(
                        String senha, String nome, String sobrenome, Long idade, String escolaridade,
                        String mensagem) throws Exception {
                TesteUtils.login(this.alunoContaAtivada);
                this.atualizarDadosAlunoDtoMock = new AtualizarDadosAlunoDto(senha, nome, sobrenome, idade,
                                escolaridade);

                String json = this.atualizarDadosAlunoDtoJson.write(atualizarDadosAlunoDtoMock).getJson();
                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .put("/aluno/%d".formatted(this.alunoContaAtivada.getId()))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.erro").value(mensagem));

        }

        @Test
        @DisplayName("Deve ser possível deletar aluno e retornar status 200")
        void givenTenhoIdDeUmAlunoExistenteWhenEnvioEndPointDeDeletarAlunoThenRetornarStatus200()
                        throws Exception {
                TesteUtils.login(alunoContaAtivada);

                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .delete("/aluno/%d".formatted(this.alunoContaAtivada.getId())))
                                .andExpect(status().isOk())
                                .andReturn().getResponse();
                Boolean alunoFoiDeletado = this.alunoRepository
                                .findByIdAndDesativadaTrue(this.alunoContaAtivada.getId())
                                .isPresent();
                assertTrue(alunoFoiDeletado);
        }

        @Test
        @DisplayName("Não deve ser possível deletar aluno inexistente e retornar status 404")
        void givenTenhoIdDeUmAlunoInexistenteWhenEnvioEndPointDeDeletarAlunoThenRetornarStatus404()
                        throws Exception {
                TesteUtils.login(alunoContaAtivada);

                mockMvc
                                .perform(MockMvcRequestBuilders.delete("/aluno/%d".formatted(500)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro").value("Aluno não encontrado."));
        }

        @Test
        @DisplayName("Não deve ser possível deletar aluno que já está desativado e retornar status 404")
        void givenTenhoIdDeUmAlunoDeUmAlunoDesativadoWhenEnvioEndPointDeDeletarAlunoThenRetornarStatus404()
                        throws Exception {
                TesteUtils.login(alunoContaAtivada);

                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .delete("/aluno/%d".formatted(this.alunoContaDesativada.getId())))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro").value("Aluno não encontrado."));
        }

        @Test
        @DisplayName("Deve ser possível listar todos alunos cadastrados ativos e retornar status 200")
        void givenEstouLogadoNaAplicaçãoWhenEnvioEndPointDeListarTodosAlunosThenRetornarStatus200()
                        throws Exception {
                TesteUtils.login(alunoContaAtivada);

                MockHttpServletResponse resposta = mockMvc
                                .perform(MockMvcRequestBuilders.get("/aluno"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andReturn().getResponse();
                List<AlunoRespostaDto> alunos = this.objectMapper.readValue(resposta.getContentAsString(),
                                new TypeReference<List<AlunoRespostaDto>>() {
                                });
                List<Aluno> alunosAtivos = this.alunosSalvos.stream().filter(aluno -> !aluno.isDesativada()).toList();
                assertThat(resposta.getStatus()).isEqualTo(HttpStatus.OK.value());
                assertEquals(alunosAtivos.size(), alunos.size());
        }

        @Test
        @DisplayName("Deve buscar por aluno ativo cadastrados e retornar status 200")
        void givenPossuoUmAlunoIdDeUmAlunoAtivadoWhenEnvioEndPointDePegarUmAlunoThenRetornarStatus200()
                        throws Exception {
                TesteUtils.login(alunoContaAtivada);
                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .get("/aluno/%d".formatted(this.alunoContaAtivada.getId())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(this.alunoContaAtivada.getId()))
                                .andExpect(jsonPath("$.email").value(this.alunoContaAtivada.getEmail()))
                                .andExpect(jsonPath("$.nome").value(this.alunoContaAtivada.getNome()))
                                .andExpect(jsonPath("$.sobrenome").value(this.alunoContaAtivada.getSobrenome()))
                                .andExpect(jsonPath("$.idade").value(this.alunoContaAtivada.getIdade()))
                                .andExpect(jsonPath("$.grauEscolaridade")
                                                .value(this.alunoContaAtivada.getGrauEscolaridade().toString()));
        }

        @Test
        @DisplayName("Deve falhar ao buscar por aluno desativado ou inexistente e retornar status 404")
        void givenPossuoUmAlunoIdInexistenteOuDeUmAlunoDesativadoEnvioEndPointDePegarUmAlunoThenRetornarStatus404()
                        throws Exception {
                TesteUtils.login(alunoContaAtivada);

                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .get("/aluno/%d".formatted(this.alunoContaDesativada.getId())))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro").value("Aluno não encontrado."));

        }
}
