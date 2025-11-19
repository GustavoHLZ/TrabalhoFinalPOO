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
 * Plano de Testes para validar as classes da camada de negócios (GerenciadorMidia).
 * Versão simplificada: Usa apenas extensão .tpoo para todos os testes.
 */
public class GerenciadorMidiaTest {

    private GerenciadorMidia gerenciador;
    private Filme filmeTeste;

    // Nome base para o arquivo principal do teste
    private final String NOME_BASE_ARQUIVO = "teste_filme_original.tpoo";
    private final String ARQUIVO_DB = "database.csv";
    private File pastaTemporaria;

    // --- CONFIGURAÇÃO E LIMPEZA (SETUP/TEARDOWN) ---

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

    @Test
    public void testAdicionarMidia() throws Exception {
        gerenciador.adicionarMidia(filmeTeste);
        assertNotNull("Mídia deve ser encontrada após adição.", gerenciador.buscarMidiaPorLocal(NOME_BASE_ARQUIVO));
    }

    @Test(expected = MidiaJaCadastradaException.class)
    public void testAdicionarMidiaDuplicadaDeveLancarExcecao() throws Exception {
        gerenciador.adicionarMidia(filmeTeste);
        gerenciador.adicionarMidia(filmeTeste); // Deve falhar aqui
    }

    @Test
    public void testRemoverMidia() throws Exception {
        gerenciador.adicionarMidia(filmeTeste);
        gerenciador.removerMidia(filmeTeste);
        assertNull("Mídia deve ser removida da lista.", gerenciador.buscarMidiaPorLocal(NOME_BASE_ARQUIVO));
    }


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