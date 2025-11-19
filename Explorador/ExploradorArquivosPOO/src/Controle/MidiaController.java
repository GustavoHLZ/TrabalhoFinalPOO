package Controle;

import Modelo.GerenciadorMidia;
import Modelo.Midia;
import Modelo.Filme;
import Modelo.Livro;
import Modelo.Musica;
import excecoes.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MidiaController {
    private GerenciadorMidia gerenciador;

    public MidiaController(GerenciadorMidia gerenciador) {
        this.gerenciador = gerenciador;
    }

    public void incluirNovaMidia(String caminhoDoArquivo, String titulo, String categoria, String extra, int duracao)
            throws ErroPersistenciaException, MidiaJaCadastradaException, FormatoNaoSuportadoException,
            ExcecaoCampoException, ExcecaoArquivoNaoExisteException {

        // Validações de Campos
        if (caminhoDoArquivo == null || caminhoDoArquivo.trim().isEmpty()) {
            throw new ExcecaoCampoException("Nenhum arquivo foi selecionado.");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new ExcecaoCampoException("O campo 'Título' é obrigatório.");
        }
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new ExcecaoCampoException("O campo 'Categoria' é obrigatório.");
        }
        if (extra == null || extra.trim().isEmpty()) {
            throw new ExcecaoCampoException("O campo 'Autor/Artista/Idioma' é obrigatório.");
        }

        // Validação do Arquivo
        File arquivo = new File(caminhoDoArquivo);
        if (!arquivo.exists()) {
            throw new ExcecaoArquivoNaoExisteException("Arquivo não encontrado: " + caminhoDoArquivo);
        }

        String extensao = getFileExtension(caminhoDoArquivo);
        Midia novaMidia;

        // Criação do Objeto baseado na Extensão
        // ATENÇÃO: A ordem dos parâmetros deve bater com seus Construtores em Modelo
        switch (extensao) {
            case "mp4":
            case "mkv":
                // Filme(Titulo, Local, Duracao, Categoria, Idioma)
                novaMidia = new Filme(titulo, caminhoDoArquivo, duracao, categoria, extra);
                break;
            case "mp3":
                // Musica(Local, Titulo, Categoria, Duracao, Artista)
                novaMidia = new Musica(caminhoDoArquivo, titulo, categoria, duracao, extra);
                break;
            case "pdf":
            case "epub":
                // Livro(Local, Titulo, Categoria, Duracao, Autores)
                novaMidia = new Livro(caminhoDoArquivo, titulo, categoria, duracao, extra);
                break;
            default:
                throw new FormatoNaoSuportadoException("Formato não suportado: ." + extensao);
        }

        gerenciador.adicionarMidia(novaMidia);
    }

    public void removerMidia(Midia midiaParaRemover) throws ErroPersistenciaException {
        if (midiaParaRemover == null) {
            throw new NullPointerException("Nenhuma mídia foi selecionada para remoção.");
        }
        gerenciador.removerMidia(midiaParaRemover);
    }

    public void editarMidia(Midia midiaOriginal, Midia midiaEditada) throws ErroPersistenciaException {
        if (midiaOriginal == null || midiaEditada == null) {
            throw new NullPointerException("Erro ao editar: mídia original ou editada está nula.");
        }
        gerenciador.editarMidia(midiaOriginal, midiaEditada);
    }

    public List<Midia> getMidiasFiltradas(String formato, String categoria, String ordem) {
        return gerenciador.getMidiasFiltradas(formato, categoria, ordem);
    }

    // --- CORREÇÃO AQUI ---
    // Em vez de chamar um método que não existe, usamos o filtro "Todos" que já funciona
    public List<Midia> getTodasAsMidias() {
        return gerenciador.getMidiasFiltradas("Todos", "", null);
    }

    public void moverMidia(Midia midia, String novoDiretorio) throws ErroPersistenciaException, ExcecaoCampoException {
        if (midia == null || novoDiretorio == null || novoDiretorio.isEmpty()) {
            throw new ExcecaoCampoException("Mídia e novo diretório são obrigatórios.");
        }
        gerenciador.moverMidia(midia, novoDiretorio);
    }

    public void renomearArquivo(Midia midia, String novoNome) throws ErroPersistenciaException, ExcecaoCampoException, IOException {
        if (midia == null || novoNome == null || novoNome.trim().isEmpty()) {
            throw new ExcecaoCampoException("O novo nome do arquivo não pode ser vazio.");
        }
        gerenciador.renomearArquivoMidia(midia, novoNome);
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }
}