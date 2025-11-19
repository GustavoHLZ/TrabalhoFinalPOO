package Modelo;

/**
 * Representa uma mídia do tipo Filme.
 * <p>
 * Esta classe estende a classe abstrata {@link Midia} e adiciona o atributo
 * específico de idioma. Ela é utilizada para manipular arquivos de vídeo
 * no sistema de gerenciamento.
 * </p>
 *
 * @author Seu Nome
 * @version 1.0
 * @see Midia
 */
public class Filme extends Midia {

    /**
     * O idioma do áudio ou legenda principal do filme (ex: "Português", "Inglês").
     */
    private String idioma;

    /**
     * Construtor completo para instanciar um novo Filme com todas as informações,
     * incluindo o tamanho do arquivo/disco.
     *
     * @param titulo O título comercial do filme.
     * @param local O caminho absoluto (path) do arquivo no sistema de arquivos.
     * @param duracaoMinutos A duração do filme em minutos.
     * @param categoria O gênero do filme (ex: "Ação", "Comédia").
     * @param idioma O idioma principal do filme.
     * @param tamanhoDisco O tamanho do arquivo em disco (ex: "2.5 GB").
     */
    public Filme(String titulo, String local, int duracaoMinutos, String categoria, String idioma, String tamanhoDisco) {
        super(local, titulo, duracaoMinutos, categoria, tamanhoDisco);
        this.idioma = idioma;
    }

    /**
     * Construtor simplificado para instanciar um Filme.
     * <p>
     * Este construtor define o tamanho do disco automaticamente como "0".
     * </p>
     *
     * @param titulo O título comercial do filme.
     * @param local O caminho absoluto (path) do arquivo.
     * @param duracaoMinutos A duração do filme em minutos.
     * @param categoria O gênero do filme.
     * @param idioma O idioma principal do filme.
     */
    public Filme(String titulo, String local, int duracaoMinutos, String categoria, String idioma) {
        this(titulo, local, duracaoMinutos, categoria, idioma, "0");
    }

    /**
     * Retorna uma representação textual formatada com os detalhes do filme.
     * <p>
     * Sobrescreve o método da classe pai para incluir o campo específico de idioma.
     * </p>
     *
     * @return Uma String contendo título, idioma, categoria, duração e tamanho,
     * separados por quebras de linha.
     */
    @Override
    public String getDetalhes() {
        return "Filme: " + getTitulo() + "\n" +
                "Idioma: " + idioma + "\n" +
                "Categoria: " + getCategoria() + "\n" +
                "Duração (min): " + getDuracao() + "\n" +
                "Tamanho: " + getTamanhoDisco() + "\n";
    }

    /**
     * Obtém o idioma do filme.
     *
     * @return O idioma atual (ex: "Dublado", "Legendado").
     */
    public String getIdioma() {
        return idioma;
    }

    /**
     * Define ou altera o idioma do filme.
     *
     * @param idioma O novo idioma a ser atribuído.
     */
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
}