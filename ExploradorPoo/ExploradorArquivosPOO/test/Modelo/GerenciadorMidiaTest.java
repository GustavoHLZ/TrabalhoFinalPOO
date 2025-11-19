package Modelo;

import excecoes.MidiaJaCadastradaException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Plano de Testes unitários para validar a lógica da classe {@link GerenciadorMidia}.
 * <p>
 * Esta classe utiliza o framework JUnit 4.
 * <b>Estratégia de Teste:</b> Para evitar a dependência de arquivos de mídia reais (mp3, mp4),
 * os testes criam arquivos fictícios com extensão <code>.tpoo</code> para simular
 * a existência física dos arquivos no disco.
 * </p>
 *
 * @author Seu Nome
 * @version 1.0
 * @see GerenciadorMidia
 */
public class GerenciadorMidiaTest {

    private GerenciadorMidia gerenciador;
    private Filme filmeTeste;

    /**
     * Nome do arquivo fictício usado como base para os testes.
     */
    private final String NOME_BASE_ARQUIVO = "teste_filme_original.tpoo";
    private final String ARQUIVO_DB = "database.csv";
    private File pastaTemporaria;

    // --- CONFIGURAÇÃO E LIMPEZA (SETUP/TEARDOWN) ---

    /**
     * Configuração inicial executada antes de <b>cada</b> teste.
     * <p>
     * 1. Cria um arquivo físico vazio no disco para simular a mídia.<br>
     * 2. Instancia um objeto {@link Filme} apontando para esse arquivo.<br>
     * 3. Inicializa o {@link GerenciadorMidia}.<br>
     * 4. Cria uma pasta temporária para testes de movimentação.
     * </p>
     *
     * @throws IOException Se houver erro na criação dos arquivos de teste.
     */
    @Before
    public void setUp() throws IOException {
        // 1- Cria o arquivo fake no disco com extensão tpoo
        new File(NOME_BASE_ARQUIVO).createNewFile();

        // 2- Cria o objeto Filme para um arquivo fake no disco
        filmeTeste = new Filme("Matrix", NOME_BASE_ARQUIVO, 120, "Ação", "Inglês", "0");

        // 3- Inicializa o gerenciador de Midia
        gerenciador = new GerenciadorMidia();

        // 4 - Cria pasta temporária para o teste de mover
        pastaTemporaria = new File("temp_test_move");
        pastaTemporaria.mkdir();
    }

    /**
     * Limpeza executada após <b>cada</b> teste.
     * <p>
     * Garante que o ambiente esteja limpo para o próximo teste, removendo:
     * arquivos criados, banco de dados CSV, arquivos .tpoo gerados e pastas temporárias.
     * </p>
     */
    @After
    public void tearDown() {
        // 1. Deleta arquivos gerados no teste
        new File(NOME_BASE_ARQUIVO).delete();
        new File("O_Grande_Filme_Novo.tpoo").delete();
        new File("TituloTeste.tpoo").delete();
        new File("TesteTPOO.tpoo").delete();

        // 2. Limpa arquivos dentro da pasta temporária
        if (pastaTemporaria.exists()) {
            File[] arquivos = pastaTemporaria.listFiles();
            if (arquivos != null) {
                for (File f : arquivos) f.delete();
            }
            pastaTemporaria.delete();
        }

        // 3. Limpa o arquivo de persistência (Banco de dados)
        new File(ARQUIVO_DB).delete();

        // 4. Limpa arquivos .tpoo auxiliares gerados pelo sistema
        new File("matrix.tpoo").delete();
        new File("Matrix.tpoo").delete();
    }

    // --- TESTES BÁSICOS ---

    /**
     * Testa o fluxo básico de adição de uma nova mídia.
     * <p>
     * Verifica se, após adicionar, o método {@code buscarMidiaPorLocal} consegue
     * encontrar o objeto registrado.
     * </p>
     * @throws Exception Em caso de falha inesperada.
     */
    @Test
    public void testAdicionarMidia() throws Exception {
        gerenciador.adicionarMidia(filmeTeste);
        assertNotNull("Mídia deve ser encontrada após adição.", gerenciador.buscarMidiaPorLocal(NOME_BASE_ARQUIVO));
    }

