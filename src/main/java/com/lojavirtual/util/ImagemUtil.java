package com.lojavirtual.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Upload e exibição de imagens (fotos de usuários e imagens de produtos).
 * O arquivo escolhido é COPIADO para a pasta "imagens/<subpasta>/" do projeto
 * com um nome único, e somente o caminho relativo é gravado no banco.
 */
public class ImagemUtil {
    private static final long TAMANHO_MAXIMO = 5L * 1024 * 1024; //5 MB

    //Abre o seletor de arquivos e valida a imagem escolhida (retorna null se cancelar/inválida)
    public static File escolher(Component pai) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Escolha uma imagem");
        fc.setFileFilter(new FileNameExtensionFilter("Imagens (png, jpg, jpeg, gif)",
                "png", "jpg", "jpeg", "gif"));

        if (fc.showOpenDialog(pai) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File arquivo = fc.getSelectedFile();

        if (arquivo.length() > TAMANHO_MAXIMO) {
            JOptionPane.showMessageDialog(pai, "A imagem deve ter no máximo 5 MB.",
                    "Atenção", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try {
            if (ImageIO.read(arquivo) == null) {
                JOptionPane.showMessageDialog(pai, "O arquivo escolhido não é uma imagem válida.",
                        "Atenção", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(pai, "Não foi possível ler a imagem.",
                    "Atenção", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return arquivo;
    }

    public static String salvar(File origem, String subpasta) throws IOException {
        return ArmazenamentoImagem.salvar(origem, subpasta);
    }

    public static void apagar(String caminho) {
        try {
            ArmazenamentoImagem.apagar(caminho);
        } catch (IOException ex) {
            System.err.println("Não foi possível remover a imagem: " + ex.getMessage());
        }
    }

    //Carrega a imagem (arquivo) redimensionada mantendo a proporção
    public static ImageIcon carregar(File arquivo, int largura, int altura) {
        try {
            BufferedImage img = ImageIO.read(arquivo);
            if (img == null) {
                return null;
            }
            double escala = Math.min((double) largura / img.getWidth(),
                    (double) altura / img.getHeight());
            int w = Math.max(1, (int) (img.getWidth() * escala));
            int h = Math.max(1, (int) (img.getHeight() * escala));
            return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            System.err.println("Não foi possível carregar a imagem: " + e.getMessage());
            return null;
        }
    }

    //Carrega a imagem a partir do caminho relativo gravado no banco
    public static ImageIcon carregar(String caminho, int largura, int altura) {
        if (caminho == null || caminho.isEmpty()) {
            return null;
        }
        File arquivo = ArmazenamentoImagem.localizar(caminho);
        return arquivo == null ? null : carregar(arquivo, largura, altura);
    }

    //Mostra o ícone no JLabel (ou o texto padrão quando não há imagem)
    public static void mostrar(JLabel label, ImageIcon icone, String textoSemImagem) {
        label.setIcon(icone);
        label.setText(icone == null ? textoSemImagem : "");
    }
}
