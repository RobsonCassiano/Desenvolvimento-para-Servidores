package com.lojavirtual.ui;

import com.lojavirtual.repository.UsuarioDAO;
import com.lojavirtual.util.ImagemUtil;
import com.lojavirtual.util.TarefasSwing;
import com.lojavirtual.util.ValidadorUsuario;

/*
    Importações de todas as bibliotecas que estou utilizando na classe
*/
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Tela de cadastro de usuário (Create).
 */
public class TelaCadastro extends JFrame {

    //Declaração dos atributos de tela
    private final JPanel tela;
    private final JTextField txtNome;
    private final JTextField txtUsuario;
    private final JPasswordField passSenha;
    private final JPasswordField passConfSenha;
    private final JLabel lblFoto;

    //Foto escolhida (ainda não copiada para a pasta de imagens)
    private File fotoSelecionada;

    //Validações de usuário e cadastro corretos
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    //Método construtor da classe
    public TelaCadastro() {
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Cadastro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 200, 470, 230);

        tela = new JPanel();
        tela.setBackground(SystemColor.gray);
        setContentPane(tela);
        tela.setLayout(null);

        //Adicionando elementos na tela
        JLabel lblIdentificacao = new JLabel("Informar campos para cadastro");
        lblIdentificacao.setBounds(60, 0, 500, 39);
        lblIdentificacao.setFont(new Font("Arial", 3, 19));
        tela.add(lblIdentificacao);

        JLabel lblNome = new JLabel("Nome");
        lblNome.setBounds(24, 50, 70, 15);
        tela.add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(120, 50, 219, 19);
        tela.add(txtNome);
        txtNome.setColumns(10);

        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setBounds(24, 75, 70, 15);
        tela.add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(120, 75, 219, 19);
        tela.add(txtUsuario);
        txtUsuario.setColumns(10);

        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setBounds(24, 100, 70, 15);
        tela.add(lblSenha);

        passSenha = new JPasswordField();
        passSenha.setBounds(120, 100, 219, 19);
        tela.add(passSenha);

        JLabel lblConfSenha = new JLabel("Confirmar Senha");
        lblConfSenha.setBounds(24, 125, 100, 15);
        tela.add(lblConfSenha);

        passConfSenha = new JPasswordField();
        passConfSenha.setBounds(120, 125, 219, 19);
        tela.add(passConfSenha);

        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBounds(200, 156, 117, 25);
        tela.add(btnCadastrar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(50, 156, 117, 25);
        tela.add(btnCancelar);

        //Foto do usuário (upload)
        lblFoto = new JLabel("Sem foto", SwingConstants.CENTER);
        lblFoto.setBounds(350, 45, 90, 90);
        lblFoto.setOpaque(true);
        lblFoto.setBackground(Color.LIGHT_GRAY);
        lblFoto.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tela.add(lblFoto);

        JButton btnFoto = new JButton("Escolher foto");
        btnFoto.setBounds(340, 140, 110, 25);
        tela.add(btnFoto);

        btnFoto.addActionListener(e -> {
            File escolhida = ImagemUtil.escolher(this);
            if (escolhida != null) {
                fotoSelecionada = escolhida;
                ImagemUtil.mostrar(lblFoto, ImagemUtil.carregar(escolhida, 90, 90), "Sem foto");
            }
        });

        //Botão Cancelar: retorna para a tela de Login
        btnCancelar.addActionListener(e -> {
            TelaLogin tLogin = new TelaLogin();
            tLogin.abreTela();
            dispose();
        });

        //Ação do botão cadastrar usuário na base de dados
        btnCadastrar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String usuario = txtUsuario.getText().trim();
            String senha = new String(passSenha.getPassword());
            String confirmacao = new String(passConfSenha.getPassword());

            var erroValidacao = ValidadorUsuario.validarCadastro(nome, usuario, senha, confirmacao);
            if (erroValidacao.isPresent()) {
                JOptionPane.showMessageDialog(this, erroValidacao.get(), "Atenção", JOptionPane.ERROR_MESSAGE);
                return;
            }

            File foto = fotoSelecionada;
            TarefasSwing.executar(this, () -> cadastrarUsuario(nome, usuario, senha, foto), resultado -> {
                if (resultado.usuarioExistente()) {
                    JOptionPane.showMessageDialog(this, "Usuário já existente na base de dados.",
                            "Atenção", JOptionPane.ERROR_MESSAGE);
                } else if (resultado.cadastrado()) {
                    JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso.",
                            "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    new TelaLogin().abreTela();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Problemas ao inserir o usuário.",
                            "Atenção", JOptionPane.ERROR_MESSAGE);
                }
            }, ex -> JOptionPane.showMessageDialog(this,
                    "Não foi possível concluir o cadastro: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE));
        });
    }

    private ResultadoCadastro cadastrarUsuario(String nome, String usuario, String senha, File foto)
            throws IOException, java.sql.SQLException {
        if (usuarioDAO.existe(usuario)) {
            return new ResultadoCadastro(true, false);
        }

        String caminhoFoto = "";
        try {
            if (foto != null) {
                caminhoFoto = ImagemUtil.salvar(foto, "usuarios");
            }
            boolean cadastrado = usuarioDAO.cadastra(nome, usuario, senha, caminhoFoto);
            if (!cadastrado) {
                ImagemUtil.apagar(caminhoFoto);
            }
            return new ResultadoCadastro(false, cadastrado);
        } catch (IOException | java.sql.SQLException ex) {
            ImagemUtil.apagar(caminhoFoto);
            throw ex;
        }
    }

    private record ResultadoCadastro(boolean usuarioExistente, boolean cadastrado) {
    }

    public void abreTela() {
        setVisible(true);
    }
}
