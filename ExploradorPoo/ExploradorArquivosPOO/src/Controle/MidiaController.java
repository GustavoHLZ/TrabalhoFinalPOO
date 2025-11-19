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

/**
 * Controlador responsável pela intermediação entre a Interface Gráfica (View) e a Lógica de Negócios (Model).
 * <p>
 * Esta classe realiza a validação prévia de dados (campos vazios, caminhos inválidos)
 * e decide qual tipo de objeto {@link Midia} instanciar (Filme, Música ou Livro)
 * com base na extensão do arquivo fornecido.
 * </p>
 *
 * @author Seu Nome
 * @version 1.0
 */
public class MidiaController {

    /**
     * Referência para o gerenciador de persistência e lógica de mídia.
     */
    private GerenciadorMidia gerenciador;

    /**
     * Construtor do Controlador.
     *
     * @param gerenciador A instância do GerenciadorMidia que será manipulada.
     */
    public MidiaController(GerenciadorMidia gerenciador) {
        this.gerenciador = gerenciador;
    }

    /**
     * Valida os dados e adiciona uma nova mídia ao sistema.
     * <p>
     * Este método verifica se os campos obrigatórios estão preenchidos e se o arquivo físico existe.
     * A partir da extensão do arquivo, ele instancia a subclasse correta:
     * <ul>
     * <li><b>.mp4, .mkv</b>: Cria um {@link Filme}.</li>
     * <li><b>.mp3</b>: Cria uma {@link Musica}.</li>
     * <li><b>.pdf, .epub</b>: Cria um {@link Livro}.</li>
     * </ul>
     * </p>
     *
     * @param caminhoDoArquivo O caminho absoluto do arquivo.
     * @param titulo O título da mídia.
     * @param categoria A categoria/gênero.
     * @param extra Campo polimórfico (Idioma, Artista ou Autores).
     * @param duracao A duração em minutos, segundos ou número de páginas.
     * @throws ErroPersistenciaException Se houver erro ao salvar no banco de dados.
     * @throws MidiaJaCadastradaException Se o arquivo já estiver registrado no sistema.
     * @throws FormatoNaoSuportadoException Se a extensão do arquivo não for reconhecida.
     * @throws ExcecaoCampoException Se algum campo obrigatório estiver vazio.
     * @throws ExcecaoArquivoNaoExisteException Se o caminho do arquivo não apontar para um arquivo existente.
     */
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

    /**
     * Solicita a remoção de uma mídia.
     *
     * @param midiaParaRemover O objeto mídia a ser removido.
     * @throws ErroPersistenciaException Se houver erro ao atualizar o banco de dados.
     * @throws NullPointerException Se a mídia fornecida for nula.
     */
    public void removerMidia(Midia midiaParaRemover) throws ErroPersistenciaException {
        if (midiaParaRemover == null) {
            throw new NullPointerException("Nenhuma mídia foi selecionada para remoção.");
        }
        gerenciador.removerMidia(midiaParaRemover);
    }

    /**
     * Solicita a edição de uma mídia existente.
     *
     * @param midiaOriginal O objeto original antes da edição.
     * @param midiaEditada O novo objeto contendo as informações atualizadas.
     * @throws ErroPersistenciaException Se houver erro na persistência dos dados.
     */
    public void editarMidia(Midia midiaOriginal, Midia midiaEditada) throws ErroPersistenciaException {
        if (midiaOriginal == null || midiaEditada == null) {
            throw new NullPointerException("Erro ao editar: mídia original ou editada está nula.");
        }
        gerenciador.editarMidia(midiaOriginal, midiaEditada);
    }

    /**
     * Obtém uma lista de mídias aplicando filtros de pesquisa.
     *
     * @param formato O tipo de mídia ("Filme", "Musica", "Livro" ou "Todos").
     * @param categoria A categoria para filtragem (string vazia ignora este filtro).
     * @param ordem O critério de ordenação.
     * @return Uma lista de objetos {@link Midia} que atendem aos critérios.
     */
    public List<Midia> getMidiasFiltradas(String formato, String categoria, String ordem) {
        return gerenciador.getMidiasFiltradas(formato, categoria, ordem);
    }

    /**
     * Obtém todas as mídias cadastradas sem aplicar filtros de categoria.
     *
     * @return Uma lista completa das mídias.
     */
    public List<Midia> getTodasAsMidias() {
        return gerenciador.getMidiasFiltradas("Todos", "", null);
    }

    /**
     * Solicita a movimentação do arquivo físico de uma mídia para um novo diretório.
     *
     * @param midia A mídia a ser movida.
     * @param novoDiretorio O caminho da pasta de destino.
     * @throws ErroPersistenciaException Se houver erro de I/O ou permissão.
     * @throws ExcecaoCampoException Se o diretório de destino for inválido.
     */
    public void moverMidia(Midia midia, String novoDiretorio) throws ErroPersistenciaException, ExcecaoCampoException {
        if (midia == null || novoDiretorio == null || novoDiretorio.isEmpty()) {
            throw new ExcecaoCampoException("Mídia e novo diretório são obrigatórios.");
        }
        gerenciador.moverMidia(midia, novoDiretorio);
    }

    /**
     * Solicita a renomeação do arquivo físico e do título da mídia.
     *
     * @param midia A mídia a ser renomeada.
     * @param novoNome O novo nome do arquivo (sem a extensão).
     * @throws ErroPersistenciaException Se falhar ao atualizar o registro.
     * @throws ExcecaoCampoException Se o novo nome for vazio ou nulo.
     * @throws IOException Se houver erro no sistema de arquivos ao renomear.
     */
    public void renomearArquivo(Midia midia, String novoNome) throws ErroPersistenciaException, ExcecaoCampoException, IOException {
        if (midia == null || novoNome == null || novoNome.trim().isEmpty()) {
            throw new ExcecaoCampoException("O novo nome do arquivo não pode ser vazio.");
        }
        gerenciador.renomearArquivoMidia(midia, novoNome);
    }

    /**
     * Método utilitário para extrair a extensão de um arquivo.
     *
     * @param filename O nome completo ou caminho do arquivo.
     * @return A extensão em letras minúsculas (ex: "mp4") ou string vazia se não houver.
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }
}