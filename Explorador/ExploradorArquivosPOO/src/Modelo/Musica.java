
package Modelo;

public class Musica extends Midia {

    private String artista;

    public Musica(String local, String titulo, String categoria, int duracaoSegundos, String artista, String tamanhoDisco) {
        super(local, titulo, duracaoSegundos, categoria, tamanhoDisco);
        this.artista = artista;
    }

    public Musica(String local, String titulo, String categoria, int duracaoSegundos, String artista) {
        this(local, titulo, categoria, duracaoSegundos, artista, "0");
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    @Override
    public String getDetalhes() {
        return "Música: " + getTitulo() + "\n" +
                "Artista: " + artista + "\n" +
                "Categoria: " + getCategoria() + "\n" +
                "Duração (seg): " + getDuracao() + "\n" +
                "Tamanho: " + getTamanhoDisco() + "\n";
    }
}
