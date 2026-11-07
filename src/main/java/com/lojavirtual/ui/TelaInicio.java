package com.lojavirtual.ui;

import com.lojavirtual.repository.UsuarioDAO;
import com.lojavirtual.session.SessaoUsuario;
import com.lojavirtual.util.ImagemUtil;
import com.lojavirtual.util.TarefasSwing;

import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Tela inicial (home) exibida após o login:
 * Estoque, Alterar Dados, Excluir e Sair.
 */
public class TelaInicio extends JFrame {

    private final JPanel tela;

    //Atributo que faremos para validar a Exclusão
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public TelaInicio() {
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 200, 520, 260);

        tela = new JPanel();
        tela.setBackground(SystemColor.gray);
        setContentPane(tela);
        tela.setLayout(null);

        JLabel lblUsuario = new JLabel("Bem vindo " + SessaoUsuario.getUsuarioAtual().getNome());
        lblUsuario.setBounds(24, 65, 340, 15);
        tela.add(lblUsuario);

        JLabel lblBusca = new JLabel("Buscar produto por ID:");
        lblBusca.setBounds(24, 130, 135, 25);
        tela.add(lblBusca);

        JTextField txtBusca = new JTextField();
        txtBusca.setBounds(165, 130, 90, 25);
        tela.add(txtBusca);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(265, 130, 95, 25);
        tela.add(btnBuscar);

        //Foto do usuário logado
        JLabel lblFoto = new JLabel("Sem foto", SwingConstants.CENTER);
        lblFoto.setBounds(390, 20, 100, 100);
        lblFoto.setOpaque(true);
        lblFoto.setBackground(Color.LIGHT_GRAY);
        lblFoto.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ImagemUtil.mostrar(lblFoto,
                ImagemUtil.carregar(SessaoUsuario.getUsuarioAtual().getFoto(), 100, 100), "Sem foto");
        tela.add(lblFoto);

        JButton btnEstoque = new JButton("Estoque");
        btnEstoque.setBounds(10, 170, 110, 25);
        tela.add(btnEstoque);

        JButton btnAlterar = new JButton("Alterar Dados");
        btnAlterar.setBounds(140, 170, 110, 25);
        tela.add(btnAlterar);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(270, 170, 110, 25);
        tela.add(btnExcluir);

        JButton btnSair = new JButton("Sair");
        btnSair.setBounds(395, 170, 110, 25);
        tela.add(btnSair);

        //Abrir a tela de estoque (somente com usuário logado)
        btnEstoque.addActionListener(e -> {
            new TelaProdutos().abreTela();
            dispose();
        });

        btnBuscar.addActionListener(e -> {
            String textoId = txtBusca.getText().trim();
            if (textoId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o ID do produto.",
                        "Atenção", JOptionPane.ERROR_MESSAGE);
                txtBusca.grabFocus();
                return;
            }

            try {
                int id = Integer.parseInt(textoId);
                TelaProdutos telaProdutos = new TelaProdutos();
                telaProdutos.abreTela(id);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "O ID precisa ser um número inteiro.",
                        "Atenção", JOptionPane.ERROR_MESSAGE);
                txtBusca.grabFocus();
            }
        });

        //Encerra a sessão e retorna à tela de login
        btnSair.addActionListener(e -> {
            SessaoUsuario.encerrar();
            new TelaLogin().abreTela();
            dispose();
        });

        //Ir para a tela de alteração
        btnAlterar.addActionListener(e -> {
            new TelaAlteracao().abreTela();
            dispose();
        });

        //Excluir o usuário logado
        btnExcluir.addActionListener(e -> {
            Object[] options = {"Sim", "Não"};
            int opcao = JOptionPane.showOptionDialog(this,
                    SessaoUsuario.getUsuarioAtual().getNome() + ", deseja se excluir?",
                    "Atenção", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

            if (opcao == 0) {
                String usuario = SessaoUsuario.getUsuarioAtual().getUsuario();
                String fotoUsuario = SessaoUsuario.getUsuarioAtual().getFoto();
                TarefasSwing.executar(this, () -> usuarioDAO.exclui(usuario), excluido -> {
                    if (excluido) {
                        ImagemUtil.apagar(fotoUsuario);
                        SessaoUsuario.encerrar();
                        JOptionPane.showMessageDialog(this,
                                "Usuário excluído. Voltando à tela de login.",
                                "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        new TelaLogin().abreTela();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Problemas ao excluir o usuário.",
                                "Atenção", JOptionPane.ERROR_MESSAGE);
                    }
                }, ex -> JOptionPane.showMessageDialog(this,
                        "Não foi possível excluir o usuário: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE));
            }
        });
    }

    public void abreTela() {
        setVisible(true);
    }
}
