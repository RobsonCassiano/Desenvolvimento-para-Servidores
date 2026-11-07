package com.lojavirtual.repository;

import com.lojavirtual.data.Conexao;
import com.lojavirtual.model.Eletronico;
import com.lojavirtual.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public boolean cadastra(Produto produto) throws SQLException {
        String sql = "INSERT INTO produto (codigo, tipo, nome, descricao, preco_base, desconto, "
                + "quantidade, meses_garantia, imagem) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement statement = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(statement, produto);
            statement.executeUpdate();

            try (ResultSet chaves = statement.getGeneratedKeys()) {
                if (chaves.next()) {
                    produto.setId(chaves.getInt(1));
                }
            }
            return true;
        }
    }

    public List<Produto> listaTodos() throws SQLException {
        String sql = "SELECT * FROM produto ORDER BY id";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement statement = conexao.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                produtos.add(mapear(resultado));
            }
        }
        return produtos;
    }

    public Produto buscaPorId(int id) throws SQLException {
        String sql = "SELECT * FROM produto WHERE id = ?";
        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next() ? mapear(resultado) : null;
            }
        }
    }

    public boolean atualiza(Produto produto) throws SQLException {
        String sql = "UPDATE produto SET codigo = ?, tipo = ?, nome = ?, descricao = ?, "
                + "preco_base = ?, desconto = ?, quantidade = ?, meses_garantia = ?, imagem = ? "
                + "WHERE id = ?";
        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            preencher(statement, produto);
            statement.setInt(10, produto.getId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean exclui(int id) throws SQLException {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private void preencher(PreparedStatement statement, Produto produto) throws SQLException {
        int mesesGarantia = produto instanceof Eletronico
                ? ((Eletronico) produto).getMesesGarantia() : 0;

        statement.setString(1, produto.getCodigo());
        statement.setString(2, produto.getTipo());
        statement.setString(3, produto.getNome());
        statement.setString(4, produto.getDescricao());
        statement.setBigDecimal(5, produto.getPrecoBase());
        statement.setBigDecimal(6, produto.getDesconto());
        statement.setInt(7, produto.getQuantidade());
        statement.setInt(8, mesesGarantia);
        statement.setString(9, produto.getImagem());
    }

    private Produto mapear(ResultSet resultado) throws SQLException {
        int id = resultado.getInt("id");
        String codigo = resultado.getString("codigo");
        String nome = resultado.getString("nome");
        String descricao = resultado.getString("descricao");
        BigDecimal preco = resultado.getBigDecimal("preco_base");
        BigDecimal desconto = resultado.getBigDecimal("desconto");
        int quantidade = resultado.getInt("quantidade");
        String imagem = resultado.getString("imagem");

        if (Produto.TIPO_ELETRONICO.equals(resultado.getString("tipo"))) {
            return new Eletronico(id, codigo, nome, descricao, preco, desconto, quantidade,
                    imagem, resultado.getInt("meses_garantia"));
        }
        return new Produto(id, codigo, nome, descricao, preco, desconto, quantidade, imagem);
    }
}
