package Modelo;

import excecoes.ErroPersistenciaException;
import excecoes.MidiaJaCadastradaException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Classe controladora responsável por gerenciar todo o ciclo de vida das Mídias.
 * <p>
 * Esta classe centraliza as operações de criar, ler, atualizar e deletar (CRUD),
 * mantendo a sincronia entre a lista em memória, o arquivo de registro central (CSV)
 * e os arquivos físicos no sistema operacional.
 * </p>
 *
 * @author Seu Nome
 * @version 1.0
 */
public class GerenciadorMidia {

    /**
     * Lista em memória contendo todas as mídias carregadas.
     */
    private List<Midia> midias;

    /**
     * Caminho para o arquivo CSV que funciona como banco de dados persistente.
     */
    private final String ARQUIVO_DB = "database.csv";

    /**
     * Construtor que inicializa o gerenciador.
     * <p>
     * Ao ser instanciado, ele tenta automaticamente carregar os dados
     * existentes do arquivo CSV para a memória.
     * </p>
     */
    public GerenciadorMidia() {
        this.midias = new ArrayList<>();
        carregarDoArquivo();
    }

    /**
     * Lê o arquivo CSV (database.csv) e popula a lista de mídias.
     * <p>
     * Este método é chamado internamente na inicialização. Ele faz o parsing
     * de cada linha para instanciar o objeto correto (Filme, Musica ou Livro).
     * Se o arquivo não existir, ele cria um novo vazio.
     * </p>
     */
    private void carregarDoArquivo() {
        File arquivo = new File(ARQUIVO_DB);
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException e) {
                System.err.println("Erro ao criar banco de dados: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) { primeiraLinha = false; continue; }

                String[] partes = linha.split(";");
                if (partes.length < 7) continue;

                String tipo = partes[0];
                String titulo = partes[1];
                String categoria = partes[2];
                int duracao = Integer.parseInt(partes[3]);
                String extra = partes[4];
                String local = partes[5];
                String tamanho = partes[6];

                Midia m = null;
                switch (tipo) {
                    case "Filme" -> m = new Filme(titulo, local, duracao, categoria, extra, tamanho);
                    case "Musica" -> m = new Musica(local, titulo, categoria, duracao, extra, tamanho);
                    case "Livro" -> m = new Livro(local, titulo, categoria, duracao, extra, tamanho);
                }
                if (m != null) midias.add(m);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler CSV: " + e.getMessage());
        }
    }

