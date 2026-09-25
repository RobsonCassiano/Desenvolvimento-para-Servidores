package com.lojavirtual;

public class Eletronico extends Produto {
    private int mesesGarantia;

    public Eletronico(String nome, double precoBase, int mesesGarantia) {
        super(nome, precoBase);
        setMesesGarantia(mesesGarantia);
    }

    public Eletronico(String nome, double precoBase, double desconto, int mesesGarantia) {
        super(nome, precoBase, desconto);
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
    public double calcularPrecoFinal() {
        return super.calcularPrecoFinal() + (mesesGarantia * 10);
    }
}
