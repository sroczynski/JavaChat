package br.feevale;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor de chat responsável por ouvir conexões e guardar mensagens
 * 
 * @author diovani
 * 
 */
public class ServidorChat extends Thread {

	/**
	 * Instancia singleton da classe
	 */
	private static ServidorChat instance;

	private Integer porta;

	private Socket cliente;

	private ServerSocket server;

	/**
	 * Construtor privado para garantir o uso como singleton
	 */
	private ServidorChat() {
	}

	/**
	 * Obtém a instancia singleton do classe ou a cria
	 * 
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
	 * 
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
	 * 
	 * @throws IOException
	 */
	public void finaliza() throws IOException {
		this.server.close();
		this.interrupt();
		instance = null;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Socket socketCliente = this.server.accept();
				
				new TelaChat(socketCliente, TelaChat.TpTela.SERVIDOR, meuNome);

				System.out.println("Socket no servidor: " + socketCliente);

			}
		} catch (Exception e) {
			System.out.printf(e.getMessage());
		}
	}

	public Socket getClient() {
		return this.cliente;
	}
}
