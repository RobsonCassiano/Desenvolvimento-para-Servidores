package com.lojavirtual.demo;

import com.lojavirtual.model.Eletronico;
import com.lojavirtual.model.Produto;
import java.math.BigDecimal;

public class DemoPoo {
    public static void main(String[] args) {
        Produto livro = new Produto("Livro de Java", new BigDecimal("80.00"));
        Produto mochila = new Produto("Mochila", new BigDecimal("150.00"), new BigDecimal("10"));
        Eletronico notebook = new Eletronico("Notebook", new BigDecimal("3500.00"), 5);
        Eletronico smartphone = new Eletronico("Smartphone", new BigDecimal("2200.00"),
                new BigDecimal("15"), 12);

        System.out.printf("%s - preco final: R$ %s%n", livro.getNome(), livro.calcularPrecoFinal());
        System.out.printf("%s - preco final com desconto: R$ %s%n",
                mochila.getNome(), mochila.calcularPrecoFinal());
        System.out.printf("%s - preco final com garantia: R$ %s%n",
                notebook.getNome(), notebook.calcularPrecoFinal());
        System.out.printf("%s - preco final com desconto e garantia: R$ %s%n",
                smartphone.getNome(), smartphone.calcularPrecoFinal());

        System.out.printf("Frete normal: R$ %s%n",
                livro.calcularFrete(new BigDecimal("20")));
        System.out.printf("Frete express: R$ %s%n",
                livro.calcularFrete(new BigDecimal("20"), true));
    }
}
