package br.feevale;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Cliente de chat, usado para conectar à um servidor
 * @author Paulo Diovani Gonçalves <paulo@diovani.com>
 *
 */
public class ClienteChat {

    private Socket socket;

    public ClienteChat(String server, Integer porta) throws UnknownHostException, IOException {
        this.socket = new Socket(server, porta);
    }

    public Socket getSocket() {
        return this.socket;
    }
}
