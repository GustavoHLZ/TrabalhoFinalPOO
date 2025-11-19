package Modelo;

import java.io.File;
import java.io.Serializable;

/**
 * Classe abstrata que representa uma mídia genérica.
 * Agora contém o campo 'tamanhoDisco' como String (campo informado no .tpoo e UI).
 */
public abstract class Midia implements Serializable {

    protected String local;
    protected String titulo;
    protected int duracao;
    protected String categoria;
    protected String tamanhoDisco;

    public Midia(String local, String titulo, int duracao, String categoria, String tamanhoDisco) {
        this.local = local;
        this.titulo = titulo;
        this.duracao = duracao;
        this.categoria = categoria;
        this.tamanhoDisco = tamanhoDisco;
    }

    public String getLocal() {
        return local;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getDuracao() {
        return duracao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTamanhoDisco() {
        return tamanhoDisco;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public void setTamanhoDisco(String tamanhoDisco) {
        this.tamanhoDisco = tamanhoDisco;
    }

    /**
     * Atualiza o tamanhoDisco lendo o tamanho do arquivo no disco e formatando como número de bytes.
     * Caso o arquivo não exista, mantém o valor atual.
     */
    public void atualizarTamanhoDoArquivo() {
        File file  = new File(local);
        if(file.exists()) {
            this.tamanhoDisco = String.valueOf(file.length());
        }
    }

    public abstract String getDetalhes();

}
