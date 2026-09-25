# Sistema de Loja Virtual

Projeto Java para demonstrar os fundamentos de Programacao Orientada a Objetos:

- classe e atributos privados;
- construtores e sobrecarga de construtor;
- encapsulamento com getters e setters;
- heranca com `Eletronico extends Produto`;
- reescrita de `calcularPrecoFinal()`;
- sobrecarga de `calcularFrete()`.

## Estrutura

```text
src/
└── main/
    └── java/
        └── com/lojavirtual/
            ├── Eletronico.java
            ├── Main.java
            └── Produto.java
```

## Executar com Maven

```bash
mvn compile
mvn exec:java -Dexec.mainClass=com.lojavirtual.Main
```

## Executar sem Maven

```bash
javac -d target/classes src/main/java/com/lojavirtual/*.java
java -cp target/classes com.lojavirtual.Main
```
