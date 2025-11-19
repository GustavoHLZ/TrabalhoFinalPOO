
package Modelo;

public class Livro extends Midia {

    private String autores;

    public Livro(String local, String titulo, String categoria, int duracaoPaginas, String autores, String tamanhoDisco) {
        super(local, titulo, duracaoPaginas, categoria, tamanhoDisco);
        this.autores = autores;
    }

    public Livro(String local, String titulo, String categoria, int duracaoPaginas, String autores) {
        this(local, titulo, categoria, duracaoPaginas, autores, "0");
    }

    public String getAutores() {
        return autores;
    }

    public void setAutores(String autores) {
        this.autores = autores;
    }

    @Override
    public String getDetalhes() {
        return "Livro: " + getTitulo() + "\n" +
                "Autores: " + this.autores + "\n" +
                "Categoria: " + getCategoria() + "\n" +
                "Páginas: " + getDuracao() + "\n" +
                "Tamanho: " + getTamanhoDisco() + "\n";
    }
}
