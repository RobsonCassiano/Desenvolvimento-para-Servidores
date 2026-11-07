package com.lojavirtual.model;

public class Usuario {

    private String usuario;
    private String nome;
    private String foto = "";

    public Usuario(String usuario, String nome, String foto) {
        this.usuario = usuario;
        this.nome = nome;
        setFoto(foto);
    }

    public String getUsuario() {
        return usuario;
    }

    public String getNome() {
        return nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto == null ? "" : foto;
    }
}
