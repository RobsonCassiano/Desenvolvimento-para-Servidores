package com.lojavirtual.session;

import com.lojavirtual.model.Usuario;

public final class SessaoUsuario {

    private static Usuario usuarioAtual;

    private SessaoUsuario() {
    }

    public static Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    public static void iniciar(Usuario usuario) {
        usuarioAtual = usuario;
    }

    public static void encerrar() {
        usuarioAtual = null;
    }
}
