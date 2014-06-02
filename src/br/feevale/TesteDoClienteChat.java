package br.feevale;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class TesteDoClienteChat {
    public static void main(String[] args) {
        ClienteChat client;
        try {
            client = new ClienteChat("localhost", 6566);
            client.login("Paulo Diovani");

            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Digite sua mensagem: ");

                try {
                    String msg = br.readLine();
                    client.mensagem(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
