package com.lojavirtual.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexao {

    private static final String DEFAULT_URL = "jdbc:mysql://127.0.0.1:3306/bd_login";

    private Conexao() {
    }

    public static Connection abrirConexao() throws SQLException {
        String url = setting("db.url", "DB_URL", DEFAULT_URL);
        String usuario = setting("db.user", "DB_USER", "root");
        String senha = setting("db.password", "DB_PASSWORD", "");
        return DriverManager.getConnection(url, usuario, senha);
    }

    private static String setting(String property, String environmentVariable, String defaultValue) {
        String value = System.getProperty(property);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentVariable);
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
