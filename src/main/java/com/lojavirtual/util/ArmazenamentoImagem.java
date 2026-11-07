package com.lojavirtual.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public final class ArmazenamentoImagem {

    private static final Path DIRETORIO_BASE = Path.of("imagens").toAbsolutePath().normalize();

    private ArmazenamentoImagem() {
    }

    public static String salvar(File origem, String subpasta) throws IOException {
        if (!"usuarios".equals(subpasta) && !"produtos".equals(subpasta)) {
            throw new IllegalArgumentException("Subpasta de imagens inválida.");
        }

        Path pastaDestino = DIRETORIO_BASE.resolve(subpasta).normalize();
        if (!pastaDestino.startsWith(DIRETORIO_BASE)) {
            throw new IllegalArgumentException("Diretório de imagens inválido.");
        }
        Files.createDirectories(pastaDestino);

        String nome = origem.getName();
        int ponto = nome.lastIndexOf('.');
        String extensao = ponto >= 0 ? nome.substring(ponto).toLowerCase() : ".png";
        String novoNome = UUID.randomUUID() + extensao;
        Files.copy(origem.toPath(), pastaDestino.resolve(novoNome), StandardCopyOption.COPY_ATTRIBUTES);

        return "imagens/" + subpasta + "/" + novoNome;
    }

    public static void apagar(String caminho) throws IOException {
        if (caminho == null || caminho.isBlank()) {
            return;
        }

        Path alvo = Path.of(caminho).toAbsolutePath().normalize();
        if (!alvo.startsWith(DIRETORIO_BASE) || alvo.equals(DIRETORIO_BASE)) {
            throw new IllegalArgumentException("O caminho da imagem está fora do diretório permitido.");
        }
        Files.deleteIfExists(alvo);
    }

    public static File localizar(String caminho) {
        if (caminho == null || caminho.isBlank()) {
            return null;
        }

        Path alvo = Path.of(caminho).toAbsolutePath().normalize();
        return alvo.startsWith(DIRETORIO_BASE) && Files.isRegularFile(alvo)
                ? alvo.toFile() : null;
    }
}
