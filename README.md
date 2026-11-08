# Sistema de Loja Virtual - P2 - Tela de Login e Banco de Dados

Aplicação desktop Java Swing para gerenciamento de usuários e estoque de produtos. O projeto também demonstra conceitos de Programação Orientada a Objetos:

- encapsulamento com atributos privados, construtores, getters e setters;
- herança com `Eletronico extends Produto`;
- sobrescrita de `calcularPrecoFinal()`;
- sobrecarga de `calcularFrete()`;
- persistência em MySQL via JDBC.

## Regras e demonstração de POO

O preço final de um eletrônico é calculado aplicando primeiro o desconto ao preço base e, em seguida, somando R$ 10,00 por mês de garantia:

```text
preço final = preço base × (1 − desconto / 100) + meses de garantia × R$ 10,00
```

Por exemplo, um smartphone de R$ 2.200,00 com 15% de desconto e 12 meses de garantia custa R$ 1.990,00: R$ 1.870,00 após o desconto, mais R$ 120,00 da garantia. A garantia pode ser de zero meses, mas não pode ser negativa.

A classe `DemoPoo` também demonstra a alteração de atributos por setters (nome, preço, desconto, estoque e meses de garantia) e o tratamento de dados inválidos. Os setters rejeitam valores fora das regras do domínio, como desconto acima de 100% ou garantia negativa, lançando `IllegalArgumentException`; a demonstração captura a exceção e continua executando.

## Requisitos

- JDK 17 ou superior;
- Maven 3.8 ou superior;
- XAMPP com MySQL/MariaDB iniciado (ou outro servidor MySQL compatível).

## Configuração do banco de dados com XAMPP

### 1. Iniciar o servidor

1. Abra o **XAMPP Control Panel**.
2. Inicie o módulo **MySQL**. O Apache só é necessário se for usar o phpMyAdmin.
3. Confirme que o MySQL está ouvindo na porta `3306`, que é a porta usada pela configuração padrão da aplicação.

### 2. Criar o banco e as tabelas

Para uma instalação nova:

1. Abra o phpMyAdmin em `http://localhost/phpmyadmin` ou conecte-se ao MySQL por outro cliente.
2. Importe ou execute o arquivo `src/main/resources/db/schema.sql`.
3. O script cria o banco `bd_login` e as tabelas `usuario` e `produto`.

Se estiver atualizando um banco da versão antiga:

1. Faça um backup do banco antes de alterar sua estrutura.
2. Execute `src/main/resources/db/migration-from-legacy.sql` uma única vez.
3. O script adiciona a coluna `foto` à tabela `usuario`. Se ela já existir, remova ou comente a instrução `ALTER TABLE usuario ADD COLUMN foto ...` antes de executar.
4. A migração altera a comparação do nome de usuário para ser sensível a maiúsculas e minúsculas e cria a tabela `produto`, caso ainda não exista.

### 3. Configurar a conexão JDBC

A aplicação usa `jdbc:mysql://127.0.0.1:3306/bd_login`. Os valores padrão são:

| Configuração | Valor padrão |
|---|---|
| URL JDBC | `jdbc:mysql://127.0.0.1:3306/bd_login` |
| Usuário | `root` |
| Senha | vazia |

Se o MySQL do seu XAMPP usar outra porta, banco, usuário ou senha, configure as variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD`. No PowerShell:

```powershell
$env:DB_URL = "jdbc:mysql://127.0.0.1:3306/bd_login"
$env:DB_USER = "root"
$env:DB_PASSWORD = "sua-senha"
mvn exec:java
```

As variáveis definidas dessa forma valem para a janela atual do PowerShell. Alternativamente, é possível passar propriedades da JVM, por exemplo:

```powershell
mvn exec:java "-Ddb.url=jdbc:mysql://127.0.0.1:3306/bd_login" "-Ddb.user=root" "-Ddb.password=sua-senha"
```

As propriedades da JVM têm precedência sobre as variáveis de ambiente. Não grave credenciais reais no código-fonte nem as envie ao repositório.

### Imagens e backup dos dados

As imagens selecionadas não são armazenadas dentro do MySQL. Os arquivos ficam em `imagens/usuarios` ou `imagens/produtos`, e o banco guarda o caminho relativo correspondente. Para preservar os dados ao transferir ou restaurar o projeto, faça backup tanto do banco `bd_login` quanto da pasta `imagens`.

## Compilar, testar e executar

Execute os comandos na raiz do projeto:

```bash
mvn clean test
mvn exec:java
```

Para testar a conexão com o MySQL sem abrir a aplicação:

```bash
mvn exec:java "-Dexec.mainClass=com.lojavirtual.app.TestaConexao"
```

Para executar a demonstração de POO no console:

```bash
mvn exec:java "-Dexec.mainClass=com.lojavirtual.demo.DemoPoo"
```

## Estrutura do projeto

```text
.
├── imagens/
│   ├── produtos/                 # Imagens dos produtos (geradas/selecionadas em execução)
│   └── usuarios/                 # Fotos dos usuários (geradas/selecionadas em execução)
├── src/
│   ├── main/
│   │   ├── java/com/lojavirtual/
│   │   │   ├── app/              # Entrada da aplicação e teste de conexão
│   │   │   ├── data/             # Conexão JDBC
│   │   │   ├── demo/             # Demonstração de conceitos de POO
│   │   │   ├── model/            # Entidades e regras de domínio
│   │   │   ├── repository/       # DAOs e operações SQL
│   │   │   ├── session/          # Estado da sessão do usuário
│   │   │   ├── ui/               # Telas Swing
│   │   │   └── util/             # Validação, tarefas Swing e imagens
│   │   └── resources/db/
│   │       ├── schema.sql                 # Criação do banco e tabelas
│   │       └── migration-from-legacy.sql  # Migração de banco existente
│   └── test/java/com/lojavirtual/
│       └── model/                # Testes automatizados do domínio
├── .gitignore
├── pom.xml                       # Dependências e configuração Maven
└── README.md
```
