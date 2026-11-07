package com.lojavirtual.ui;
import com.lojavirtual.repository.UsuarioDAO;
import com.lojavirtual.session.SessaoUsuario;
import com.lojavirtual.util.TarefasSwing;

/*
    Importações de todas as bibliotecas que estou utilizando na classe
*/
import java.awt.Font;                //Trabalhar com fontes
import java.awt.SystemColor;         //Trabalhar com cores
import javax.swing.JButton;          //Trabalhar com botões
import javax.swing.JFrame;           //Trabalhar com frames
import javax.swing.JLabel;           //Trabalhar com labels
import javax.swing.JOptionPane;      //Trabalhar com mensagens
import javax.swing.JPanel;           //Trabalhar com painéis
import javax.swing.JPasswordField;   //Trabalhar com campos de senha
import javax.swing.JTextField;       //Trabalhar com campos de texto

/**
 * Tela de login do sistema.
 */
public class TelaLogin extends JFrame {

    //Tela (JPanel)
    private final JPanel panelTela;

    //Campos na tela
    private final JTextField txtUsuario;
    private final JPasswordField pswSenha;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    //Método construtor
    public TelaLogin() {
        //Coloca o objeto na referência do centro da tela
        setLocationRelativeTo(null);

        //Não permite que o objeto seja expandido
        setResizable(false);

        //Coloca título na caixa JFrame
        setTitle("Login");

        //Quando clicado no X, encerro todo o programa
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Define posicionamento e tamanho: x, y, width, height
        setBounds(500, 200, 426, 212);

        //Crio um objeto JPanel e atribuo à variável tela
        panelTela = new JPanel();

        //Define a cor de fundo do JPanel (tela)
        panelTela.setBackground(SystemColor.gray);
        setContentPane(panelTela);

        //Vou utilizar o meu panel sem utilizar o layout padrão
        panelTela.setLayout(null);

        //Adicionando elementos na tela
        JLabel lblIdentificacao = new JLabel("IDENTIFICAÇÃO");
        lblIdentificacao.setBounds(144, 0, 160, 39);
        lblIdentificacao.setFont(new Font("Arial", 3, 19));
        panelTela.add(lblIdentificacao);

        //Identificação e posicionamento dos labels
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setBounds(24, 65, 70, 15);
        panelTela.add(lblUsuario);

        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setBounds(24, 92, 70, 15);
        panelTela.add(lblSenha);

        //Identificação e posicionamento dos text fields
        txtUsuario = new JTextField();
        txtUsuario.setBounds(112, 63, 219, 19);
        panelTela.add(txtUsuario);
        txtUsuario.setColumns(10);

        pswSenha = new JPasswordField();
        pswSenha.setBounds(112, 90, 219, 19);
        panelTela.add(pswSenha);

        //Identificação e posicionamento dos botões
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(200, 136, 117, 25);
        panelTela.add(btnEntrar);

        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBounds(50, 136, 117, 25);
        panelTela.add(btnCadastrar);

        //Ação no botão de entrar no sistema
        btnEntrar.addActionListener(e -> {
            String usuario = txtUsuario.getText().trim();
            String senha = new String(pswSenha.getPassword());

            if (usuario.isEmpty()) {
                //Vamos dar uma mensagem na tela
                JOptionPane.showMessageDialog(null,
                        "Campo usuário precisa ser informado!",
                        "Atenção",
                        JOptionPane.ERROR_MESSAGE);
                //Voltar o cursor para o campo txtUsuario
                txtUsuario.grabFocus();
            } else if (senha.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Campo senha precisa ser informado!",
                        "Atenção",
                        JOptionPane.ERROR_MESSAGE);
                //Voltar o cursor para o campo pswSenha
                pswSenha.grabFocus();
            } else {
                btnEntrar.setEnabled(false);
                TarefasSwing.executar(this, () -> usuarioDAO.autenticar(usuario, senha), usuarioAutenticado -> {
                    btnEntrar.setEnabled(true);
                    if (usuarioAutenticado != null) {
                        SessaoUsuario.iniciar(usuarioAutenticado);
                        new TelaInicio().abreTela();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Usuário inválido ou inexistente",
                                "Atenção", JOptionPane.ERROR_MESSAGE);
                        limpaText();
                        txtUsuario.grabFocus();
                    }
                }, ex -> {
                    btnEntrar.setEnabled(true);
                    JOptionPane.showMessageDialog(this,
                            "Não foi possível acessar o banco de dados: " + ex.getMessage(),
                            "Erro de conexão", JOptionPane.ERROR_MESSAGE);
                });
            }
        });

        //Ação no botão para cadastrar usuário
        btnCadastrar.addActionListener(e -> {
            //Instancio a classe TelaCadastro
            TelaCadastro tCadastro = new TelaCadastro();
            tCadastro.abreTela();
            dispose();
        });
    }

    public void abreTela() {
        setVisible(true);
    }

    public void limpaText() {
        txtUsuario.setText("");
        pswSenha.setText("");
    }
}
