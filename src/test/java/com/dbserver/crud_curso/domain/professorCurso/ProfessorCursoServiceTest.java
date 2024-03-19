package com.dbserver.crud_curso.domain.professorCurso;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.List;
import java.util.NoSuchElementException;
import com.dbserver.crud_curso.domain.curso.Curso;
import com.dbserver.crud_curso.domain.curso.CursoRepository;
import com.dbserver.crud_curso.domain.professor.Professor;
import com.dbserver.crud_curso.domain.professor.ProfessorRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ProfessorCursoServiceTest {
    @InjectMocks
    private ProfessorCursoService professorCursoService;

    @Mock
    private ProfessorCursoRepository professorCursoRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private Pageable pageable;

    private Curso cursoBacharelMock;
    private Curso cursoMestreMock;
    private Professor professorMock;
    private ProfessorCurso professorCursoMock;

    @BeforeEach
    void configuracao() {
        this.professorMock = new Professor("exemplo@email.com",
                "senha123",
                "João",
                "Silva",
                25L,
                "BACHAREL");
        this.cursoBacharelMock = new Curso("Curso de Lógica de Programação",
                6L,
                "ENSINO_MEDIO_INCOMPLETO",
                "BACHAREL");
        this.cursoMestreMock = new Curso("Curso de Lógica de Programação",
                6L,
                "ENSINO_SUPERIOR_COMPLETO",
                "MESTRE");

        this.professorCursoMock = new ProfessorCurso(professorMock, cursoBacharelMock, true);
    }

    @Test
    @DisplayName("Deve ser possível cadastrar um professor que não estava cadastrado no curso corretamente")
    void givenTenhoUmProfessorIdEUmCursoIdExistentesWhenCadastroProfessorNoCursoThenRetornarCadastroDoProfessorNoCurso() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoBacharelMock));
        when(this.professorRepository.findById(1L)).thenReturn(Optional.of(this.professorMock));
        when(this.professorCursoRepository.findByProfessorIdAndCursoId(1L, 1L)).thenReturn(Optional.empty());

        ProfessorCurso resposta = this.professorCursoService.cadastrarProfessorNoCurso(1L, 1L);

        assertEquals(this.professorMock.getEmail(), resposta.getProfessor().getEmail());
        assertEquals(this.professorMock.getSenha(), resposta.getProfessor().getSenha());
        assertEquals(this.professorMock.getNome(), resposta.getProfessor().getNome());
        assertEquals(this.professorMock.getSobrenome(), resposta.getProfessor().getSobrenome());
        assertEquals(this.professorMock.getIdade(), resposta.getProfessor().getIdade());
        assertEquals(this.professorMock.getGrauAcademico().toString(),
                resposta.getProfessor().getGrauAcademico().toString());

        assertEquals(this.cursoBacharelMock.getTitulo(), resposta.getCurso().getTitulo());
        assertEquals(this.cursoBacharelMock.getDuracaoMeses(), resposta.getCurso().getDuracaoMeses());
        assertEquals(this.cursoBacharelMock.getGrauAcademicoMinimo().toString(),
                resposta.getCurso().getGrauAcademicoMinimo().toString());
        assertEquals(this.cursoBacharelMock.getGrauEscolarMinimo().toString(),
                resposta.getCurso().getGrauEscolarMinimo().toString());

        assertTrue(resposta.isAtivo());
        assertFalse(resposta.isCriador());
    }

    @Test
    @DisplayName("Deve ser possível recadastrar um professor que estava com status ativo false")
    void givenTenhoUmProfessorCadastradoNoCursoComStatusAtivoFalseWhenCadastroProfessorNoCursoThenRetornarCadastroDoProfessorNoCurso() {
        this.professorCursoMock.setAtivo(false);
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoBacharelMock));
        when(this.professorRepository.findById(1L)).thenReturn(Optional.of(this.professorMock));
        when(this.professorCursoRepository.findByProfessorIdAndCursoId(1L, 1L))
                .thenReturn(Optional.of(this.professorCursoMock));

        ProfessorCurso resposta = this.professorCursoService.cadastrarProfessorNoCurso(1L, 1L);

        assertEquals(this.professorMock.getEmail(), resposta.getProfessor().getEmail());
        assertEquals(this.professorMock.getSenha(), resposta.getProfessor().getSenha());
        assertEquals(this.professorMock.getNome(), resposta.getProfessor().getNome());
        assertEquals(this.professorMock.getSobrenome(), resposta.getProfessor().getSobrenome());
        assertEquals(this.professorMock.getIdade(), resposta.getProfessor().getIdade());
        assertEquals(this.professorMock.getGrauAcademico().toString(),
                resposta.getProfessor().getGrauAcademico().toString());

        assertEquals(this.cursoBacharelMock.getTitulo(), resposta.getCurso().getTitulo());
        assertEquals(this.cursoBacharelMock.getDuracaoMeses(), resposta.getCurso().getDuracaoMeses());
        assertEquals(this.cursoBacharelMock.getGrauAcademicoMinimo().toString(),
                resposta.getCurso().getGrauAcademicoMinimo().toString());
        assertEquals(this.cursoBacharelMock.getGrauEscolarMinimo().toString(),
                resposta.getCurso().getGrauEscolarMinimo().toString());

        assertTrue(resposta.isAtivo());
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um professor ativo no curso novamente ")
    void givenTenhoUmProfessorCadastradoNoCursoComStatusAtivoTrueWhenCadastroProfessorNoCursoThenRetornarErro() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoBacharelMock));
        when(this.professorRepository.findById(1L)).thenReturn(Optional.of(this.professorMock));
        when(this.professorCursoRepository.findByProfessorIdAndCursoId(1L, 1L))
                .thenReturn(Optional.of(this.professorCursoMock));

        assertThrows(IllegalArgumentException.class,
                () -> this.professorCursoService.cadastrarProfessorNoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um professor em um curso inexistente")
    void givenTenhoUmProfessorIdExistenteEUmCursoIdInexistenteWhenCadastroProfessorNoCursoThenRetornarErro() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> this.professorCursoService.cadastrarProfessorNoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um professor inexistente em um curso")
    void givenTenhoUmProfessorIdInexistenteEUmCursoIdExistenteWhenCadastroProfessorNoCursoThenRetornarErro() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoBacharelMock));
        when(this.professorRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> this.professorCursoService.cadastrarProfessorNoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um professor com grau academico menor que o exigido no curso")
    void givenTenhoUmProfessorComGrauAcademicoMenorQueOExigidoNoCursoWhenCadastroProfessorNoCursoThenRetornarErro() {
        when(this.cursoRepository.findById(1L)).thenReturn(Optional.of(this.cursoMestreMock));
        when(this.professorRepository.findById(1L)).thenReturn(Optional.of(this.professorMock));
        when(this.professorCursoRepository.findByProfessorIdAndCursoId(1L, 1L))
                .thenReturn(Optional.of(this.professorCursoMock));
        assertThrows(IllegalArgumentException.class,
                () -> this.professorCursoService.cadastrarProfessorNoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Deve ser possível atualizar o status ativo de um professor no curso")
    void givenTenhoUmProfessorIdEUmCursoIdEUmNovoDadoParaAtivoWhenAtualizoProfessorNoCursoThenRetornarProfessorNoCurso() {

        when(this.professorCursoRepository.findByProfessorIdAndCursoId(1L, 1L))
                .thenReturn(Optional.of(this.professorCursoMock));

        ProfessorCurso resposta = this.professorCursoService.atualizarStatusAtivoProfessor(1L, 1L, false);

        assertEquals(this.professorMock.getEmail(), resposta.getProfessor().getEmail());
        assertEquals(this.professorMock.getSenha(), resposta.getProfessor().getSenha());
        assertEquals(this.professorMock.getNome(), resposta.getProfessor().getNome());
        assertEquals(this.professorMock.getSobrenome(), resposta.getProfessor().getSobrenome());
        assertEquals(this.professorMock.getIdade(), resposta.getProfessor().getIdade());
        assertEquals(this.professorMock.getGrauAcademico().toString(),
                resposta.getProfessor().getGrauAcademico().toString());

        assertEquals(this.cursoBacharelMock.getTitulo(), resposta.getCurso().getTitulo());
        assertEquals(this.cursoBacharelMock.getDuracaoMeses(), resposta.getCurso().getDuracaoMeses());
        assertEquals(this.cursoBacharelMock.getGrauAcademicoMinimo().toString(),
                resposta.getCurso().getGrauAcademicoMinimo().toString());
        assertEquals(this.cursoBacharelMock.getGrauEscolarMinimo().toString(),
                resposta.getCurso().getGrauEscolarMinimo().toString());

        assertFalse(resposta.isAtivo());
    }

    @Test
    @DisplayName("Não deve ser possível atualizar o status ativo de um professor que não está no curso ")
    void givenTenhoUmProfessorIdOuCursoIdInexistenteEUmNovoDadoParaAtivoWhenAtualizoProfessorNoCursoThenRetornarErro() {

        when(this.professorCursoRepository.findByProfessorIdAndCursoId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> this.professorCursoService.atualizarStatusAtivoProfessor(1L, 1L, false));
    }

    @Test
    @DisplayName("Deve ser possível remover um professor do curso, atualizando o status ativo para false")
    void givenTenhoUmProfessorIdEUmCursoIdWhenRemovoProfessorDoCursoThenNaoDeveLancarErro() {

        when(this.professorCursoRepository.findByProfessorIdAndCursoId(1L, 1L))
                .thenReturn(Optional.of(this.professorCursoMock));
        when(this.professorCursoRepository.findByProfessorIdAndCursoIdAndAtivoTrue(1L, 1L))
                .thenReturn(Optional.of(this.professorCursoMock));

        assertDoesNotThrow(() -> this.professorCursoService.removerProfessorDoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Não deve ser possível remover um professor do curso")
    void givenTenhoUmProfessorIdQueNãoEstáAtivoOuCadastradoNoCursoIdWhenRemovoProfessorDoCursoThenNaoDeveLancarErro() {

        when(this.professorCursoRepository.findByProfessorIdAndCursoIdAndAtivoTrue(1L, 1L))
                .thenReturn(Optional.empty());
        when(this.professorCursoRepository.findByProfessorIdAndCursoIdAndAtivoTrue(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> this.professorCursoService.removerProfessorDoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Deve ser possível listar todos professores ativos do curso")
    void givenTenhoUmCursoIdWhenListoTodosProfessoresAtivosDoCursoThenRetornarUmaListaDeProfessorCurso() {
        List<ProfessorCurso> listaDeProfessores = List.of(this.professorCursoMock, this.professorCursoMock);
        when(this.professorCursoRepository.findAllByCursoIdAndAtivoTrue(1L, this.pageable))
                .thenReturn(new PageImpl<>(listaDeProfessores));
        List<ProfessorCurso> professores = this.professorCursoService.listarTodosProfessoresAtivosDoCurso(1L, pageable);

        assertEquals(listaDeProfessores.size(), professores.size());
    }

    @Test
    @DisplayName("Deve ser retornar uma lista vazia caso não encontre professores no curso")
    void givenTenhoUmCursoIdWhenListoTodosProfessoresAtivosDoCursoThenRetornarUmaListaVazia() {
        List<ProfessorCurso> listaDeProfessores = List.of();
        when(this.professorCursoRepository.findAllByCursoIdAndAtivoTrue(1L, this.pageable))
                .thenReturn(new PageImpl<>(listaDeProfessores));
        List<ProfessorCurso> professores = this.professorCursoService.listarTodosProfessoresAtivosDoCurso(1L, pageable);

        assertEquals(listaDeProfessores.size(), professores.size());
    }

    @Test
    @DisplayName("Deve ser retornar o um ProfessorCurso buscado")
    void givenTenhoUmCursoIdEOIdDeUmProfessorAtivoTrueWhenListoBuscaOProfessorNoCursoThenRetornarProfessor() {
        when(this.professorCursoRepository.findByProfessorIdAndCursoIdAndAtivoTrue(1L, 1L))
                .thenReturn(Optional.of(this.professorCursoMock));
        ProfessorCurso resposta = this.professorCursoService.pegarProfessorDoCurso(1L, 1L);

        assertEquals(this.professorMock.getEmail(), resposta.getProfessor().getEmail());
        assertEquals(this.professorMock.getSenha(), resposta.getProfessor().getSenha());
        assertEquals(this.professorMock.getNome(), resposta.getProfessor().getNome());
        assertEquals(this.professorMock.getSobrenome(), resposta.getProfessor().getSobrenome());
        assertEquals(this.professorMock.getIdade(), resposta.getProfessor().getIdade());
        assertEquals(this.professorMock.getGrauAcademico().toString(),
                resposta.getProfessor().getGrauAcademico().toString());

        assertEquals(this.cursoBacharelMock.getTitulo(), resposta.getCurso().getTitulo());
        assertEquals(this.cursoBacharelMock.getDuracaoMeses(), resposta.getCurso().getDuracaoMeses());
        assertEquals(this.cursoBacharelMock.getGrauAcademicoMinimo().toString(),
                resposta.getCurso().getGrauAcademicoMinimo().toString());
        assertEquals(this.cursoBacharelMock.getGrauEscolarMinimo().toString(),
                resposta.getCurso().getGrauEscolarMinimo().toString());

        assertTrue(resposta.isAtivo());
    }

    @Test
    @DisplayName("Deve ser retornar um erro ao não encontrar professorCurso ativo true no curso informado")
    void givenTenhoUmCursoIdEUmProfessorQueNaoEstaAtivoOuCadastradoNoCursoWhenListoBuscaOProfessorNoCursoThenRetornarUmErro() {
        when(this.professorCursoRepository.findByProfessorIdAndCursoIdAndAtivoTrue(1L, 1L))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> this.professorCursoService.pegarProfessorDoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Deve retornar true ao encontrar professor no curso")
    void givenPossuoUmCursoIdEProfessorIdExistentesWhenVerificoSeProfessorEstaNoCursoThenRetornarTrue() {
        when(this.professorCursoRepository.findByProfessorIdAndCursoId(1L, 1L))
                .thenReturn(Optional.of(this.professorCursoMock));
        assertTrue(this.professorCursoService.verificarSeProfessorEstaCadastradoNoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Deve retornar False ao não encontrar professor no curso")
    void givenPossuoUmCursoIdEProfessorIdExistentesWhenVerificoSeProfessorEstaNoCursoThenRetornarFalse() {
        when(this.professorCursoRepository.findByProfessorIdAndCursoId(1L, 1L)).thenReturn(Optional.empty());
        assertFalse(this.professorCursoService.verificarSeProfessorEstaCadastradoNoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Deve retornar True ao encontrar professor ativo true no curso")
    void givenPossuoUmCursoIdEProfessorIdWhenVerificoSeProfessorEstaNoCursoThenRetornarTrue() {
        when(this.professorCursoRepository.findByProfessorIdAndCursoIdAndAtivoTrue(1L, 1L))
                .thenReturn(Optional.of(this.professorCursoMock));
        assertTrue(this.professorCursoService.verificarSeProfessorEstaAtivoNoCurso(1L, 1L));
    }

    @Test
    @DisplayName("Deve retornar False ao não encontrar professor ativo true no curso")
    void givenPossuoUmCursoIdEProfessorIdWhenVerificoSeProfessorEstaNoCursoThenRetornarFalse() {
        when(this.professorCursoRepository.findByProfessorIdAndCursoIdAndAtivoTrue(1L, 1L))
                .thenReturn(Optional.of(this.professorCursoMock));
        assertTrue(this.professorCursoService.verificarSeProfessorEstaAtivoNoCurso(1L, 1L));
    }
}
