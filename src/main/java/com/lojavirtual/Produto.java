package com.lojavirtual;

import java.util.UUID;

public class Produto {
    private String nome;
    private double precoBase;
    private String codigo;
    private double desconto;

    public Produto(String nome, double precoBase) {
        this(nome, precoBase, 0);
    }

    public Produto(String nome, double precoBase, double desconto) {
        setNome(nome);
        setPrecoBase(precoBase);
        setDesconto(desconto);
        this.codigo = UUID.randomUUID().toString();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do produto e obrigatorio.");
        }
        this.nome = nome;
    }

    public double getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(double preco) {
        if (preco <= 0) {
            throw new IllegalArgumentException("O preco deve ser maior que zero.");
        }
        this.precoBase = preco;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("O codigo do produto e obrigatorio.");
        }
        this.codigo = codigo;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        if (desconto < 0 || desconto > 100) {
            throw new IllegalArgumentException("O desconto deve estar entre 0 e 100.");
        }
        this.desconto = desconto;
    }

    public double calcularPrecoFinal() {
        return precoBase * (1 - desconto / 100);
    }

    public double calcularFrete(double distancia) {
        validarDistancia(distancia);
        return distancia * 0.50;
    }

    public double calcularFrete(double distancia, boolean isExpress) {
        double frete = calcularFrete(distancia);
        return isExpress ? frete + 20 : frete;
    }

    private void validarDistancia(double distancia) {
        if (distancia < 0) {
            throw new IllegalArgumentException("A distancia nao pode ser negativa.");
        }
    }
}
