package Modelo;

import java.io.File;
import java.io.Serializable;

/**
 * Classe abstrata que representa a entidade genérica de uma Mídia no sistema.
 * <p>
 * Serve como base para tipos específicos (como {@link Filme}, {@link Musica}, {@link Livro})
 * e contém os atributos e comportamentos comuns a todos os arquivos gerenciados.
 * </p>
 * <p>
 * Implementa a interface {@link Serializable} para permitir que os objetos e suas
 * subclasses sejam convertidos em bytes e salvos em arquivos (persistência binária).
 * </p>
 *
 * @author Seu Nome
 * @version 1.0
 */
public abstract class Midia implements Serializable {

    /**
     * Caminho absoluto do arquivo no sistema de arquivos (ex: "C:/Videos/filme.mp4").
     */
    protected String local;

    /**
     * Título ou nome de exibição da mídia.
     */
    protected String titulo;

    /**
     * Duração da mídia em minutos ou páginas (dependendo do tipo concreto).
     */
    protected int duracao;

    /**
     * Categoria ou gênero da mídia (ex: "Ação", "Rock", "Educação").
     */
    protected String categoria;

    /**
     * Tamanho do arquivo físico em bytes, armazenado como texto.
     */
    protected String tamanhoDisco;

    /**
     * Construtor base para inicializar os atributos comuns de qualquer mídia.
     *
     * @param local O caminho absoluto do arquivo.
     * @param titulo O título da mídia.
     * @param duracao A duração (em minutos) ou extensão da mídia.
     * @param categoria A categoria ou gênero.
     * @param tamanhoDisco O tamanho do arquivo em bytes (como String).
     */
    public Midia(String local, String titulo, int duracao, String categoria, String tamanhoDisco) {
        this.local = local;
        this.titulo = titulo;
        this.duracao = duracao;
        this.categoria = categoria;
        this.tamanhoDisco = tamanhoDisco;
    }

    /**
     * Obtém o caminho absoluto do arquivo.
     * @return O caminho (path) como String.
     */
    public String getLocal() {
        return local;
    }

    /**
     * Obtém o título da mídia.
     * @return O título.
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Obtém a duração ou extensão da mídia.
     * @return O valor inteiro representando tempo ou quantidade.
     */
    public int getDuracao() {
        return duracao;
    }

    /**
     * Obtém a categoria da mídia.
     * @return A categoria.
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Define uma nova categoria para a mídia.
     * @param categoria A nova categoria a ser atribuída.
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtém o tamanho do arquivo armazenado.
     * @return O tamanho em bytes como String.
     */
    public String getTamanhoDisco() {
        return tamanhoDisco;
    }

    /**
     * Atualiza o caminho do arquivo.
     * @param local O novo caminho absoluto.
     */
    public void setLocal(String local) {
        this.local = local;
    }

    /**
     * Define um novo título para a mídia.
     * @param titulo O novo título.
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Define a duração da mídia.
     * @param duracao A nova duração.
     */
    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    /**
     * Define manualmente o tamanho do disco.
     * @param tamanhoDisco O tamanho em bytes como String.
     */
    public void setTamanhoDisco(String tamanhoDisco) {
        this.tamanhoDisco = tamanhoDisco;
    }

    /**
     * Atualiza o atributo {@code tamanhoDisco} lendo o arquivo físico real no sistema.
     * <p>
     * O método verifica se o arquivo existe no caminho especificado em {@code local}.
     * Se existir, captura o tamanho em bytes ({@code length()}) e converte para String.
     * Se não existir, mantém o valor anterior.
     * </p>
     */
    public void atualizarTamanhoDoArquivo() {
        File file  = new File(local);
        if(file.exists()) {
            this.tamanhoDisco = String.valueOf(file.length());
        }
    }

    /**
     * Retorna uma representação detalhada da mídia em formato de texto.
     * <p>
     * Este método é abstrato e obriga todas as classes filhas (Filme, Musica, Livro)
     * a implementarem sua própria formatação de detalhes.
     * </p>
     *
     * @return Uma String contendo os detalhes específicos da mídia.
     */
    public abstract String getDetalhes();

}