    /**
     * Persiste o estado atual da lista de mídias no arquivo CSV.
     * <p>
     * Este método sobrescreve o arquivo CSV inteiro com os dados atuais da memória.
     * Deve ser chamado após qualquer operação de adição, remoção ou edição.
     * </p>
     *
     * @throws ErroPersistenciaException Se houver falha na escrita do arquivo.
     */
    private void salvarNoArquivo() throws ErroPersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_DB))) {
            bw.write("TIPO;TITULO;CATEGORIA;DURACAO;EXTRA;LOCAL;TAMANHO");
            bw.newLine();

            for (Midia m : midias) {
                String extra = "";
                String tipo = "";

                if (m instanceof Filme f) { tipo = "Filme"; extra = f.getIdioma(); }
                else if (m instanceof Musica mu) { tipo = "Musica"; extra = mu.getArtista(); }
                else if (m instanceof Livro l) { tipo = "Livro"; extra = l.getAutores(); }

                String linha = String.format("%s;%s;%s;%d;%s;%s;%s",
                        tipo, m.getTitulo(), m.getCategoria(), m.getDuracao(), extra, m.getLocal(), m.getTamanhoDisco());
                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ErroPersistenciaException("Erro ao salvar dados: " + e.getMessage());
        }
    }

    /**
     * Gera um arquivo de metadados (.tpoo) na mesma pasta do arquivo de mídia original.
     * <p>
     * O nome do arquivo é gerado com base no título da mídia. Este arquivo contém
     * um resumo textual das propriedades do objeto.
     * </p>
     *
     * @param m A mídia para a qual o arquivo .tpoo será gerado.
     */
    private void gerarArquivoIndividual(Midia m) {
        //1- Salva o .tpoo na mesma pasta do arquivo original
        File arquivoOriginal = new File(m.getLocal());
        File pastaDoArquivo = arquivoOriginal.getParentFile();

        // O nome do .tpoo é baseado no TÍTULO
        String nomeTpoo = m.getTitulo().replaceAll("[^a-zA-Z0-9]", ".tpoo");  //replace faz que acrescente o tpoo
        File arquivoTPOO = new File(pastaDoArquivo, nomeTpoo);

        try (PrintWriter pw = new PrintWriter(arquivoTPOO)) {
            pw.println("Arquivo TPOO - " + m.getClass().getSimpleName());
            pw.println("Titulo: " + m.getTitulo());
            pw.println("Categoria: " + m.getCategoria());
            pw.println("Duração: " + m.getDuracao());
            pw.println("Local: " + m.getLocal());
            pw.println("Tamanho: " + m.getTamanhoDisco());
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao gerar .tpoo: " + e.getMessage());
        }
    }


    /**
     * Adiciona uma nova mídia ao sistema.
     * <p>
     * Verifica se o arquivo já está cadastrado (pelo caminho). Se não estiver,
     * adiciona à lista, salva no CSV e gera o arquivo .tpoo.
     * </p>
     *
     * @param m O objeto Midia a ser adicionado.
     * @throws MidiaJaCadastradaException Se já existir uma mídia com o mesmo caminho.
     * @throws ErroPersistenciaException Se houver erro ao salvar no disco.
     */
    public void adicionarMidia(Midia m) throws MidiaJaCadastradaException, ErroPersistenciaException {
        if (buscarMidiaPorLocal(m.getLocal()) != null) {
            throw new MidiaJaCadastradaException("Mídia já cadastrada!");
        }
        midias.add(m);
        salvarNoArquivo();
        gerarArquivoIndividual(m);
    }

    /**
     * Remove uma mídia do sistema.
     * <p>
     * Remove da lista, atualiza o CSV e tenta excluir o arquivo .tpoo associado.
     * Nota: O arquivo de mídia original (mp3, mp4, pdf) NÃO é excluído.
     * </p>
     *
     * @param m A mídia a ser removida.
     * @throws ErroPersistenciaException Se houver erro ao salvar o estado atualizado.
     */
    public void removerMidia(Midia m) throws ErroPersistenciaException {
        midias.remove(m);
        salvarNoArquivo();

        // Tenta apagar o .tpoo associado
        File tpoo = getArquivoTpooAssociado(m);
        if (tpoo != null && tpoo.exists()) {
            tpoo.delete();
        }
    }

    /**
     * Edita as informações de uma mídia existente.
     * <p>
     * Substitui o objeto antigo pelo novo na lista e atualiza a persistência.
     * Se o título mudar, o arquivo .tpoo antigo é removido e um novo é gerado.
     * </p>
     *
     * @param midiaAntiga O objeto original que será substituído.
     * @param midiaNova O novo objeto com as informações atualizadas.
     * @throws ErroPersistenciaException Se houver erro ao salvar as alterações.
     */
    public void editarMidia(Midia midiaAntiga, Midia midiaNova) throws ErroPersistenciaException {
        int index = midias.indexOf(midiaAntiga);
        if (index != -1) {
            // Se mudou o título, precisamos renomear o .tpoo antigo ou criar um novo
            // Simplificação: remove o .tpoo antigo e cria um novo
            File tpooAntigo = getArquivoTpooAssociado(midiaAntiga);
            if(tpooAntigo != null && tpooAntigo.exists()) tpooAntigo.delete();

            midias.set(index, midiaNova);
            salvarNoArquivo();
            gerarArquivoIndividual(midiaNova);
        }
    }

    /**
     * Move o arquivo físico da mídia para um novo diretório.
     * <p>
     * Esta operação é complexa pois envolve:
     * 1. Mover o arquivo de mídia real.
     * 2. Mover o arquivo .tpoo associado.
     * 3. Atualizar o caminho interno no objeto.
     * 4. Atualizar o registro no CSV.
     * </p>
     *
     * @param m A mídia a ser movida.
     * @param novoDiretorio O caminho da pasta de destino.
     * @throws ErroPersistenciaException Se o arquivo original não existir, o destino for inválido ou houver erro de permissão.
     */
    public void moverMidia(Midia m, String novoDiretorio) throws ErroPersistenciaException {
        File arquivoOriginal = new File(m.getLocal());
        File pastaDestino = new File(novoDiretorio);

        if (!arquivoOriginal.exists()) {
            throw new ErroPersistenciaException("ERRO FATAL: Arquivo não encontrado.");
        }
        if (!pastaDestino.exists() || !pastaDestino.isDirectory()) {
            throw new ErroPersistenciaException("A pasta de destino não existe: " + novoDiretorio);
        }

        Path origem = Paths.get(m.getLocal());
        Path destino = Paths.get(novoDiretorio, arquivoOriginal.getName()); // Mantém o nome do arquivo

        try {
            // 1. Move o Arquivo de Mídia (Filme/Musica/Livro)
            Files.move(origem, destino, StandardCopyOption.REPLACE_EXISTING);

            // 2. Move o Arquivo .tpoo junto
            moverTpooJunto(m, novoDiretorio);

            // 3. ATUALIZAÇÃO DO OBJETO NA MEMÓRIA
            m.setLocal(destino.toFile().getAbsolutePath()); // Atualiza o caminho

            // 4. ATUALIZAÇÃO DA PERSISTÊNCIA (CSV)
            salvarNoArquivo();

        } catch (IOException e) {
            throw new ErroPersistenciaException("Falha técnica ao mover: " + e.getMessage());
        }
    }

    /**
     * Renomeia o arquivo físico da mídia e atualiza o título no sistema.
     *
     * @param m A mídia a ser renomeada.
     * @param novoNomeSemExtensao O novo nome desejado (sem a extensão .mp3, .pdf, etc).
     * @throws ErroPersistenciaException Se houver falha na operação de renomear do sistema operacional.
     */
    public void renomearArquivoMidia(Midia m, String novoNomeSemExtensao) throws ErroPersistenciaException {
        File arquivoOriginal = new File(m.getLocal());

        // Devemos obter a extensão correta para montar o novo caminho
        String nomeAtual = arquivoOriginal.getName();
        int i = nomeAtual.lastIndexOf('.');
        String extensao = (i > 0) ? nomeAtual.substring(i) : "";

        File novoArquivo = new File(arquivoOriginal.getParent(), novoNomeSemExtensao + extensao);

        try {
            // 1. Move o arquivo físico (renomeando)
            Files.move(arquivoOriginal.toPath(), novoArquivo.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 2. ATUALIZAÇÃO DO OBJETO NA MEMÓRIA (Corpo do objeto 'm')
            m.setLocal(novoArquivo.getAbsolutePath());
            m.setTitulo(novoNomeSemExtensao); // Atualiza o título para refletir o nome do arquivo

            // 3. ATUALIZAÇÃO DA PERSISTÊNCIA (CSV)
            salvarNoArquivo();

            // 4. Se o título mudou, o .tpoo também deve mudar. Usamos o editarMidia aqui:
            // Simplificamos: removemos o antigo e criamos o novo
            File tpooAntigo = getArquivoTpooAssociado(m);
            if(tpooAntigo != null && tpooAntigo.exists()) tpooAntigo.delete();
            gerarArquivoIndividual(m);

        } catch(IOException e){
            throw new ErroPersistenciaException("Erro ao renomear arquivo: " + e.getMessage());
        }
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * Helper para localizar o arquivo .tpoo com base nas regras de nomenclatura do sistema.
     * @param m A mídia base.
     * @return Um objeto File apontando para onde o .tpoo deveria estar.
     */
    private File getArquivoTpooAssociado(Midia m) {
        // Reconstrói o caminho onde o .tpoo deveria estar
        File arquivoMedia = new File(m.getLocal());
        String nomeTpoo = m.getTitulo().replaceAll("[^a-zA-Z0-9]", ".tpoo");
        return new File(arquivoMedia.getParent(), nomeTpoo);
    }

    /**
     * Helper para mover o arquivo .tpoo quando a mídia principal é movida.
     * @param m A mídia que está sendo movida.
     * @param novoDiretorio O caminho da pasta de destino.
     */
    private void moverTpooJunto(Midia m, String novoDiretorio) {
        try {
            File tpooOrigem = getArquivoTpooAssociado(m);

            if (tpooOrigem.exists()) {
                Path origem = tpooOrigem.toPath();
                Path destino = Paths.get(novoDiretorio, tpooOrigem.getName());
                Files.move(origem, destino, StandardCopyOption.REPLACE_EXISTING);
                System.out.println(".tpoo movido com sucesso para: " + destino);
            } else {
                System.out.println("Aviso: Arquivo .tpoo não encontrado para mover (não é crítico).");
            }
        } catch (Exception e) {
            System.out.println("Erro ao mover .tpoo: " + e.getMessage());
        }
    }

    /**
     * Helper (legado) para recriar a referência do objeto após mudança de arquivo.
     * <p>
     * <b>Nota:</b> Este método remove a referência antiga e cria uma nova instância.
     * Útil se a imutabilidade fosse um requisito estrito, mas atualmente o sistema atualiza o objeto existente.
     * </p>
     * @param m A mídia antiga.
     * @param novoArquivo O arquivo no novo local.
     * @throws ErroPersistenciaException Se falhar ao salvar.
     */
    private void atualizarObjetoAposMudancaArquivo(Midia m, File novoArquivo) throws ErroPersistenciaException {
        // Remove a referência antiga
        midias.remove(m);

        // Cria nova referência
        Midia nova = null;
        String caminho = novoArquivo.getAbsolutePath();

        if (m instanceof Filme f) nova = new Filme(f.getTitulo(), caminho, f.getDuracao(), f.getCategoria(), f.getIdioma());
        else if (m instanceof Musica mu) nova = new Musica(caminho, mu.getTitulo(), mu.getCategoria(), mu.getDuracao(), mu.getArtista());
        else if (m instanceof Livro l) nova = new Livro(caminho, l.getTitulo(), l.getCategoria(), l.getDuracao(), l.getAutores());

        if (nova != null) {
            midias.add(nova);
            salvarNoArquivo(); // Atualiza CSV imediatamente
        }
    }

    // ---------------- CONSULTAS ----------------

    /**
     * Busca uma mídia na lista com base no caminho absoluto do arquivo.
     *
     * @param local O caminho absoluto do arquivo.
     * @return O objeto Midia se encontrado, ou null caso contrário.
     */
    public Midia buscarMidiaPorLocal(String local) {
        for (Midia m : midias) {
            if (m.getLocal().equals(local)) return m;
        }
        return null;
    }

    /**
     * Retorna uma lista filtrada e ordenada de mídias.
     *
     * @param tipo O tipo de mídia ("Filme", "Musica", "Livro" ou "Todos").
     * @param categoria A categoria para filtrar (pode ser vazio para ignorar).
     * @param ordem O critério de ordenação ("Alfabética" ou "Duração").
     * @return Uma nova lista contendo apenas as mídias que atendem aos critérios.
     */
    public List<Midia> getMidiasFiltradas(String tipo, String categoria, String ordem) {
        List<Midia> filtradas = new ArrayList<>();
        for (Midia m : midias) {
            boolean okTipo = tipo.equals("Todos") ||
                    (tipo.equals("Filme") && m instanceof Filme) ||
                    (tipo.equals("Musica") && m instanceof Musica) ||
                    (tipo.equals("Livro") && m instanceof Livro);
            boolean okCat = categoria.isEmpty() || m.getCategoria().equalsIgnoreCase(categoria);

            if (okTipo && okCat) filtradas.add(m);
        }

        if (ordem != null && !ordem.isEmpty()) {
            if (ordem.contains("Alfabética")) filtradas.sort(Comparator.comparing(Midia::getTitulo));
            else if (ordem.contains("Duração")) filtradas.sort(Comparator.comparingInt(Midia::getDuracao));
        }
        return filtradas;
    }
}