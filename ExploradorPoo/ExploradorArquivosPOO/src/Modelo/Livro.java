
package Modelo;

public class Livro extends Midia {

    /**
     * Classe que representa um Livro dentro do sistema de gerenciamento de mídia.
     * <p>
     * Esta classe estende {@link Midia} e adiciona comportamentos e atributos
     * específicos para livros, como a gestão de autores e a interpretação da duração como número de páginas.
     * </p>
     *
     * @author Gustavo E Fuzetti
     * @version 1.0
     */



    private String autores;

    /**
     * Construtor completo da classe Livro.
     * Permite definir todos os atributos, incluindo o tamanho do arquivo em disco.
     *
     * @param local          O caminho do arquivo ou localização física do livro.
     * @param titulo         O título da obra.
     * @param categoria      O gênero ou categoria do livro (ex: Romance, Técnico).
     * @param duracaoPaginas O número total de páginas do livro (mapeado para o atributo duração da classe pai).
     * @param autores        O nome do autor ou lista de autores.
     * @param tamanhoDisco   O tamanho ocupado em disco (ex: "5MB" para eBooks).
     */

    public Livro(String local, String titulo, String categoria, int duracaoPaginas, String autores, String tamanhoDisco) {
        super(local, titulo, duracaoPaginas, categoria, tamanhoDisco);
        this.autores = autores;
    }
    /**
     * Construtor de conveniência da classe Livro.
     * Inicializa o livro definindo automaticamente o tamanho em disco como "0".
     * Ideal para livros físicos onde o tamanho do arquivo não se aplica.
     *
     * @param local          O caminho ou localização do livro.
     * @param titulo         O título da obra.
     * @param categoria      O gênero ou categoria do livro.
     * @param duracaoPaginas O número total de páginas do livro.
     * @param autores        O nome do autor ou lista de autores.
     */

    public Livro(String local, String titulo, String categoria, int duracaoPaginas, String autores) {
        this(local, titulo, categoria, duracaoPaginas, autores, "0");
    }

    /**
     * Obtém o nome dos autores do livro.
     *
     * @return Uma String contendo o(s) nome(s) do(s) autor(es).
     */
    public String getAutores() {
        return autores;
    }
    /**
     * Define o nome dos autores do livro.
     *
     * @param autores Uma String com o novo nome do autor ou autores.
     */

    public void setAutores(String autores) {
        this.autores = autores;
    }

    /**
     * Retorna uma representação detalhada do Livro em formato de texto.
     * <p>
     * Este método sobrescreve o método da classe pai para incluir o campo "Autores"
     * e rotular a duração especificamente como "Páginas".
     * </p>
     *
     * @return Uma String formatada contendo Título, Autores, Categoria, Número de Páginas e Tamanho.
     */

    @Override
    public String getDetalhes() {
        return "Livro: " + getTitulo() + "\n" +
                "Autores: " + this.autores + "\n" +
                "Categoria: " + getCategoria() + "\n" +
                "Páginas: " + getDuracao() + "\n" +
                "Tamanho: " + getTamanhoDisco() + "\n";
    }
}
