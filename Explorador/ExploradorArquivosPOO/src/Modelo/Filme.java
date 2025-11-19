
package Modelo;

public class Filme extends Midia {

    private String idioma;

    // CONSTRUTOR compatível novo: recebe tamanhoDisco
    public Filme(String titulo, String local, int duracaoMinutos, String categoria, String idioma, String tamanhoDisco) {
        super(local, titulo, duracaoMinutos, categoria, tamanhoDisco);
        this.idioma = idioma;
    }

    // Constructor compatibility with old code (calculates tamanho como 0)
    public Filme(String titulo, String local, int duracaoMinutos, String categoria, String idioma) {
        this(titulo, local, duracaoMinutos, categoria, idioma, "0");
    }

    @Override
    public String getDetalhes() {
        return "Filme: " + getTitulo() + "\n" +
                "Idioma: " + idioma + "\n" +
                "Categoria: " + getCategoria() + "\n" +
                "Duração (min): " + getDuracao() + "\n" +
                "Tamanho: " + getTamanhoDisco() + "\n";
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
}
