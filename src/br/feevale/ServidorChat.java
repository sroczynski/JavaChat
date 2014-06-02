package br.feevale;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor de chat responsável por ouvir conexões e guardar mensagens
 * @author Paulo Diovani Gonçalves <paulo@diovani.com>
 *
 */
public class ServidorChat extends Thread {

	/**
	 * Instancia singleton da classe
	 */
	private static ServidorChat instance;
	
	/**
	 * Servidor guardado
	 */
	private ServerSocket server;
	
	private Socket client;
	
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
		try {
			server = new ServerSocket(porta);
		} catch (IOException e) {
			e.printStackTrace();
		}
		start();
	}
	
	/**
	 * Finaliza a thread a fecha o servidor 
	 */
	public void finaliza() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		interrupt();
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				client = server.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
