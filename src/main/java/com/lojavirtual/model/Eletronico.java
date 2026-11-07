package com.lojavirtual.model;

import java.math.BigDecimal;

public class Eletronico extends Produto {

    private int mesesGarantia;

    public Eletronico(String nome, BigDecimal precoBase, int mesesGarantia) {
        super(nome, precoBase);
        setMesesGarantia(mesesGarantia);
    }

    public Eletronico(String nome, BigDecimal precoBase, BigDecimal desconto, int mesesGarantia) {
        super(nome, precoBase, desconto);
        setMesesGarantia(mesesGarantia);
    }

    public Eletronico(String nome, String descricao, BigDecimal precoBase, BigDecimal desconto,
                      int quantidade, int mesesGarantia) {
        super(nome, descricao, precoBase, desconto, quantidade);
        setMesesGarantia(mesesGarantia);
    }

    public Eletronico(int id, String codigo, String nome, String descricao, BigDecimal precoBase,
                      BigDecimal desconto, int quantidade, String imagem, int mesesGarantia) {
        super(id, codigo, nome, descricao, precoBase, desconto, quantidade, imagem);
        setMesesGarantia(mesesGarantia);
    }

    public int getMesesGarantia() {
        return mesesGarantia;
    }

    public void setMesesGarantia(int mesesGarantia) {
        if (mesesGarantia < 0) {
            throw new IllegalArgumentException("Os meses de garantia nao podem ser negativos.");
        }
        this.mesesGarantia = mesesGarantia;
    }

    @Override
    public String getTipo() {
        return TIPO_ELETRONICO;
    }

    @Override
    public BigDecimal calcularPrecoFinal() {
        return super.calcularPrecoFinal().add(BigDecimal.valueOf(mesesGarantia)
                .multiply(new BigDecimal("10.00")));
    }
}
