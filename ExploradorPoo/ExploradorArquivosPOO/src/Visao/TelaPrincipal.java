package Visao;

import Modelo.*;
import excecoes.ErroPersistenciaException;
import excecoes.MidiaJaCadastradaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Representa a interface gráfica principal (GUI) da aplicação.
 * <p>
 * Esta classe herda de {@link JFrame} e atua como a camada de Visão (View).
 * Ela é responsável por exibir a lista de mídias, fornecer formulários para
 * cadastro/edição e capturar as ações do usuário para repassar ao controlador
 * {@link GerenciadorMidia}.
 * </p>
 *
 * @author Seu Nome
 * @version 1.0
 */
public class TelaPrincipal extends JFrame {

    /**
     * Controlador responsável pela lógica de negócios e persistência.
     */
    private GerenciadorMidia gerenciador;

    private JTable tabelaMidias;
    private DefaultTableModel modeloTabela;

    private JComboBox<String> filtroTipo;
    private JComboBox<String> filtroCategoria;
    private JComboBox<String> filtroOrdem;

    /**
     * Armazena o último diretório acessado pelo JFileChooser para melhorar a usabilidade,
     * evitando que o usuário tenha que navegar tudo novamente a cada operação.
     */
    private File ultimoDiretorio = new File(System.getProperty("user.home"));

