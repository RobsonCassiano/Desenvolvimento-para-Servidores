package com.lojavirtual.app;

import com.lojavirtual.data.Conexao;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Teste para verificarmos se o banco conecta e desconecta.
 */
public class TestaConexao {

    public static void main(String[] args) {
        try (Connection ignored = Conexao.abrirConexao()) {
            System.out.println("Conexão com o banco aberta com sucesso.");
        } catch (SQLException ex) {
            System.err.println("Não foi possível conectar ao banco: " + ex.getMessage());
        }
    }
}
