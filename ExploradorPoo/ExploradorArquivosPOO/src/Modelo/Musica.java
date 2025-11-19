package Modelo;

/**
 * Representa uma mídia do tipo Música (faixa de áudio).
 * <p>
 * Esta classe estende {@link Midia} para incluir informações específicas
 * sobre o artista ou banda responsável pela obra.
 * </p>
 *
 * @author Seu Nome
 * @version 1.0
 * @see Midia
 */
public class Musica extends Midia {

    /**
     * Nome do artista, banda ou compositor da música.
     */
    private String artista;

    /**
     * Construtor completo para instanciar uma Música com todas as informações,
     * incluindo o tamanho do arquivo.
     *
     * @param local O caminho absoluto do arquivo de áudio.
     * @param titulo O nome da música.
     * @param categoria O gênero musical (ex: "Rock", "Pop", "Clássica").
     * @param duracaoSegundos A duração da faixa em segundos.
     * @param artista O nome do artista ou banda.
     * @param tamanhoDisco O tamanho do arquivo em bytes (formato texto).
     */
    public Musica(String local, String titulo, String categoria, int duracaoSegundos, String artista, String tamanhoDisco) {
        super(local, titulo, duracaoSegundos, categoria, tamanhoDisco);
        this.artista = artista;
    }

    /**
     * Construtor simplificado para instanciar uma Música.
     * <p>
     * Inicializa o tamanho do disco como "0" por padrão.
     * </p>
     *
     * @param local O caminho absoluto do arquivo de áudio.
     * @param titulo O nome da música.
     * @param categoria O gênero musical.
     * @param duracaoSegundos A duração da faixa em segundos.
     * @param artista O nome do artista ou banda.
     */
    public Musica(String local, String titulo, String categoria, int duracaoSegundos, String artista) {
        this(local, titulo, categoria, duracaoSegundos, artista, "0");
    }

    /**
     * Obtém o nome do artista.
     *
     * @return Uma String com o nome do artista/banda.
     */
    public String getArtista() {
        return artista;
    }

    /**
     * Define ou altera o nome do artista.
     *
     * @param artista O novo nome do artista.
     */
    public void setArtista(String artista) {
        this.artista = artista;
    }

    /**
     * Retorna uma representação textual formatada com os detalhes da música.
     * <p>
     * Sobrescreve o método da classe pai para incluir o campo específico do artista.
     * </p>
     *
     * @return Uma String contendo título, artista, categoria, duração e tamanho.
     */
    @Override
    public String getDetalhes() {
        return "Música: " + getTitulo() + "\n" +
                "Artista: " + artista + "\n" +
                "Categoria: " + getCategoria() + "\n" +
                "Duração (seg): " + getDuracao() + "\n" +
                "Tamanho: " + getTamanhoDisco() + "\n";
    }
}