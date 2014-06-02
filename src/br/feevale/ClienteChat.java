package br.feevale;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Cliente de chat, usado para conectar à um servidor
 * @author diovani
 *
 */
public class ClienteChat {

    private Socket socket;

    private MensagemJson clienteMensagem;

    public ClienteChat(String server, Integer porta) throws UnknownHostException, IOException {
        this.socket = new Socket(server, porta);

        //Teste: Por hora, o client apenas envia mensagens
        this.clienteMensagem = new MensagemJson(this.socket);
    }

    public void login(String nomeUsuario) {
        this.clienteMensagem.enviaHandshake(nomeUsuario);
    }

    public void mensagem(String mensagem) {
        this.clienteMensagem.enviaMensagem(mensagem);
    }

    public Socket getSocket() {
        return this.socket;
    }
}
