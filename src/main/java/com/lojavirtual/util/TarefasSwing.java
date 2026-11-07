package com.lojavirtual.util;

import java.awt.Component;
import java.awt.Container;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

public final class TarefasSwing {

    private TarefasSwing() {
    }

    public static <T> void executar(Component componente, Callable<T> tarefa,
                                    Consumer<T> aoConcluir, Consumer<Exception> aoFalhar) {
        Map<Component, Boolean> estadosHabilitacao = new IdentityHashMap<>();
        alterarHabilitacao(componente, false, estadosHabilitacao);

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarefa.call();
            }

            @Override
            protected void done() {
                estadosHabilitacao.forEach(Component::setEnabled);
                try {
                    aoConcluir.accept(get());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    aoFalhar.accept(ex);
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    if (causa instanceof Exception excecao) {
                        aoFalhar.accept(excecao);
                    } else {
                        throw new IllegalStateException("Falha inesperada na tarefa em segundo plano.", causa);
                    }
                }
            }
        }.execute();
    }

    private static void alterarHabilitacao(
            Component componente, boolean habilitado, Map<Component, Boolean> estados) {
        estados.put(componente, componente.isEnabled());
        componente.setEnabled(habilitado);
        if (componente instanceof Container container) {
            for (Component filho : container.getComponents()) {
                alterarHabilitacao(filho, habilitado, estados);
            }
        }
    }
}
