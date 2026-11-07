#CRIAÇÃO DO BANCO DE DADOS BD_LOGIN
create database if not exists bd_login;

#SELECIONANDO O BANCO DE DADOS
use bd_login;

#CRIANDO A TABELA DE LOGIN (foto = caminho relativo da imagem do usuário)
create table if not exists usuario(
    usuario varchar(10) character set utf8mb4 collate utf8mb4_bin not null primary key,
    nome    varchar(30) default '',
    senha   char(32)    default '',
    foto    varchar(255) default ''
);

#CRIANDO A TABELA DE PRODUTOS (tipo: PRODUTO ou ELETRONICO)
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
