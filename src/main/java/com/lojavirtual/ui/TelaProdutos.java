package com.lojavirtual.ui;

import com.lojavirtual.model.Eletronico;
import com.lojavirtual.model.Produto;
import com.lojavirtual.repository.ProdutoDAO;
import com.lojavirtual.session.SessaoUsuario;
import com.lojavirtual.util.ImagemUtil;
import com.lojavirtual.util.TarefasSwing;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Tela de produtos (CRUD completo): somente para usuário cadastrado e logado.
 * Create: Cadastrar | Read: Listar todos / Buscar por ID | Update: Atualizar | Delete: Excluir
 */
public class TelaProdutos extends JFrame {

    private final ProdutoDAO dao = new ProdutoDAO();

    //Tabela
    private final DefaultTableModel modelo;
    private final JTable tabela;
    private List<Produto> produtosListados = new ArrayList<>();

    //Campos do formulário
    private final JTextField txtId;
    private final JTextField txtCodigo;
    private final JComboBox<String> cmbTipo;
    private final JTextField txtNome;
    private final JTextField txtDescricao;
    private final JTextField txtPreco;
    private final JTextField txtDesconto;
    private final JTextField txtQuantidade;
    private final JTextField txtGarantia;
    private final JLabel lblImagem;

    //Imagem: nova escolhida (ainda não copiada) e a já gravada do produto selecionado
    private File imagemSelecionada;
    private String imagemAtual = "";

    public TelaProdutos() {
        setTitle("Estoque");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 640);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.setBackground(SystemColor.gray);
        setContentPane(raiz);

        //---------- Topo: voltar ao menu ----------
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topo.setOpaque(false);

        JButton btnVoltar = new JButton("Voltar ao menu");
        topo.add(btnVoltar);
        topo.add(new JLabel("   Usuário: " + usuarioAtual()));
        raiz.add(topo, BorderLayout.NORTH);

        //---------- Centro: tabela ----------
        String[] colunas = {"ID", "Tipo", "Nome", "Preço base", "Desc. %", "Preço final", "Qtd", "Garantia (meses)"};
        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(22);
        raiz.add(new JScrollPane(tabela), BorderLayout.CENTER);

