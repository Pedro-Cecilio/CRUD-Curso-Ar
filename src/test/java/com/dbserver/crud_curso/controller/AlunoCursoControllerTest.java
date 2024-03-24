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
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.dbserver.crud_curso.controller.utils.TesteUtils;
import com.dbserver.crud_curso.domain.aluno.Aluno;
import com.dbserver.crud_curso.domain.aluno.AlunoRepository;
import com.dbserver.crud_curso.domain.alunoCurso.AlunoCurso;
import com.dbserver.crud_curso.domain.alunoCurso.AlunoCursoRepository;
import com.dbserver.crud_curso.domain.alunoCurso.dto.DadosEntradaAlunoCurso;
import com.dbserver.crud_curso.domain.curso.Curso;
import com.dbserver.crud_curso.domain.curso.CursoRepository;
import com.dbserver.crud_curso.domain.enums.StatusMatricula;
import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlunoCursoControllerTest {

        private MockMvc mockMvc;
        private JacksonTester<DadosEntradaAlunoCurso> dadosEntradaAlunoCursoJson;
        private AlunoRepository alunoRepository;
        private CursoRepository cursoRepository;
        private ProfessorRepository professorRepository;
        private AlunoCursoRepository alunoCursoRepository;
        private List<Curso> cursosSalvos;
        private List<Aluno> alunosSalvos;
        private DadosEntradaAlunoCurso dadosEntradaAlunoCurso;
        private Professor professor;
        private ObjectMapper objectMapper;
        private AlunoCurso alunoCurso;

        @Autowired
        public AlunoCursoControllerTest(MockMvc mockMvc,
                        CursoRepository cursoRepository, AlunoCursoRepository alunoCursoRepository,
                        AlunoRepository alunoRepository, ProfessorRepository professorRepository,
                        JacksonTester<DadosEntradaAlunoCurso> dadosEntradaAlunoCursoJson,
                        ObjectMapper objectMapper) {
                this.mockMvc = mockMvc;
                this.cursoRepository = cursoRepository;
                this.alunoRepository = alunoRepository;
                this.alunoCursoRepository = alunoCursoRepository;
                this.professorRepository = professorRepository;
                this.dadosEntradaAlunoCursoJson = dadosEntradaAlunoCursoJson;
                this.objectMapper = objectMapper;

        }

        private void popularBanco() {
                Aluno alunoSuperiorCompleto = new Aluno(
                                "exemplo@email.com",
                                "senha123",
                                "João",
                                "Silva",
                                25L,
                                "ENSINO_SUPERIOR_COMPLETO");
                Aluno alunoMedioIncompleto = new Aluno(
                                "exemplo2@email.com",
                                "senha123",
                                "Lucas",
                                "Silva",
                                16L,
                                "ENSINO_MEDIO_INCOMPLETO");

                Professor professor = new Professor("professorContaAtivada@email.com",
                                "senha123",
                                "João",
                                "Silva",
                                25L,
                                "BACHAREL");

                Curso cursoMedioIncompleto = new Curso("Curso de Lógica de Programação",
                                6L,
                                "ENSINO_MEDIO_INCOMPLETO",
                                "BACHAREL");
                Curso cursoSuperiorIncompleto = new Curso("Curso de QA",
                                12L,
                                "ENSINO_SUPERIOR_INCOMPLETO",
                                "MESTRE");

                this.professor = this.professorRepository.save(professor);
                this.alunosSalvos = this.alunoRepository.saveAll(List.of(alunoSuperiorCompleto, alunoMedioIncompleto));
                this.cursosSalvos = this.cursoRepository
                                .saveAll(List.of(cursoMedioIncompleto, cursoSuperiorIncompleto));

                AlunoCurso alunoCurso = new AlunoCurso(alunoSuperiorCompleto, cursoMedioIncompleto);
                this.alunoCurso = this.alunoCursoRepository.save(alunoCurso);
        }

        @BeforeEach
        void prepararTeste() {

                this.popularBanco();
        }

        @AfterEach
        void limparTeste() {
                this.alunoRepository.deleteAll();
                this.cursoRepository.deleteAll();
                this.professorRepository.deleteAll();
                this.alunoCursoRepository.deleteAll();
        }

        @Test
        @DisplayName("Como aluno, deve ser possível se cadastrar em um curso")
        void givenEstouLogadoComoAlunoEPossuoIdDoCursoWhenTentoMeCadastrarEmUmCursoThenRetornarStatus200()
                        throws Exception {
                Aluno alunoLogado = TesteUtils.login(this.alunosSalvos.get(1));
                Curso cursoASerCadastrado = this.cursosSalvos.get(0);
                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .post("/alunoCurso/%d".formatted(cursoASerCadastrado.getId())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").isNumber())
                                .andExpect(jsonPath("$.aluno.id").value(alunoLogado.getId()))
                                .andExpect(jsonPath("$.curso.id").value(cursoASerCadastrado.getId()))
                                .andExpect(jsonPath("$.statusMatricula").value(StatusMatricula.ATIVO.toString()));
        }

        @Test
        @DisplayName("Não deve ser possível se cadastrar em um curso inexistente")
        void givenEstouLogadoComoAlunoEPossuoIdDeUmCursoInexistenteWhenTentoMeCadastrarEmUmCursoThenRetornarStatus404()
                        throws Exception {
                TesteUtils.login(this.alunosSalvos.get(0));
                mockMvc
                                .perform(MockMvcRequestBuilders.post("/alunoCurso/%d".formatted(500)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro").value("Curso não encontrado."));
        }

        @Test
        @DisplayName("Como professor, deve ser possível formar um aluno no curso")
        void givenEstouLogadoComoProfessorEPossuoDadosEntradaAlunoCursoEAlunoIdWhenTentoFormarOAlunoNoCursoThenRetornarStatus200()
                        throws Exception {
                TesteUtils.login(this.professor);

                this.dadosEntradaAlunoCurso = new DadosEntradaAlunoCurso(this.alunoCurso.getCurso().getId(),
                                this.alunoCurso.getAluno().getId());

                String json = this.dadosEntradaAlunoCursoJson.write(this.dadosEntradaAlunoCurso).getJson();

                mockMvc
                                .perform(MockMvcRequestBuilders.patch("/alunoCurso/formar")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusMatricula").value(StatusMatricula.FORMADO.toString()));
        }

        @Test
        @DisplayName("Como professor, não deve ser possível formar um aluno que não está cadastrado no curso")
        void givenEstouLogadoComoProfessorEPossuoDadosEntradaAlunoCursoEAlunoIdInexistenteWhenTentoFormarOAlunoNoCursoThenRetornarStatus200()
                        throws Exception {
                TesteUtils.login(this.professor);
                Aluno alunoASeFormar = this.alunosSalvos.get(1);

                this.dadosEntradaAlunoCurso = new DadosEntradaAlunoCurso(alunoASeFormar.getId(),
                                this.alunoCurso.getAluno().getId());

                String json = this.dadosEntradaAlunoCursoJson.write(this.dadosEntradaAlunoCurso).getJson();

                mockMvc
                                .perform(MockMvcRequestBuilders.patch("/alunoCurso/formar")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro").value("O aluno informado não está cadastrado no curso"));
        }

        @Test
        @DisplayName("Como aluno, deve ser possível trancar a matricula de um curso")
        void givenEstouLogadoComoAlunoEPossuoCursoIdWhenTentoTrancarMatriculaNoCursoThenRetornarStatus200()
                        throws Exception {
                TesteUtils.login(this.alunoCurso.getAluno());
                Curso cursoASeFormar = this.alunoCurso.getCurso();

                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .patch("/alunoCurso/trancarMatricula/%d"
                                                                .formatted(cursoASeFormar.getId())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusMatricula").value(StatusMatricula.INATIVO.toString()));
        }

        @Test
        @DisplayName("Como aluno, não deve ser possível trancar matrícula em um curso inexistente")
        void givenEstouLogadoComoAlunoEPossuoCursoIdInexistenteWhenTentoFormarOAlunoNoCursoThenRetornarStatus404()
                        throws Exception {
                TesteUtils.login(this.alunosSalvos.get(0));

                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .patch("/alunoCurso/trancarMatricula/%d".formatted(500)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro").value("O aluno informado não está cadastrado no curso"));
        }

        @Test
        @DisplayName("Como aluno, não deve ser possível trancar matrícula passando como id do curso algo que não seja um long")
        void givenEstouLogadoComoAlunoEPossuoCursoIdComFormatoInválidoWhenTentoFormarOAlunoNoCursoThenRetornarStatus404()
                        throws Exception {
                TesteUtils.login(this.alunosSalvos.get(0));

                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .patch("/alunoCurso/trancarMatricula/%s".formatted("50ab")))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro").value("Curso não encontrado."));
        }

        @Test
        @DisplayName("Como aluno, deve ser possível reativar a matricula de um curso")
        void givenEstouLogadoComoAlunoEPossuoCursoIdWhenTentoReativarMatriculaDoCursoThenRetornarStatus200()
                        throws Exception {
                TesteUtils.login(this.alunosSalvos.get(0));
                Curso cursoAReativarMatricula = this.cursosSalvos.get(0);
                this.alunoCurso.setStatusMatricula(StatusMatricula.INATIVO.toString());
                this.alunoCursoRepository.save(this.alunoCurso);

                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .patch("/alunoCurso/reativarMatricula/%d"
                                                                .formatted(cursoAReativarMatricula.getId())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusMatricula").value(StatusMatricula.ATIVO.toString()));
        }

        @Test
        @DisplayName("Como aluno, não deve ser possível reativar matrícula em um curso inexistente")
        void givenEstouLogadoComoAlunoEPossuoCursoIdInexistenteWhenTentoReativarMatriculaThenRetornarStatus404()
                        throws Exception {
                TesteUtils.login(this.alunosSalvos.get(0));

                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .patch("/alunoCurso/reativarMatricula/%d".formatted(500)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro").value("O aluno informado não está cadastrado no curso"));
        }

        @Test
        @DisplayName("Como aluno, não deve ser possível reativar matrícula passando como id do curso algo que não seja um long")
        void givenEstouLogadoComoAlunoEPossuoCursoIdComFormatoInválidoWhenReativarMatriculaThenRetornarStatus404()
                        throws Exception {
                TesteUtils.login(this.alunosSalvos.get(0));

                mockMvc
                                .perform(MockMvcRequestBuilders
                                                .patch("/alunoCurso/reativarMatricula/%s".formatted("50ab")))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro").value("Curso não encontrado."));
        }

        @Test
        @DisplayName("Deve ser possível listar todos alunos cadastrados no curso retornar status 200")
        void givenPossuoCursoIdWhenEnvioEndPointDeListarTodosAlunosDoCursoThenRetornarStatus200()
                        throws Exception {
                TesteUtils.login(this.alunoCurso.getAluno());
                Curso cursoASerBuscado = this.alunoCurso.getCurso();
                AlunoCurso novoAlunoNoCurso = new AlunoCurso(this.alunosSalvos.get(1),
                                cursoASerBuscado);
                this.alunoCursoRepository.save(novoAlunoNoCurso);
                List<AlunoCurso> alunosNoCurso = this.alunoCursoRepository.findAllByCursoId(cursoASerBuscado.getId());

                MockHttpServletResponse resposta = mockMvc
                                .perform(MockMvcRequestBuilders
                                                .get("/alunoCurso/%d".formatted(cursoASerBuscado.getId())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andReturn().getResponse();
                List<AlunoCurso> listaAlunoCurso = this.objectMapper.readValue(resposta.getContentAsString(),
                                new TypeReference<List<AlunoCurso>>() {
                                });
                assertEquals(alunosNoCurso.size(), listaAlunoCurso.size());
        }

        @Test
        @DisplayName("Não Deve ser possível listar todos alunos cadastrados no curso ao enviar cursoId no formato inválido retornar status 404")
        void givenPossuoCursoIdNoFormatoInválidoWhenEnvioEndPointDeListarTodosAlunosDoCursoThenRetornarStatus404()
                        throws Exception {
                TesteUtils.login(this.alunosSalvos.get(0));

                mockMvc
                                .perform(MockMvcRequestBuilders.get("/alunoCurso/%s".formatted("5615fa")))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro").value("Curso não encontrado."));
        }

        @Test
        @DisplayName("Deve ser possível buscar por um aluno cadastrado no curso retornar status 200")
        void givenPossuoUmDadosEntradaAlunoCursoWhenEnvioEndPointDePegarAlunoDoCursoThenRetornarStatus200()
                        throws Exception {
                TesteUtils.login(this.alunosSalvos.get(0));

                mockMvc
                                .perform(MockMvcRequestBuilders.get("/alunoCurso/aluno")
                                                .param("alunoId",
                                                                String.valueOf(this.alunoCurso.getAluno().getId()))
                                                .param("cursoId", String.valueOf(this.alunoCurso.getCurso().getId())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(this.alunoCurso.getId()))
                                .andExpect(jsonPath("$.aluno.id").value(this.alunoCurso.getAluno().getId()));
        }

        @Test
        @DisplayName("Não deve ser possível buscar por um aluno que não esteja cadastrado no curso informado e deve retornar status 404")
        void givenPossuoUmDadosEntradaAlunoCursoComAlunoIdNãoPetencenteAoCursoDoCursoIdWhenEnvioEndPointDePegarAlunoDoCursoThenRetornarStatus404()
                        throws Exception {
                TesteUtils.login(this.alunosSalvos.get(0));
                Curso cursoASerBuscado = this.cursosSalvos.get(1);

                mockMvc
                                .perform(MockMvcRequestBuilders.get("/alunoCurso/aluno")
                                                .param("alunoId", "500")
                                                .param("cursoId", String.valueOf(cursoASerBuscado.getId())))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.erro").value("Aluno não encontrado"));
        }

}
