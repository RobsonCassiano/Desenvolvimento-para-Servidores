package com.lojavirtual.util;

import java.util.Optional;

public final class ValidadorUsuario {

    private ValidadorUsuario() {
    }

    public static Optional<String> validarCadastro(
            String nome, String usuario, String senha, String confirmacao) {
        Optional<String> dadosInvalidos = validarNomeEUsuario(nome, usuario);
        if (dadosInvalidos.isPresent()) {
            return dadosInvalidos;
        }
        if (senha == null || senha.isEmpty()) {
            return Optional.of("A senha precisa ser informada!");
        }
        if (!senha.equals(confirmacao)) {
            return Optional.of("Campos senha e confirmação de senha não coincidem!");
        }
        return Optional.empty();
    }

    public static Optional<String> validarAlteracao(String nome, String novaSenha, String confirmacao) {
        if (nome == null || nome.isBlank()) {
            return Optional.of("Campo nome do usuário precisa ser informado!");
        }
        if (nome.length() > 30) {
            return Optional.of("O nome pode ter no máximo 30 caracteres!");
        }
        if (novaSenha == null || novaSenha.isEmpty()) {
            return Optional.of("A senha precisa ser informada!");
        }
        if (!novaSenha.equals(confirmacao)) {
            return Optional.of("Campos de Senha e Confirmação não são iguais!");
        }
        return Optional.empty();
    }

    private static Optional<String> validarNomeEUsuario(String nome, String usuario) {
        if (nome == null || nome.isBlank()) {
            return Optional.of("Campo nome do usuário precisa ser informado!");
        }
        if (nome.length() > 30) {
            return Optional.of("O nome pode ter no máximo 30 caracteres!");
        }
        if (usuario == null || usuario.isBlank()) {
            return Optional.of("Campo usuário precisa ser informado!");
        }
        if (usuario.length() > 10) {
            return Optional.of("O usuário pode ter no máximo 10 caracteres!");
        }
        return Optional.empty();
    }
}
