package br.feevale;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor de chat responsável por ouvir conexões e guardar mensagens
 * @author diovani
 *
 */
public class ServidorChat extends Thread {

    /**
     * Instancia singleton da classe
     */
    private static ServidorChat instance;

    private Integer porta;

    private Socket client;

    private ServerSocket server;

    /**
     * Construtor privado para garantir o uso como singleton
     */
    private ServidorChat() {
        //empty
    }

    /**
     * Obtém a instancia singleton do classe ou a cria
     * @return ServidorChat
     */
    public static ServidorChat getInstance() {
        if (instance == null) {
            instance = new ServidorChat();
        }

        return instance;
    }

    /**
     * Inicia a thread para escutar a porta
     * @param int porta
     */
    public void inicia(int porta) {
        this.porta = porta;

        try {
            server = new ServerSocket(this.porta);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.start();
    }

    /**
     * Finaliza a thread a fecha o servidor
     * @throws IOException
     */
    public void finaliza() throws IOException {
        this.server.close();
        this.interrupt();
        instance = null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.client = server.accept();

                //Teste: Por hora, o server apenas recebe mensagems
                MonitorChatConsole monitor = new MonitorChatConsole(this.client);
                monitor.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getClient() {
        return this.client;
    }
}
