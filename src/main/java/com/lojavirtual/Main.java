package com.lojavirtual;

public class Main {
    public static void main(String[] args) {
        Produto livro = new Produto("Livro de Java", 80.00);
        Produto mochila = new Produto("Mochila", 150.00, 10);
        Eletronico notebook = new Eletronico("Notebook", 3500.00, 5);
        Eletronico smartphone = new Eletronico("Smartphone", 2200.00, 15, 12);

        System.out.printf("%s - preco final: R$ %.2f%n", livro.getNome(), livro.calcularPrecoFinal());
        System.out.printf("%s - preco final com desconto: R$ %.2f%n",
                mochila.getNome(), mochila.calcularPrecoFinal());
        System.out.printf("%s - preco final com garantia: R$ %.2f%n",
                notebook.getNome(), notebook.calcularPrecoFinal());
        System.out.printf("%s - preco final com desconto e garantia: R$ %.2f%n",
                smartphone.getNome(), smartphone.calcularPrecoFinal());

        System.out.printf("Frete normal: R$ %.2f%n", livro.calcularFrete(20));
        System.out.printf("Frete express: R$ %.2f%n", livro.calcularFrete(20, true));
    }
}
