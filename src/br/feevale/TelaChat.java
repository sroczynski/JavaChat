package br.feevale;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.JSONException;
import org.json.JSONObject;

public class TelaChat extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum TpTela {
		SERVIDOR, CLIENTE
	}

	/*
	 * Atributos de tela
	 */
	private JTextArea log;
	private JTextField mensagem;
	private JButton arquivo;
	private JButton atencao;
	/*
	 * Outros atributos
	 */

	public JFileChooser fc;
	public JFileChooser musica1;
	public JFileChooser musica2;
	public String file;
	private Socket socket;
	private String meuNome;
	private String outroNome;

	/**
	 * @param socket
	 * @param tipo
	 * @param meuNome
	 * @throws JSONException
	 */
	public TelaChat(Socket socket, TpTela tipo, String meuNome)
			throws JSONException {

		this.socket = socket;
		this.meuNome = meuNome;

		if (tipo == TpTela.SERVIDOR) {
			setBounds(10, 80, 500, 450);
		} else {
			setBounds(515, 80, 500, 450);
		}
		setTitle(meuNome);
		setLayout(null);

		log = new JTextArea();
		log.setLineWrap(true);
		log.setEditable(false);

		JScrollPane sp = new JScrollPane(log);
		sp.setBounds(10, 10, 470, 300);
		getContentPane().add(sp);

		mensagem = new JTextField();
		mensagem.setBounds(10, 320, 470, 23);
		getContentPane().add(mensagem);

		mensagem.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					enviaMensagem();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		/*
		 * botão para enviar arquivo
		 */
		arquivo = new JButton("Enviar arquivo");
		arquivo.setBounds(10, 345, 150, 20);
		getContentPane().add(arquivo);
		arquivo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				perguntaEnviarArquivo();
			}
		});

		/*
		 * botão para chamar a atenção
		 */

		atencao = new JButton("Chamar atenção");
		atencao.setBounds(170, 345, 150, 20);
		getContentPane().add(atencao);
		atencao.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				chamaAtencao();
			}
		});

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);

		System.out.println("Iniciando...");
		new LeitorDeSocket().start();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
		}

		enviaHandshake();
	}

	private void enviaHandshake() throws JSONException {

		JSONObject obj = new JSONObject();
		obj.put("tpTransacao", 1);
		obj.put("meuNome", meuNome);

		enviaTransacao(obj);
	}

	private void enviaTransacao(JSONObject obj) {

		try {
			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);

			dos.writeUTF(obj.toString());
			os.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void enviaMensagem() {

		String mensagemAEnviar = mensagem.getText();

		mensagem.setText(null);
		mensagem.requestFocusInWindow();

		try {
			JSONObject obj = new JSONObject();
			obj.put("tpTransacao", 2);
			obj.put("msg", mensagemAEnviar);

			enviaTransacao(obj);

			escreveNoLog(meuNome + ": " + mensagemAEnviar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void perguntaEnviarArquivo() {
		// TODO Auto-generated method stub
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(TelaChat.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File file = fc.getSelectedFile();

			try {
				JSONObject obj = new JSONObject();
				obj.put("tpTransacao", 3);
				obj.put("arquivo", file.getName());
				obj.put("tamanho", file.length());

				enviaTransacao(obj);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void respondeEnviarArquivo() {
		// TODO Auto-generated method stub

	}

	private class LeitorDeSocket extends Thread {

		@Override
		public void run() {
			try {
				InputStream is = socket.getInputStream();

				DataInputStream dis = new DataInputStream(is);

				while (isVisible()) {

					String mensagem = dis.readUTF();

					JSONObject obj = new JSONObject(mensagem);

					int tpTransacao = obj.getInt("tpTransacao");

					switch (tpTransacao) {

					case 1:
						trataHandshake(obj);
						break;
					case 2:
						trataMensagem(obj);
						break;
					case 3:
						chamaAtencao();
						break;
					case 4:
						perguntaEnviarArquivo();
						break;
					case 5:
						respondeEnviarArquivo();
						break;

					}

					trataArquivo(obj);
					break;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void escreveNoLog(String msg) {
		log.append(msg);
		log.append("\n");
	}

	public void trataHandshake(JSONObject obj) throws JSONException {
		outroNome = obj.getString("meuNome");
	}

	public void trataMensagem(JSONObject obj) throws JSONException {

		String mensagem = obj.getString("msg");
		escreveNoLog(outroNome + ": " + mensagem);
	}

	/*
	 * Trata arquivo a receber
	 */
	public void trataArquivo(JSONObject obj) throws JSONException {

		File file = (File) obj.get("Arquivo");

		int confirma = JOptionPane.showConfirmDialog(null,
				"Deseja receber o arquivo" + file.getName(), "Atenção",
				JOptionPane.YES_NO_OPTION);
		if (confirma == JOptionPane.YES_OPTION) {

			int returnVal = fc.showSaveDialog(TelaChat.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {

				// File file = fc.getSelectedFile();

				escreveNoLog(outroNome + ": " + file.getName() + "." + "/n");

			} else {
				escreveNoLog(outroNome + ": " + "Cancelou recebimento");
			}
		}

		String mensagem = obj.getString("msg");

		escreveNoLog(outroNome + ": " + mensagem);
	}

	public void chamaAtencao() {
		try {
			// Open an audio input stream.
			URL url = this
					.getClass()
					.getClassLoader()
					.getResource("C:/Users/nicolasvinicius/Desktop/sw_main.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
		

		private void trataPerguntaEnviarArquivo(JSONObject obj) {
			// TODO Auto-generated method stub

				String arquivo = obj.getString("arquivo");
				String tamanho = obj.getString("tamanho");

				respostaEnviarArquivo();

				String mensagem = obj.getString("msg");

				escreveNoLog(outroNome + ": " + mensagem);
		}
		
		
		private void respostaEnviarArquivo() {
			// TODO Auto-generated method stub
			int confirma = JOptionPane.showConfirmDialog(null,
					"Deseja receber o arquivo/n" + arquivo + "/n " +  tamanho , "Atenção",
					JOptionPane.YES_NO_OPTION);
			
			if (confirma == JOptionPane.YES_OPTION) {
				try {
					JSONObject obj = new JSONObject();
					obj.put("tpTransacao", 6);
					obj.put("resposta", true);
					obj.put("porta", 6666)

					enviaTransacao(obj);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				try {
					JSONObject obj = new JSONObject();
					obj.put("tpTransacao", 6);
					obj.put("resposta", false);
										
					enviaTransacao(obj);
				
				} catch (Exception e) {
						e.printStackTrace();
				}
			}			
		}
		
		private void trataRespostaEnviarArquivo(JSONObject obj) {
			// TODO Auto-generated method stub
			boolean resposta = obj.get("resposta");
			if(resposta = true){
				int porta = obj.getInt("porta");
				
			}
		}
	
	}
}
