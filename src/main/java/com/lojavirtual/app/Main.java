package com.lojavirtual.app;

import com.lojavirtual.ui.TelaLogin;
import javax.swing.SwingUtilities;

/**
 * Ponto de entrada do sistema: inicia pela tela de login.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
