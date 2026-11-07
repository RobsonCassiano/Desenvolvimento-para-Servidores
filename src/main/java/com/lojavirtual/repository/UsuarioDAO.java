package com.lojavirtual.repository;

import com.lojavirtual.data.Conexao;
import com.lojavirtual.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario autenticar(String usuario, String senha) throws SQLException {
        String sql = "SELECT usuario, nome, foto FROM usuario "
                + "WHERE BINARY usuario = BINARY ? AND senha = md5(?)";
        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, usuario);
            statement.setString(2, senha);
            try (ResultSet resultado = statement.executeQuery()) {
                if (!resultado.next()) {
                    return null;
                }
                return new Usuario(resultado.getString("usuario"),
                        resultado.getString("nome"), resultado.getString("foto"));
            }
        }
    }

    public boolean existe(String usuario) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE BINARY usuario = BINARY ?";
        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, usuario);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean cadastra(String nome, String usuario, String senha, String foto) throws SQLException {
        String sql = "INSERT INTO usuario (nome, usuario, senha, foto) VALUES (?, ?, md5(?), ?)";
        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setString(2, usuario);
            statement.setString(3, senha);
            statement.setString(4, foto == null ? "" : foto);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean atualiza(String nome, String usuario, String senha, String foto) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, senha = md5(?), foto = ? "
                + "WHERE BINARY usuario = BINARY ?";
        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setString(2, senha);
            statement.setString(3, foto == null ? "" : foto);
            statement.setString(4, usuario);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean exclui(String usuario) throws SQLException {
        String sql = "DELETE FROM usuario WHERE BINARY usuario = BINARY ?";
        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, usuario);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean senhaConfere(String usuario, String senha) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE BINARY usuario = BINARY ? AND senha = md5(?)";
        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, usuario);
            statement.setString(2, senha);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next();
            }
        }
    }
}
