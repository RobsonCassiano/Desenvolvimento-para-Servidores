package com.lojavirtual.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Produto {

    public static final String TIPO_PRODUTO = "PRODUTO";
    public static final String TIPO_ELETRONICO = "ELETRONICO";
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal CEM = new BigDecimal("100");
    private static final BigDecimal FRETE_POR_KM = new BigDecimal("0.50");
    private static final BigDecimal TAXA_EXPRESSA = new BigDecimal("20.00");

    private int id;
    private String nome;
    private String descricao = "";
    private BigDecimal precoBase;
    private String codigo;
    private BigDecimal desconto;
    private int quantidade;
    private String imagem = "";

    public Produto(String nome, BigDecimal precoBase) {
        this(nome, precoBase, ZERO);
    }

    public Produto(String nome, BigDecimal precoBase, BigDecimal desconto) {
        setNome(nome);
        setPrecoBase(precoBase);
        setDesconto(desconto);
        codigo = UUID.randomUUID().toString();
    }

    public Produto(String nome, String descricao, BigDecimal precoBase,
                   BigDecimal desconto, int quantidade) {
        this(nome, precoBase, desconto);
        setDescricao(descricao);
        setQuantidade(quantidade);
    }

    public Produto(int id, String codigo, String nome, String descricao, BigDecimal precoBase,
                   BigDecimal desconto, int quantidade, String imagem) {
        this(nome, descricao, precoBase, desconto, quantidade);
        setId(id);
        setCodigo(codigo);
        setImagem(imagem);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("O id nao pode ser negativo.");
        }
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do produto e obrigatorio.");
        }
        if (nome.length() > 60) {
            throw new IllegalArgumentException("O nome pode ter no maximo 60 caracteres.");
        }
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        String valor = descricao == null ? "" : descricao;
        if (valor.length() > 200) {
            throw new IllegalArgumentException("A descricao pode ter no maximo 200 caracteres.");
        }
        this.descricao = valor;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(BigDecimal precoBase) {
        Objects.requireNonNull(precoBase, "O preco base e obrigatorio.");
        if (precoBase.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("O preco deve ser maior que zero.");
        }
        this.precoBase = precoBase;
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

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        Objects.requireNonNull(desconto, "O desconto e obrigatorio.");
        if (desconto.compareTo(ZERO) < 0 || desconto.compareTo(CEM) > 0) {
            throw new IllegalArgumentException("O desconto deve estar entre 0 e 100.");
        }
        this.desconto = desconto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade nao pode ser negativa.");
        }
        this.quantidade = quantidade;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem == null ? "" : imagem;
    }

    public String getTipo() {
        return TIPO_PRODUTO;
    }

    public BigDecimal calcularPrecoFinal() {
        return precoBase.multiply(CEM.subtract(desconto))
                .divide(CEM, 2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal calcularFrete(BigDecimal distancia) {
        Objects.requireNonNull(distancia, "A distancia e obrigatoria.");
        if (distancia.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("A distancia nao pode ser negativa.");
        }
        return distancia.multiply(FRETE_POR_KM).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal calcularFrete(BigDecimal distancia, boolean expresso) {
        BigDecimal frete = calcularFrete(distancia);
        return expresso ? frete.add(TAXA_EXPRESSA) : frete;
    }
}
