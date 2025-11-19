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

public class GerenciadorMidia {

    private List<Midia> midias;
    private final String ARQUIVO_DB = "database.csv";

    public GerenciadorMidia() {
        this.midias = new ArrayList<>();
        carregarDoArquivo();
    }

    // ---------------- MÉTODOS DE PERSISTÊNCIA (CSV) ----------------

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

    private void gerarArquivoIndividual(Midia m) {
        // Salva o .tpoo na mesma pasta do arquivo original
        File arquivoOriginal = new File(m.getLocal());
        File pastaDoArquivo = arquivoOriginal.getParentFile();

        // O nome do .tpoo é baseado no TÍTULO (conforme regra do trabalho)
        String nomeTpoo = m.getTitulo().replaceAll("[^a-zA-Z0-9]", ".tpoo");
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

    // ---------------- CRUD ----------------

    public void adicionarMidia(Midia m) throws MidiaJaCadastradaException, ErroPersistenciaException {
        if (buscarMidiaPorLocal(m.getLocal()) != null) {
            throw new MidiaJaCadastradaException("Mídia já cadastrada!");
        }
        midias.add(m);
        salvarNoArquivo();
        gerarArquivoIndividual(m);
    }

    public void removerMidia(Midia m) throws ErroPersistenciaException {
        midias.remove(m);
        salvarNoArquivo();

        // Tenta apagar o .tpoo associado
        File tpoo = getArquivoTpooAssociado(m);
        if (tpoo != null && tpoo.exists()) {
            tpoo.delete();
        }
    }

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

    // ---------------- MOVER E RENOMEAR (CORRIGIDOS) ----------------

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
            moverTpooJunto(m, novoDiretorio); // Este método precisa ser verificado, pois depende do Título que não mudou.

            // 3. ATUALIZAÇÃO DO OBJETO NA MEMÓRIA
            m.setLocal(destino.toFile().getAbsolutePath()); // Atualiza o caminho

            // 4. ATUALIZAÇÃO DA PERSISTÊNCIA (CSV)
            salvarNoArquivo();

        } catch (IOException e) {
            throw new ErroPersistenciaException("Falha técnica ao mover: " + e.getMessage());
        }
    }

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

    private File getArquivoTpooAssociado(Midia m) {
        // Reconstrói o caminho onde o .tpoo deveria estar
        File arquivoMedia = new File(m.getLocal());
        String nomeTpoo = m.getTitulo().replaceAll("[^a-zA-Z0-9]", ".tpoo");
        return new File(arquivoMedia.getParent(), nomeTpoo);
    }

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

    public Midia buscarMidiaPorLocal(String local) {
        for (Midia m : midias) {
            if (m.getLocal().equals(local)) return m;
        }
        return null;
    }

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