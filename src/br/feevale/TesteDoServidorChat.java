package br.feevale;

public class TesteDoServidorChat {

    public static void main(String[] args) {
        ServidorChat server = ServidorChat.getInstance();
        server.inicia(6566);

        System.out.println("Servidor iniciado.");
    }
}
