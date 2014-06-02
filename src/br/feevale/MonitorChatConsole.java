package br.feevale;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.Socket;

import org.json.JSONObject;

public class MonitorChatConsole extends Thread implements IMonitorChat {

    private Socket socket;

    private String outroNome;

    public MonitorChatConsole(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream is = this.socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);

            while(true) {
                String mensagem = dis.readUTF();
                JSONObject json = new JSONObject(mensagem);

                int tpTransacao = json.getInt("tpTransacao");

                switch( tpTransacao ) {

                    case IMensagemChat.TipoMensagem.HANDSHAKE:
                        this.trataHandshake(json);
                        break;
                    case IMensagemChat.TipoMensagem.MENSAGEM:
                        this.trataMensagem(json);
                        break;
                }

                sleep(50);
            }
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void trataHandshake(JSONObject data) {
        this.outroNome = data.getString("nome");
        System.out.println("Handshake recebido. Nome: " + this.outroNome);
    }

    @Override
    public void trataMensagem(JSONObject data) {
        String mensagem = data.getString("mensagem");
        System.out.println(this.outroNome + ": " + mensagem);
    }

    @Override
    public void trataAtencao(JSONObject data) {
        // TODO Auto-generated method stub
    }

    @Override
    public void trataDesconexao(JSONObject data) {
        // TODO Auto-generated method stub
    }

    @Override
    public void trataSolicitacaoEnvioArquivo(JSONObject data) {
        // TODO Auto-generated method stub
    }

    @Override
    public void trataAceiteEnvioArquivo(JSONObject data) {
        // TODO Auto-generated method stub
    }

    @Override
    public void trataSolicitacaoEnvioFoto(JSONObject data) {
        // TODO Auto-generated method stub
    }

    @Override
    public void trataAceiteEnvioFoto(JSONObject data) {
        // TODO Auto-generated method stub
    }

}
