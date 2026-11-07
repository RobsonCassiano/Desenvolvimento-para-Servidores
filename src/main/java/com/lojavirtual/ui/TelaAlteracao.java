package com.lojavirtual.ui;

import com.lojavirtual.model.Usuario;
import com.lojavirtual.repository.UsuarioDAO;
import com.lojavirtual.session.SessaoUsuario;
import com.lojavirtual.util.ImagemUtil;
import com.lojavirtual.util.TarefasSwing;
import com.lojavirtual.util.ValidadorUsuario;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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
 * Tela de alteração dos dados do usuário logado (Update).
 */
public class TelaAlteracao extends JFrame {

    //Criando meus atributos globais
    private final JPanel tela;
    private final JTextField txtNome;
    private final JPasswordField passAtual;
    private final JPasswordField passSenha;
    private final JPasswordField confPassSenha;
    private final JLabel lblFoto;

    //Nova foto escolhida (ainda não copiada para a pasta de imagens)
    private File fotoSelecionada;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public TelaAlteracao() {
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Alteração");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 200, 470, 212);

        tela = new JPanel();
        tela.setBackground(SystemColor.gray);
        setContentPane(tela);
        tela.setLayout(null);

        //Adicionando elementos na tela
        JLabel lblIdentificacao = new JLabel("Informar campos para alteração");
        lblIdentificacao.setBounds(60, 0, 500, 39);
        lblIdentificacao.setFont(new Font("Arial", 3, 19));
        tela.add(lblIdentificacao);

        JLabel lblNome = new JLabel("Nome");
        lblNome.setBounds(24, 35, 100, 15);
        tela.add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(120, 35, 218, 20);
        tela.add(txtNome);
        txtNome.setColumns(10);

        JLabel lblSenhaAtual = new JLabel("Senha Atual");
        lblSenhaAtual.setBounds(24, 60, 70, 15);
        tela.add(lblSenhaAtual);

        passAtual = new JPasswordField();
        passAtual.setBounds(120, 60, 219, 19);
        tela.add(passAtual);

        JLabel lblNovaSenha = new JLabel("Nova Senha");
        lblNovaSenha.setBounds(24, 85, 70, 15);
        tela.add(lblNovaSenha);

        passSenha = new JPasswordField();
        passSenha.setBounds(120, 85, 219, 19);
        tela.add(passSenha);

        JLabel lblConfSenha = new JLabel("Confirmar Senha");
        lblConfSenha.setBounds(24, 110, 100, 15);
        tela.add(lblConfSenha);

        confPassSenha = new JPasswordField();
        confPassSenha.setBounds(120, 110, 219, 19);
        tela.add(confPassSenha);

        JButton btnAlterar = new JButton("Alterar");
        btnAlterar.setBounds(200, 136, 117, 25);
        tela.add(btnAlterar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(50, 136, 117, 25);
        tela.add(btnCancelar);

        //Foto do usuário (mostra a atual e permite trocar)
        lblFoto = new JLabel("Sem foto", SwingConstants.CENTER);
        lblFoto.setBounds(350, 40, 90, 90);
        lblFoto.setOpaque(true);
        lblFoto.setBackground(Color.LIGHT_GRAY);
        lblFoto.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ImagemUtil.mostrar(lblFoto,
                ImagemUtil.carregar(SessaoUsuario.getUsuarioAtual().getFoto(), 90, 90), "Sem foto");
        tela.add(lblFoto);

        JButton btnFoto = new JButton("Trocar foto");
        btnFoto.setBounds(340, 136, 110, 25);
        tela.add(btnFoto);

        btnFoto.addActionListener(e -> {
            File escolhida = ImagemUtil.escolher(this);
            if (escolhida != null) {
                fotoSelecionada = escolhida;
                ImagemUtil.mostrar(lblFoto, ImagemUtil.carregar(escolhida, 90, 90), "Sem foto");
            }
        });

        //Cancelar: volta para a tela inicial
        btnCancelar.addActionListener(e -> {
            TelaInicio telaIni = new TelaInicio();
            telaIni.setVisible(true);
            dispose();
        });

        //Botão de alteração
        btnAlterar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String novaSenha = new String(passSenha.getPassword());
            Usuario usuarioAtual = SessaoUsuario.getUsuarioAtual();
            String senhaAtual = new String(passAtual.getPassword());
            String confirmacao = new String(confPassSenha.getPassword());

            var erroValidacao = ValidadorUsuario.validarAlteracao(nome, novaSenha, confirmacao);
            if (erroValidacao.isPresent()) {
                JOptionPane.showMessageDialog(this, erroValidacao.get(), "Atenção", JOptionPane.ERROR_MESSAGE);
                return;
            }

            File foto = fotoSelecionada;
            TarefasSwing.executar(this,
                    () -> atualizarUsuario(usuarioAtual, nome, novaSenha, senhaAtual, foto),
                    resultado -> {
                        if (!resultado.atualizado()) {
                            JOptionPane.showMessageDialog(this,
                                    resultado.senhaValida() ? "Problemas ao atualizar o usuário"
                                            : "Senha inválida, verifique!",
                                    "Atenção", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (!resultado.fotoNova().equals(resultado.fotoAntiga())) {
                            ImagemUtil.apagar(resultado.fotoAntiga());
                        }
                        SessaoUsuario.encerrar();
                        JOptionPane.showMessageDialog(this,
                                "Dados alterados. Faça login novamente.",
                                "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        new TelaLogin().abreTela();
                        dispose();
                    },
                    ex -> JOptionPane.showMessageDialog(this,
                            "Não foi possível atualizar os dados: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE));
        });

        //Atribuir o atributo global ao objeto
        txtNome.setText(SessaoUsuario.getUsuarioAtual().getNome());
    }

    public void abreTela() {
        setVisible(true);
    }

    private ResultadoAlteracao atualizarUsuario(
            Usuario usuario, String nome, String novaSenha, String senhaAtual, File foto)
            throws IOException, SQLException {
        if (!usuarioDAO.senhaConfere(usuario.getUsuario(), senhaAtual)) {
            return new ResultadoAlteracao(false, false, usuario.getFoto(), usuario.getFoto());
        }

        String fotoNova = usuario.getFoto();
        try {
            if (foto != null) {
                fotoNova = ImagemUtil.salvar(foto, "usuarios");
            }
            boolean atualizado = usuarioDAO.atualiza(nome, usuario.getUsuario(), novaSenha, fotoNova);
            if (!atualizado && !fotoNova.equals(usuario.getFoto())) {
                ImagemUtil.apagar(fotoNova);
            }
            return new ResultadoAlteracao(true, atualizado, usuario.getFoto(), fotoNova);
        } catch (IOException | SQLException ex) {
            if (!fotoNova.equals(usuario.getFoto())) {
                ImagemUtil.apagar(fotoNova);
            }
            throw ex;
        }
    }

    private record ResultadoAlteracao(
            boolean senhaValida, boolean atualizado, String fotoAntiga, String fotoNova) {
    }
}