        //---------- Direita: formulário ----------
        JPanel direita = new JPanel(new BorderLayout(0, 8));
        direita.setOpaque(false);
        direita.setPreferredSize(new Dimension(400, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        txtId = new JTextField(18);
        txtId.setEditable(false);
        txtCodigo = new JTextField(18);
        txtCodigo.setEditable(false);
        cmbTipo = new JComboBox<>(new String[]{"Produto", "Eletrônico"});
        txtNome = new JTextField(18);
        txtDescricao = new JTextField(18);
        txtPreco = new JTextField(18);
        txtDesconto = new JTextField("0", 18);
        txtQuantidade = new JTextField("0", 18);
        txtGarantia = new JTextField("0", 18);
        txtGarantia.setEnabled(false);

        int linha = 0;
        adicionarCampo(form, linha++, "ID", txtId);
        adicionarCampo(form, linha++, "Código", txtCodigo);
        adicionarCampo(form, linha++, "Tipo", cmbTipo);
        adicionarCampo(form, linha++, "Nome", txtNome);
        adicionarCampo(form, linha++, "Descrição", txtDescricao);
        adicionarCampo(form, linha++, "Preço base (R$)", txtPreco);
        adicionarCampo(form, linha++, "Desconto (%)", txtDesconto);
        adicionarCampo(form, linha++, "Quantidade", txtQuantidade);
        adicionarCampo(form, linha++, "Garantia (meses)", txtGarantia);

        lblImagem = new JLabel("Sem imagem", SwingConstants.CENTER);
        lblImagem.setPreferredSize(new Dimension(150, 150));
        lblImagem.setOpaque(true);
        lblImagem.setBackground(Color.LIGHT_GRAY);
        lblImagem.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        adicionarCampo(form, linha++, "Imagem", lblImagem);

        JButton btnImagem = new JButton("Escolher imagem...");
        adicionarCampo(form, linha++, "", btnImagem);

        direita.add(form, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new GridLayout(2, 3, 6, 6));
        botoes.setOpaque(false);
        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");
        JButton btnFrete = new JButton("Simular frete");
        botoes.add(btnCadastrar);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);
        botoes.add(btnLimpar);
        botoes.add(btnFrete);
        direita.add(botoes, BorderLayout.SOUTH);

        raiz.add(direita, BorderLayout.EAST);

        //---------- Eventos ----------

        //Garantia só vale para eletrônicos
        cmbTipo.addActionListener(e -> {
            boolean eletronico = cmbTipo.getSelectedIndex() == 1;
            txtGarantia.setEnabled(eletronico);
            if (!eletronico) {
                txtGarantia.setText("0");
            }
        });

        //Selecionar uma linha preenche o formulário
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int linhaSelecionada = tabela.getSelectedRow();
            if (linhaSelecionada >= 0 && linhaSelecionada < produtosListados.size()) {
                preencherFormulario(produtosListados.get(linhaSelecionada));
            }
        });

        btnImagem.addActionListener(e -> {
            File escolhida = ImagemUtil.escolher(this);
            if (escolhida != null) {
                imagemSelecionada = escolhida;
                ImagemUtil.mostrar(lblImagem, ImagemUtil.carregar(escolhida, 150, 150), "Sem imagem");
            }
        });

        btnCadastrar.addActionListener(e -> cadastrar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnFrete.addActionListener(e -> simularFrete());

        btnVoltar.addActionListener(e -> {
            TelaInicio telaInicio = new TelaInicio();
            telaInicio.abreTela();
            dispose();
        });
    }

    //Abre a tela somente se houver usuário logado
    public void abreTela() {
        if (SessaoUsuario.getUsuarioAtual() == null) {
            JOptionPane.showMessageDialog(null,
                    "É necessário estar logado para acessar o estoque.",
                    "Atenção",
                    JOptionPane.ERROR_MESSAGE);
            TelaLogin tLogin = new TelaLogin();
            tLogin.abreTela();
            dispose();
            return;
        }

        setVisible(true);
        listarTodos();
    }

    public void abreTela(int idProduto) {
        if (SessaoUsuario.getUsuarioAtual() == null) {
            JOptionPane.showMessageDialog(null,
                    "É necessário estar logado para acessar o estoque.",
                    "Atenção",
                    JOptionPane.ERROR_MESSAGE);
            new TelaLogin().abreTela();
            dispose();
            return;
        }

        setVisible(true);
        buscarPorId(idProduto);
    }

    // ===================== CRUD =====================

    //READ: lista todos
    private void listarTodos() {
        TarefasSwing.executar(this, dao::listaTodos, produtos -> {
            produtosListados = produtos;
            preencherTabela();
        }, ex -> mensagem("Não foi possível listar produtos: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE));
    }

    //READ: consulta por ID
    private void buscarPorId(int id) {
        TarefasSwing.executar(this, () -> dao.buscaPorId(id), produto -> {
            if (produto == null) {
                mensagem("Produto não encontrado.", JOptionPane.ERROR_MESSAGE);
                return;
            }
            produtosListados = new ArrayList<>(List.of(produto));
            preencherTabela();
            tabela.setRowSelectionInterval(0, 0);
        }, ex -> mensagem("Não foi possível consultar o produto: " + ex.getMessage(),
                JOptionPane.ERROR_MESSAGE));
    }

    //CREATE: cadastra produto ou eletrônico
    private void cadastrar() {
        try {
            Produto p = lerFormulario(false);
            File arquivoImagem = imagemSelecionada;
            TarefasSwing.executar(this, () -> cadastrarProduto(p, arquivoImagem), resultado -> {
                if (resultado.cadastrado()) {
                    imagemSelecionada = null;
                    imagemAtual = "";
                    mensagem("Produto cadastrado com sucesso.", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    listarTodos();
                } else {
                    ImagemUtil.apagar(resultado.caminhoImagem());
                    mensagem("Problemas ao inserir o produto.", JOptionPane.ERROR_MESSAGE);
                }
            }, ex -> mensagem("Não foi possível cadastrar o produto: " + ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE));
        } catch (IllegalArgumentException ex) {
            mensagem(ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    //UPDATE: atualiza os dados do produto selecionado
    private void atualizar() {
        if (txtId.getText().isEmpty()) {
            mensagem("Selecione um produto na tabela para atualizar.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Produto p = lerFormulario(true);
            String imagemAntiga = imagemAtual;
            File arquivoImagem = imagemSelecionada;
            TarefasSwing.executar(this, () -> atualizarProduto(p, arquivoImagem, imagemAntiga), resultado -> {
                if (resultado.atualizado()) {
                    if (resultado.trocouImagem()) {
                        ImagemUtil.apagar(imagemAntiga);
                    }
                    imagemSelecionada = null;
                    imagemAtual = "";
                    mensagem("Produto atualizado com sucesso.", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    listarTodos();
                } else {
                    if (resultado.trocouImagem()) {
                        ImagemUtil.apagar(resultado.caminhoImagem());
                    }
                    mensagem("Problemas ao atualizar o produto.", JOptionPane.ERROR_MESSAGE);
                }
            }, ex -> mensagem("Não foi possível atualizar o produto: " + ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE));
        } catch (IllegalArgumentException ex) {
            mensagem(ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    //DELETE: exclui o produto selecionado
    private void excluir() {
        if (txtId.getText().isEmpty()) {
            mensagem("Selecione um produto na tabela para excluir.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object[] options = {"Sim", "Não"};
        int opcao = JOptionPane.showOptionDialog(this,
                "Deseja excluir o produto \"" + txtNome.getText() + "\"?",
                "Atenção",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null,
                options, options[1]);

        if (opcao == 0) {
            int id = Integer.parseInt(txtId.getText());
            String imagem = imagemAtual;
            TarefasSwing.executar(this, () -> dao.exclui(id), excluido -> {
                if (excluido) {
                    ImagemUtil.apagar(imagem);
                    mensagem("Produto excluído com sucesso.", JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    listarTodos();
                } else {
                    mensagem("Problemas ao excluir o produto.", JOptionPane.ERROR_MESSAGE);
                }
            }, ex -> mensagem("Não foi possível excluir o produto: " + ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE));
        }
    }

    //Demonstra a sobrecarga de calcularFrete() e a sobrescrita de calcularPrecoFinal()
    private void simularFrete() {
        try {
            Produto p = lerFormulario(!txtId.getText().isEmpty());

            String texto = JOptionPane.showInputDialog(this, "Distância de entrega (km):",
                    "Simulação de frete", JOptionPane.QUESTION_MESSAGE);
            if (texto == null) {
                return;
            }
            BigDecimal km = new BigDecimal(texto.trim().replace(',', '.'));

            mensagem("Produto: " + p.getNome() + " (" + p.getTipo() + ")\n"
                    + "Preço final: R$ " + moeda(p.calcularPrecoFinal()) + "\n"
                    + "Frete normal (" + km + " km): R$ " + moeda(p.calcularFrete(km)) + "\n"
                    + "Frete express (" + km + " km): R$ " + moeda(p.calcularFrete(km, true)),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            mensagem("Distância inválida!", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            mensagem(ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== Formulário e tabela =====================

    //Monta um Produto ou Eletronico a partir dos campos (valida usando os setters do modelo)
    private Produto lerFormulario(boolean existente) {
        String nome = txtNome.getText().trim();
        String descricao = txtDescricao.getText().trim();
        BigDecimal preco = lerDecimal(txtPreco, "Preço base");
        BigDecimal desconto = lerDecimal(txtDesconto, "Desconto");
        int quantidade = lerInt(txtQuantidade, "Quantidade");

        Produto p;
        if (cmbTipo.getSelectedIndex() == 1) {
            int garantia = lerInt(txtGarantia, "Garantia");
            p = new Eletronico(nome, descricao, preco, desconto, quantidade, garantia);
        } else {
            p = new Produto(nome, descricao, preco, desconto, quantidade);
        }

        if (existente) {
            p.setId(Integer.parseInt(txtId.getText()));
            p.setCodigo(txtCodigo.getText());
            p.setImagem(imagemAtual);
        }
        return p;
    }

    private BigDecimal lerDecimal(JTextField campo, String nomeCampo) {
        String texto = campo.getText().trim().replace(',', '.');
        if (texto.isEmpty()) {
            campo.grabFocus();
            throw new IllegalArgumentException("Campo " + nomeCampo + " precisa ser informado!");
        }
        try {
            return new BigDecimal(texto).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            campo.grabFocus();
            throw new IllegalArgumentException("Campo " + nomeCampo + " inválido!");
        }
    }

    private int lerInt(JTextField campo, String nomeCampo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            campo.grabFocus();
            throw new IllegalArgumentException("Campo " + nomeCampo + " precisa ser informado!");
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            campo.grabFocus();
            throw new IllegalArgumentException("Campo " + nomeCampo + " inválido (use número inteiro)!");
        }
    }

    private void preencherTabela() {
        modelo.setRowCount(0);
        for (Produto p : produtosListados) {
            String tipo = "Produto";
            String garantia = "-";
            if (p instanceof Eletronico eletronico) {
                tipo = "Eletrônico";
                garantia = String.valueOf(eletronico.getMesesGarantia());
            }
            modelo.addRow(new Object[]{
                p.getId(),
                tipo,
                p.getNome(),
                moeda(p.getPrecoBase()),
                moeda(p.getDesconto()),
                moeda(p.calcularPrecoFinal()), //polimorfismo: usa a regra do tipo real
                p.getQuantidade(),
                garantia
            });
        }
    }

    private void preencherFormulario(Produto p) {
        txtId.setText(String.valueOf(p.getId()));
        txtCodigo.setText(p.getCodigo());
        txtNome.setText(p.getNome());
        txtDescricao.setText(p.getDescricao());
        txtPreco.setText(String.valueOf(p.getPrecoBase()));
        txtDesconto.setText(String.valueOf(p.getDesconto()));
        txtQuantidade.setText(String.valueOf(p.getQuantidade()));

        if (p instanceof Eletronico eletronico) {
            cmbTipo.setSelectedIndex(1);
            txtGarantia.setText(String.valueOf(eletronico.getMesesGarantia()));
        } else {
            cmbTipo.setSelectedIndex(0);
            txtGarantia.setText("0");
        }

        imagemSelecionada = null;
        imagemAtual = p.getImagem();
        ImagemUtil.mostrar(lblImagem, ImagemUtil.carregar(imagemAtual, 150, 150), "Sem imagem");
    }

    private void limparFormulario() {
        txtId.setText("");
        txtCodigo.setText("");
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtDesconto.setText("0");
        txtQuantidade.setText("0");
        txtGarantia.setText("0");
        cmbTipo.setSelectedIndex(0);
        imagemSelecionada = null;
        imagemAtual = "";
        ImagemUtil.mostrar(lblImagem, null, "Sem imagem");
        tabela.clearSelection();
        txtNome.grabFocus();
    }

    private void adicionarCampo(JPanel painel, int linha, String rotulo, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 4, 3, 4);
        g.gridy = linha;
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0;
        g.weightx = 0;
        painel.add(new JLabel(rotulo), g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = (campo instanceof JLabel) ? GridBagConstraints.NONE : GridBagConstraints.HORIZONTAL;
        painel.add(campo, g);
    }

    private void mensagem(String texto, int tipo) {
        JOptionPane.showMessageDialog(this, texto, "Atenção", tipo);
    }

    private String moeda(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP)
                .toPlainString().replace('.', ',');
    }

    private String usuarioAtual() {
        return SessaoUsuario.getUsuarioAtual() == null
                ? "" : SessaoUsuario.getUsuarioAtual().getNome();
    }

    private ResultadoOperacao cadastrarProduto(Produto produto, File arquivoImagem)
            throws IOException, java.sql.SQLException {
        String caminhoImagem = "";
        try {
            if (arquivoImagem != null) {
                caminhoImagem = ImagemUtil.salvar(arquivoImagem, "produtos");
            }
            produto.setImagem(caminhoImagem);
            boolean cadastrado = dao.cadastra(produto);
            return new ResultadoOperacao(cadastrado, caminhoImagem);
        } catch (IOException | java.sql.SQLException ex) {
            ImagemUtil.apagar(caminhoImagem);
            throw ex;
        }
    }

    private ResultadoAtualizacao atualizarProduto(
            Produto produto, File arquivoImagem, String imagemAntiga)
            throws IOException, java.sql.SQLException {
        boolean trocouImagem = arquivoImagem != null;
        String caminhoImagem = trocouImagem ? "" : imagemAntiga;
        try {
            if (trocouImagem) {
                caminhoImagem = ImagemUtil.salvar(arquivoImagem, "produtos");
            }
            produto.setImagem(caminhoImagem);
            boolean atualizado = dao.atualiza(produto);
            return new ResultadoAtualizacao(atualizado, trocouImagem, caminhoImagem);
        } catch (IOException | java.sql.SQLException ex) {
            if (trocouImagem) {
                ImagemUtil.apagar(caminhoImagem);
            }
            throw ex;
        }
    }

    private record ResultadoOperacao(boolean cadastrado, String caminhoImagem) {
    }

    private record ResultadoAtualizacao(boolean atualizado, boolean trocouImagem, String caminhoImagem) {
    }
}
