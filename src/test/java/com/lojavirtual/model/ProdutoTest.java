package com.lojavirtual.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProdutoTest {

    @Test
    void calculaPrecoFinalComDesconto() {
        Produto produto = new Produto("Mochila", new BigDecimal("150.00"), new BigDecimal("10"));

        assertEquals(new BigDecimal("135.00"), produto.calcularPrecoFinal());
    }

    @Test
    void incluiGarantiaNoPrecoFinalDoEletronico() {
        Produto produto = new Eletronico("Notebook", new BigDecimal("3500.00"), 5);

        assertEquals(new BigDecimal("3550.00"), produto.calcularPrecoFinal());
    }

    @Test
    void rejeitaDescontoForaDoIntervaloPermitido() {
        assertThrows(IllegalArgumentException.class,
                () -> new Produto("Mochila", new BigDecimal("150.00"), new BigDecimal("101")));
    }

    @Test
    void calculaFreteNormalEExpressoComPrecisaoMonetaria() {
        Produto produto = new Produto("Mochila", new BigDecimal("150.00"));

        assertEquals(new BigDecimal("5.00"), produto.calcularFrete(new BigDecimal("10")));
        assertEquals(new BigDecimal("25.00"), produto.calcularFrete(new BigDecimal("10"), true));
    }

    @Test
    void arredondaFreteParaCentavos() {
        Produto produto = new Produto("Mochila", new BigDecimal("150.00"));

        assertEquals(new BigDecimal("0.01"), produto.calcularFrete(new BigDecimal("0.01")));
    }

    @Test
    void rejeitaDistanciaNegativaNoFrete() {
        Produto produto = new Produto("Mochila", new BigDecimal("150.00"));

        assertThrows(IllegalArgumentException.class,
                () -> produto.calcularFrete(new BigDecimal("-1")));
    }
}
