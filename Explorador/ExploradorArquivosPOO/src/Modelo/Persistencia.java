package Modelo;

import excecoes.ErroPersistenciaException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class Persistencia implements Serializable {

    private static final String ARQUIVO_MESTRE_CSV = "database.csv";

    public Persistencia() {
        // NÃO cria mais pasta tpoo_data
    }

    // 🟢 Retorna o arquivo .tpoo na MESMA pasta do arquivo original
    private String getCaminhoTPOO(Midia midia) {

        File arquivoOriginal = new File(midia.getLocal());

        String pasta = arquivoOriginal.getParent();

        String nomeFinal = arquivoOriginal.getName()
                .replaceAll("\\..+$", ".tpoo"); // remove extensão

        return pasta + File.separator + nomeFinal;
    }

    // 🟢 Agora salva na mesma pasta do arquivo original
    public void salvarArquivoTPOO(Midia midia) throws ErroPersistenciaException {

        String caminho = getCaminhoTPOO(midia);
        System.out.println("🔹 Salvando .tpoo em: " + caminho);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminho))) {
            oos.writeObject(midia);
        } catch (IOException e) {
            throw new ErroPersistenciaException("Falha ao salvar o arquivo .tpoo", e);
        }
    }

    // 🟢 Agora deleta na mesma pasta do original
    public void deletarArquivoTPOO(Midia midia) throws ErroPersistenciaException {

        File arquivo = new File(getCaminhoTPOO(midia));

        if (arquivo.exists() && !arquivo.delete()) {
            throw new ErroPersistenciaException("Falha ao deletar o arquivo .tpoo: " + arquivo.getAbsolutePath());
        }
    }

    // 🚫 TODA A PARTE ABAIXO PERMANECE IGUAL AO SEU CÓDIGO
    // NÃO FOI ALTERADO NADA

    public void salvarTodasAsMidiasCSV(List<Midia> midias) throws ErroPersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_MESTRE_CSV))) {
            bw.write("TIPO;LOCAL;TITULO;DURACAO;CATEGORIA;TAMANHO;EXTRA\n");

            for (Midia m : midias) {
                String tipo = "";
                String extra = "";

                if (m instanceof Musica) {
                    tipo = "Musica";
                    extra = ((Musica) m).getArtista();
                } else if (m instanceof Filme) {
                    tipo = "Filme";
                    extra = ((Filme) m).getIdioma();
                } else if (m instanceof Livro) {
                    tipo = "Livro";
                    extra = ((Livro) m).getAutores();
                }

                String linha = String.format("%s;%s;%s;%d;%s;%f;%s\n",
                        tipo, m.getLocal(), m.getTitulo(), m.getDuracao(),
                        m.getCategoria(), m.getTamanhoDisco(), extra);

                bw.write(linha);
            }
        } catch (IOException e) {
            throw new ErroPersistenciaException("Falha ao salvar o arquivo CSV mestre.", e);
        }
    }

    public List<Midia> carregarTodasAsMidiasCSV() throws ErroPersistenciaException {
        List<Midia> midiasCarregadas = new ArrayList<>();
        File arquivo = new File(ARQUIVO_MESTRE_CSV);

        if (!arquivo.exists() || arquivo.length() == 0) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_MESTRE_CSV))) {
                bw.write("TIPO;LOCAL;TITULO;DURACAO;CATEGORIA;TAMANHO;EXTRA\n");
            } catch (IOException e) {
                throw new ErroPersistenciaException("Não foi possível inicializar o CSV mestre.", e);
            }
            return midiasCarregadas;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_MESTRE_CSV))) {

            String header = br.readLine();
            if (header == null || !header.startsWith("TIPO;")) {
                throw new ErroPersistenciaException("CSV inválido ou corrompido.");
            }

            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length < 7) continue;

                String tipo = dados[0];
                String local = dados[1];
                String titulo = dados[2];
                int duracao = Integer.parseInt(dados[3]);
                String categoria = dados[4];
                double tamanho = Double.parseDouble(dados[5]);
                String extra = dados[6];

                switch (tipo) {
                    case "Musica":
                        midiasCarregadas.add(new Musica(local, titulo, categoria, duracao, extra));
                        break;
                    case "Filme":
                        midiasCarregadas.add(new Filme(titulo, local, duracao, categoria, extra));
                        break;
                    case "Livro":
                        midiasCarregadas.add(new Livro(local, titulo, categoria, duracao, extra));
                        break;
                }
            }

        } catch (IOException | NumberFormatException e) {
            throw new ErroPersistenciaException("Falha ao carregar o arquivo CSV mestre.", e);
        }

        return midiasCarregadas;
    }

    public void moverOuRenomearArquivoFisico(String caminhoAtual, String novoCaminho) throws IOException {
        File arquivoAtual = new File(caminhoAtual);
        File arquivoNovo = new File(novoCaminho);

        if (!arquivoAtual.exists()) {
            throw new IOException("Arquivo original não encontrado: " + caminhoAtual);
        }

        if (arquivoNovo.exists()) {
            throw new IOException("Já existe um arquivo com este nome no destino.");
        }

        if (!arquivoAtual.renameTo(arquivoNovo)) {
            throw new IOException("Falha ao mover/renomear o arquivo. Verifique permissões.");
        }
    }
}
