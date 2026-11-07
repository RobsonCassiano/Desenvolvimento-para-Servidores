-- Use ESTE script se o banco bd_login JÁ EXISTE (criado com a versão anterior, sem foto nem produtos).
use bd_login;

-- Rode apenas uma vez se a coluna foto ainda não existir.
alter table usuario add column foto varchar(255) default '';

-- Torna o nome de usuário case-sensitive e permite contas que diferem por caixa.
alter table usuario
    modify usuario varchar(10) character set utf8mb4 collate utf8mb4_bin not null;

create table if not exists produto(
    id             int auto_increment primary key,
    codigo         char(36)      not null unique,
    tipo           varchar(10)   not null default 'PRODUTO',
    nome           varchar(60)   not null,
    descricao      varchar(200)  default '',
    preco_base     decimal(10,2) not null,
    desconto       decimal(5,2)  not null default 0,
    quantidade     int           not null default 0,
    meses_garantia int           not null default 0,
    imagem         varchar(255)  default ''
);