    /**
     * Construtor da Tela Principal.
     * <p>
     * Inicializa o gerenciador, configura as propriedades da janela (tamanho, título, layout),
     * constrói os componentes visuais (tabela, filtros, botões) e carrega os dados iniciais.
     * </p>
     */
    public TelaPrincipal() {
        gerenciador = new GerenciadorMidia();

        setTitle("📀 Gerenciador de Mídias");
        setSize(1100, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        criarTabela();
        criarPainelFiltros();
        criarBotoes();

        atualizarTabela();
    }

    // --------- TABELA -----------

    /**
     * Configura e inicializa a JTable com o modelo de colunas padrão.
     * <p>
     * As colunas definidas são: Título, Tipo, Categoria, Duração, Tamanho, Info extra e Local.
     * O modelo é configurado para impedir a edição direta das células (`isCellEditable` retorna false).
     * </p>
     */
    private void criarTabela() {
        modeloTabela = new DefaultTableModel(
                new Object[]{"Título", "Tipo", "Categoria", "Duração", "Tamanho", "Info extra", "Local"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Bloqueia edição direta na célula para evitar erros
            }
        };

        tabelaMidias = new JTable(modeloTabela);
        JScrollPane scroll = new JScrollPane(tabelaMidias);

        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Atualiza o conteúdo visual da tabela com base nos filtros atuais.
     * <p>
     * O método limpa a tabela, solicita a lista filtrada ao {@link GerenciadorMidia}
     * e repovoa as linhas. A coluna "Info extra" é preenchida dinamicamente dependendo
     * do tipo da mídia (polimorfismo).
     * </p>
     */
    private void atualizarTabela() {
        modeloTabela.setRowCount(0);

        List<Midia> lista = gerenciador.getMidiasFiltradas(
                (String) filtroTipo.getSelectedItem(),
                (String) filtroCategoria.getSelectedItem(),
                (String) filtroOrdem.getSelectedItem()
        );

        for (Midia m : lista) {
            modeloTabela.addRow(new Object[]{
                    m.getTitulo(),
                    m.getClass().getSimpleName(),
                    m.getCategoria(),
                    m.getDuracao(),
                    m.getTamanhoDisco(), // novo campo exibido
                    infoExtra(m),
                    m.getLocal()
            });
        }
    }

    /**
     * Helper para extrair informações específicas de cada subclasse de Mídia.
     *
     * @param m O objeto Midia.
     * @return Uma String formatada (ex: "Idioma: Português" para filmes).
     */
    private String infoExtra(Midia m) {
        if (m instanceof Filme f) return "Idioma: " + f.getIdioma();
        if (m instanceof Musica c) return "Artista: " + c.getArtista();
        if (m instanceof Livro l) return "Autores: " + l.getAutores();
        return "-";
    }

    // --------- FILTROS -----------

    /**
     * Cria o painel superior contendo os filtros de pesquisa (Tipo, Categoria, Ordenação).
     */
    private void criarPainelFiltros() {
        JPanel filtroPanel = new JPanel();

        filtroTipo = new JComboBox<>(new String[]{"Todos", "Filme", "Musica", "Livro"});
        filtroCategoria = new JComboBox<>(new String[]{"", "Ação", "Aventura", "Rock", "Drama", "Terror"});
        filtroOrdem = new JComboBox<>(new String[]{"", "Alfabética (A-Z)", "Duração (Crescente)"});

        filtroPanel.add(new JLabel("Tipo:"));
        filtroPanel.add(filtroTipo);

        filtroPanel.add(new JLabel("Categoria:"));
        filtroPanel.add(filtroCategoria);

        filtroPanel.add(new JLabel("Ordenar:"));
        filtroPanel.add(filtroOrdem);

        JButton filtrar = new JButton("Filtrar");
        filtrar.addActionListener(e -> atualizarTabela());
        filtroPanel.add(filtrar);

        add(filtroPanel, BorderLayout.NORTH);
    }

    // ---------------- BOTÕES E AÇÕES --------------------

    /**
     * Cria o painel inferior contendo os botões de ação (Adicionar, Editar, Remover, etc.).
     */
    private void criarBotoes() {
        JPanel painel = new JPanel();

        painel.add(btn("➕ Adicionar", e -> adicionarMidia()));
        painel.add(btn("✏ Editar", e -> editarMidia()));
        painel.add(btn("❌ Remover", e -> removerMidia()));
        painel.add(btn("📁 Mover", e -> moverMidia()));
        painel.add(btn("📝 Renomear", e -> renomearMidia()));
        painel.add(btn("🔄 Recarregar", e -> atualizarTabela()));

        add(painel, BorderLayout.SOUTH);
    }

    /**
     * Método utilitário para criar botões com ActionListeners de forma concisa.
     *
     * @param texto O texto do botão.
     * @param acao A expressão lambda ou listener a ser executado no clique.
     * @return O objeto JButton configurado.
     */
    private JButton btn(String texto, java.awt.event.ActionListener acao) {
        JButton b = new JButton(texto);
        b.addActionListener(acao);
        return b;
    }

    /**
     * Recupera o objeto {@link Midia} correspondente à linha selecionada na tabela.
     * <p>
     * Utiliza a coluna "Local" (índice 6) como chave única para buscar o objeto
     * no gerenciador.
     * </p>
     *
     * @return O objeto Midia selecionado ou {@code null} se nenhuma linha estiver selecionada (exibe alerta).
     */
    private Midia getMidiaSelecionada() {
        int linha = tabelaMidias.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma mídia na tabela.");
            return null;
        }
        String local = tabelaMidias.getValueAt(linha, 6).toString();
        return gerenciador.buscarMidiaPorLocal(local);
    }

    // ---------------- ADICIONAR (Atualizado) --------------------

    /**
     * Fluxo para adicionar uma nova mídia ao sistema.
     * <p>
     * O processo envolve:
     * <ol>
     * <li>Selecionar o tipo de mídia via Popup.</li>
     * <li>Selecionar o arquivo físico via {@link JFileChooser}.</li>
     * <li>Preencher um formulário com metadados (Título, Categoria, etc.).</li>
     * <li>Validar os dados (ex: checar se nomes contêm números proibidos).</li>
     * <li>Instanciar a classe correta e passar ao gerenciador.</li>
     * </ol>
     * </p>
     */
    private void adicionarMidia() {
        // 1. Escolher o TIPO primeiro
        String[] tipos = {"Filme", "Música", "Livro"};
        String tipoEscolhido = (String) JOptionPane.showInputDialog(this, "Tipo de mídia:", "Adicionar",
                JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);

        if (tipoEscolhido == null) return;

        // 2. Ler a pasta (File Chooser) usando a memória do ultimoDiretorio
        JFileChooser seletor = new JFileChooser(ultimoDiretorio);
        seletor.setDialogTitle("Selecione o arquivo de " + tipoEscolhido);

        // Configura filtro visual
        if (tipoEscolhido.equals("Filme")) {
            seletor.addChoosableFileFilter(new FileNameExtensionFilter("Vídeo (MP4, MKV)", "mp4", "mkv"));
        } else if (tipoEscolhido.equals("Música")) {
            seletor.addChoosableFileFilter(new FileNameExtensionFilter("Áudio (MP3, WAV)", "mp3", "wav"));
        } else {
            seletor.addChoosableFileFilter(new FileNameExtensionFilter("Texto (PDF, EPUB)", "pdf", "epub"));
        }

        if (seletor.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arq = seletor.getSelectedFile();
            ultimoDiretorio = arq.getParentFile(); // Atualiza a pasta padrão para a próxima vez

            // 3. Criar Painel (Formulário) com todos os campos
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10)); // Grid de 2 colunas

            // Tenta adivinhar o título pelo nome do arquivo (remove extensão)
            String nomeSugestao = arq.getName().contains(".") ?
                    arq.getName().substring(0, arq.getName().lastIndexOf('.')) : arq.getName();

            JTextField txtTitulo = new JTextField(nomeSugestao);
            JTextField txtCategoria = new JTextField();
            JTextField txtDuracao = new JTextField("0");
            JTextField txtExtra = new JTextField(); // Campo variável
            JTextField txtTamanho = new JTextField(""); // novo campo tamanhoDisco

            panel.add(new JLabel("Título:"));
            panel.add(txtTitulo);
            panel.add(new JLabel("Categoria:"));
            panel.add(txtCategoria);
            panel.add(new JLabel("Duração (min / seg / páginas):"));
            panel.add(txtDuracao);

            // Define label do campo extra
            String labelExtra = switch (tipoEscolhido) {
                case "Filme" -> "Idioma:";
                case "Música" -> "Artista:";
                case "Livro" -> "Autor(es):";
                default -> "Extra:";
            };
            panel.add(new JLabel(labelExtra));
            panel.add(txtExtra);

            panel.add(new JLabel("Tamanho (tamanhoDisco):"));
            panel.add(txtTamanho);

            // 4. Mostra o Popup único
            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Dados da Mídia", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String titulo = txtTitulo.getText().trim();
                    String categoria = txtCategoria.getText().trim();
                    String duracaoStr = txtDuracao.getText().trim();
                    String extra = txtExtra.getText().trim();
                    String tamanhoDisco = txtTamanho.getText().trim();

                    // Validações básicas
                    if (titulo.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Erro: Título obrigatório.");
                        return;
                    }
                    int duracao;
                    try {
                        duracao = Integer.parseInt(duracaoStr);
                        if (duracao < 0) throw new NumberFormatException();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Erro: Duração deve ser um número inteiro não-negativo.");
                        return;
                    }

                    // Verifica campos específicos (não podem ter números)
                    if (tipoEscolhido.equals("Música") && containsDigits(extra)) {
                        JOptionPane.showMessageDialog(this, "Erro: Artista não pode conter números.");
                        return;
                    }
                    if (tipoEscolhido.equals("Livro") && containsDigits(extra)) {
                        JOptionPane.showMessageDialog(this, "Erro: Autor(es) não pode conter números.");
                        return;
                    }
                    if (tipoEscolhido.equals("Filme") && containsDigits(extra)) {
                        JOptionPane.showMessageDialog(this, "Erro: Idioma não pode conter números.");
                        return;
                    }
                    if (tamanhoDisco.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Erro: Tamanho do disco (tamanhoDisco) é obrigatório.");
                        return;
                    }

                    Midia novaMidia = switch (tipoEscolhido) {
                        case "Filme" -> new Filme(titulo, arq.getAbsolutePath(), duracao, categoria, extra, tamanhoDisco);
                        case "Música" -> new Musica(arq.getAbsolutePath(), titulo, categoria, duracao, extra, tamanhoDisco);
                        case "Livro" -> new Livro(arq.getAbsolutePath(), titulo, categoria, duracao, extra, tamanhoDisco);
                        default -> null;
                    };

                    gerenciador.adicionarMidia(novaMidia);
                    atualizarTabela();
                    JOptionPane.showMessageDialog(this, "Mídia adicionada com sucesso!");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: Duração deve ser um número inteiro.");
                } catch (MidiaJaCadastradaException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
                } catch (ErroPersistenciaException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao persistir: " + ex.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
                }
            }
        }
    }

    // ---------------- EDITAR (Atualizado para Popup) --------------------

    /**
     * Abre diálogo para editar a mídia selecionada.
     * <p>
     * Preenche o formulário com os dados atuais do objeto selecionado.
     * Aplica as mesmas regras de validação da adição. Ao salvar, substitui
     * o objeto antigo pelo novo no gerenciador.
     * </p>
     */
    private void editarMidia() {
        Midia midia = getMidiaSelecionada();
        if (midia == null) return;

        // Cria o painel já preenchido com os dados atuais
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField txtTitulo = new JTextField(midia.getTitulo());
        JTextField txtCategoria = new JTextField(midia.getCategoria());
        JTextField txtDuracao = new JTextField(String.valueOf(midia.getDuracao()));
        JTextField txtExtra = new JTextField();
        JTextField txtTamanho = new JTextField(midia.getTamanhoDisco()); // novo campo

        panel.add(new JLabel("Título:"));
        panel.add(txtTitulo);
        panel.add(new JLabel("Categoria:"));
        panel.add(txtCategoria);
        panel.add(new JLabel("Duração (min / seg / páginas):"));
        panel.add(txtDuracao);

        String labelExtra = "Info:";
        String valorExtra = "";

        if (midia instanceof Filme f) {
            labelExtra = "Idioma:";
            valorExtra = f.getIdioma();
        } else if (midia instanceof Musica m) {
            labelExtra = "Artista:";
            valorExtra = m.getArtista();
        } else if (midia instanceof Livro l) {
            labelExtra = "Autores:";
            valorExtra = l.getAutores();
        }

        txtExtra.setText(valorExtra);
        panel.add(new JLabel(labelExtra));
        panel.add(txtExtra);

        panel.add(new JLabel("Tamanho (tamanhoDisco):"));
        panel.add(txtTamanho);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Editar Mídia", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String novoTitulo = txtTitulo.getText().trim();
                String novaCategoria = txtCategoria.getText().trim();
                String novaDuracaoStr = txtDuracao.getText().trim();
                String novoExtra = txtExtra.getText().trim();
                String novoTamanho = txtTamanho.getText().trim();

                if (novoTitulo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Erro: Título obrigatório.");
                    return;
                }

                int novaDuracao;
                try {
                    novaDuracao = Integer.parseInt(novaDuracaoStr);
                    if (novaDuracao < 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: Duração inválida.");
                    return;
                }

                // validações específicas
                if (midia instanceof Musica && containsDigits(novoExtra)) {
                    JOptionPane.showMessageDialog(this, "Erro: Artista não pode conter números.");
                    return;
                }
                if (midia instanceof Livro && containsDigits(novoExtra)) {
                    JOptionPane.showMessageDialog(this, "Erro: Autor(es) não pode conter números.");
                    return;
                }
                if (midia instanceof Filme && containsDigits(novoExtra)) {
                    JOptionPane.showMessageDialog(this, "Erro: Idioma não pode conter números.");
                    return;
                }
                if (novoTamanho.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Erro: Tamanho do disco (tamanhoDisco) é obrigatório.");
                    return;
                }

                Midia novaMidia = null;
                if (midia instanceof Filme) {
                    novaMidia = new Filme(novoTitulo, midia.getLocal(), novaDuracao, novaCategoria, novoExtra, novoTamanho);
                } else if (midia instanceof Musica) {
                    novaMidia = new Musica(midia.getLocal(), novoTitulo, novaCategoria, novaDuracao, novoExtra, novoTamanho);
                } else if (midia instanceof Livro) {
                    novaMidia = new Livro(midia.getLocal(), novoTitulo, novaCategoria, novaDuracao, novoExtra, novoTamanho);
                }

                gerenciador.editarMidia(midia, novaMidia);
                atualizarTabela();
                JOptionPane.showMessageDialog(this, "Mídia editada!");

            } catch (ErroPersistenciaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    // ---------------- REMOVER --------------------

    /**
     * Solicita a remoção da mídia selecionada.
     * <p>
     * Exibe uma caixa de diálogo de confirmação antes de proceder com a exclusão
     * no gerenciador.
     * </p>
     */
    private void removerMidia() {
        Midia midia = getMidiaSelecionada();
        if (midia == null) return;

        if (JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover?",
                "Remover mídia", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                gerenciador.removerMidia(midia);
                atualizarTabela();
                JOptionPane.showMessageDialog(this, "Removida!");
            } catch (ErroPersistenciaException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    // ---------------- MOVER --------------------

    /**
     * Abre um seletor de diretórios para mover o arquivo físico da mídia.
     * <p>
     * Caso selecionado um destino válido, solicita ao gerenciador a movimentação
     * do arquivo físico e do arquivo .tpoo associado.
     * </p>
     */
    private void moverMidia() {
        Midia midia = getMidiaSelecionada();
        if (midia == null) return;

        JFileChooser seletor = new JFileChooser(ultimoDiretorio);
        seletor.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (seletor.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File destino = seletor.getSelectedFile();
                ultimoDiretorio = destino; // Lembra o diretório também ao mover

                gerenciador.moverMidia(midia, destino.getAbsolutePath());
                atualizarTabela();
                JOptionPane.showMessageDialog(this, "Arquivo movido!");
            } catch (ErroPersistenciaException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    // ---------------- RENOMEAR --------------------

    /**
     * Solicita um novo nome para o arquivo da mídia.
     * <p>
     * Se o usuário fornecer um nome válido, o sistema renomeia o arquivo físico
     * e atualiza o título da mídia no registro.
     * </p>
     */
    private void renomearMidia() {
        Midia midia = getMidiaSelecionada();
        if (midia == null) return;

        String novoNome = JOptionPane.showInputDialog(this, "Novo nome do arquivo (sem extensão):", "Renomear Arquivo", JOptionPane.PLAIN_MESSAGE);
        if (novoNome == null || novoNome.isBlank()) return;

        try {
            gerenciador.renomearArquivoMidia(midia, novoNome);

            atualizarTabela();

            JOptionPane.showMessageDialog(this, "Arquivo renomeado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /**
     * Método utilitário para validação de campos de texto.
     *
     * @param s A string a ser verificada.
     * @return {@code true} se a string contiver algum dígito numérico, {@code false} caso contrário.
     */
    private boolean containsDigits(String s) {
        if (s == null) return false;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) return true;
        }
        return false;
    }
}