    /**
     * Testa a validação de duplicidade.
     * <p>
     * Espera-se que a exceção {@link MidiaJaCadastradaException} seja lançada
     * ao tentar adicionar a mesma mídia duas vezes.
     * </p>
     * @throws Exception Se a exceção esperada não ocorrer ou ocorrer outro erro.
     */
    @Test(expected = MidiaJaCadastradaException.class)
    public void testAdicionarMidiaDuplicadaDeveLancarExcecao() throws Exception {
        gerenciador.adicionarMidia(filmeTeste);
        gerenciador.adicionarMidia(filmeTeste); // Deve falhar aqui
    }

    /**
     * Testa a remoção de uma mídia.
     * <p>
     * Verifica se o objeto não é mais retornado pela busca após a chamada do método remover.
     * </p>
     * @throws Exception Em caso de falha inesperada.
     */
    @Test
    public void testRemoverMidia() throws Exception {
        gerenciador.adicionarMidia(filmeTeste);
        gerenciador.removerMidia(filmeTeste);
        assertNull("Mídia deve ser removida da lista.", gerenciador.buscarMidiaPorLocal(NOME_BASE_ARQUIVO));
    }

    /**
     * Testa o processo de renomear um arquivo de mídia.
     * <p>
     * Verifica três aspectos:
     * 1. Se o título do objeto na memória foi atualizado.
     * 2. Se o caminho do arquivo no objeto foi atualizado.
     * 3. Se o arquivo físico foi realmente renomeado no sistema operacional.
     * </p>
     * @throws Exception Em caso de falha de I/O.
     */
    @Test
    public void testRenomearArquivoMidiaEAtualizaObjeto() throws Exception {
        gerenciador.adicionarMidia(filmeTeste);

        String NOVO_NOME = "O_Grande_Filme_Novo";
        String NOVO_CAMINHO_ESPERADO = NOVO_NOME + ".tpoo";

        // Ação: Renomear
        gerenciador.renomearArquivoMidia(filmeTeste, NOVO_NOME);

        // Verificação
        assertEquals(NOVO_NOME, filmeTeste.getTitulo());
        assertTrue("Caminho deve terminar com .tpoo", filmeTeste.getLocal().endsWith(NOVO_CAMINHO_ESPERADO));
        assertTrue("Novo arquivo deve existir", new File(NOVO_CAMINHO_ESPERADO).exists());
        assertFalse("Arquivo antigo deve sumir", new File(NOME_BASE_ARQUIVO).exists());
    }

    /**
     * Testa a movimentação de uma mídia para outra pasta.
     * <p>
     * Verifica se o arquivo físico foi movido para a pasta temporária e se
     * o atributo de caminho do objeto foi atualizado corretamente.
     * </p>
     * @throws Exception Em caso de falha de I/O.
     */
    @Test
    public void testMoverMidiaEAtualizaLocal() throws Exception {
        gerenciador.adicionarMidia(filmeTeste);

        String caminhoDestino = pastaTemporaria.getAbsolutePath();

        // Ação: Mover para pasta temporária
        gerenciador.moverMidia(filmeTeste, caminhoDestino);

        // Verificação
        assertTrue("Caminho deve estar na nova pasta", filmeTeste.getLocal().startsWith(caminhoDestino));
        File arquivoNoDestino = new File(pastaTemporaria, NOME_BASE_ARQUIVO);
        assertTrue("Arquivo físico deve existir no destino", arquivoNoDestino.exists());
        assertFalse("Arquivo antigo deve sumir", new File(NOME_BASE_ARQUIVO).exists());
    }

    /**
     * Testa a persistência correta do atributo 'Tamanho'.
     * <p>
     * Cria uma mídia, define um tamanho específico ("999"), salva no banco e então
     * lê o arquivo CSV bruto para garantir que o valor "999" foi escrito.
     * </p>
     * @throws Exception Em caso de falha de I/O.
     */
    @Test
    public void testTamanhoDiscoPersistencia() throws Exception {
        // Cria arquivo fake .tpoo
        String nomeArq = "TituloTeste.tpoo";
        new File(nomeArq).createNewFile();

        GerenciadorMidia gm = new GerenciadorMidia();
        Midia m = new Filme("TituloTeste", nomeArq, 120, "Ação", "PT-BR", "999");
        gm.adicionarMidia(m);

        // Verifica se salvou "999" no CSV
        File csv = new File(ARQUIVO_DB);
        assertTrue(csv.exists());

        boolean achou = false;
        try (Scanner sc = new Scanner(csv)) {
            while (sc.hasNextLine()) {
                if (sc.nextLine().contains("999")) {
                    achou = true; break;
                }
            }
        }
        assertTrue("CSV deve conter o tamanho 999", achou);
    }

}