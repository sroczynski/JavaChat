package br.feevale;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

import javazoom.jl.player.Player;
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
	public File fileArq;
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
			setBounds(10, 80, 500, 420);
		} else {
			setBounds(515, 80, 500, 420);
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

				EnviarArquivo();

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

		addWindowListener(new FechaJanelaListener());

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
	
	public void chamaAtencao(){
		try {
			JSONObject obj = new JSONObject();
			obj.put("tpTransacao", 3);
			
			enviaTransacao(obj);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	private void EnviarArquivo() {
		// TODO Auto-generated method stub
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = fc.showOpenDialog(TelaChat.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			fileArq = fc.getSelectedFile();

			try {
				JSONObject obj = new JSONObject();
				obj.put("tpTransacao", 5);
				obj.put("arquivo", fileArq.getName());
				obj.put("tamanho", fileArq.length());

				enviaTransacao(obj);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void respondeEnviarArquivo(boolean receber) {

		try {
			JSONObject obj = new JSONObject();
			obj.put("tpTransacao", 6);
			obj.put("resposta", receber);
			obj.put("file", fileArq);
			
			enviaTransacao(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void trataResposta(JSONObject obj) throws JSONException {

		boolean resposta = obj.getBoolean("resposta");
		
		String mensagem;
		if (resposta == true) {
			
			fileArq = (File) obj.getFile("file");
			
			fc.setSelectedFile (fileArq);

		    int result = fc.showSaveDialog (this);
				
		    if (result == JFileChooser.APPROVE_OPTION) {
		         
		  		mensagem = "Confirmou Receber o Arquivo";

				try {
					JSONObject objMen = new JSONObject();
					objMen.put("tpTransacao", 2);
					objMen.put("msg", mensagem);
					
					enviaTransacao(objMen);
	
					escreveNoLog(meuNome + ": " + mensagem);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }else{
		    	mensagem = "Erro no envio do arquivo!";

				try {
					JSONObject objMen = new JSONObject();
					objMen.put("tpTransacao", 2);
					objMen.put("msg", mensagem);
					
					enviaTransacao(objMen);
	
					escreveNoLog(meuNome + ": " + mensagem);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }

		} else {

			mensagem = "Não confirmou receber o arquivo";

			try {
				JSONObject objMen = new JSONObject();
				objMen.put("tpTransacao", 2);
				objMen.put("msg", mensagem);

				enviaTransacao(objMen);

				escreveNoLog(meuNome + ": " + mensagem);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}		
				
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
						tratachamaAtencao();
						break;
					case 4:
						trataDesconexao();
						break;
					case 5:
						perguntaEnviarArquivo(obj);
						break;
					case 6:
						trataResposta(obj);
						break;
					}

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

	public void informaDesconexao() {
		// TODO Auto-generated method stub
		try {
			JSONObject obj = new JSONObject();
			obj.put("tpTransacao", 4);

			enviaTransacao(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * trata desconexão de algum usuário
	 */

	public void trataDesconexao() {

		JOptionPane.showMessageDialog(null, "Usuário " + this.outroNome
				+ " desconectou");
		// Desabilida os campos de ação para o usuário
		this.mensagem.setEnabled(false);
		this.arquivo.setEnabled(false);
		this.atencao.setEnabled(false);

	}

	/*
	 * Trata handshake
	 */
	public void trataHandshake(JSONObject obj) throws JSONException {
		outroNome = obj.getString("meuNome");
	}

	/*
	 * Trata mensagem
	 */
	public void trataMensagem(JSONObject obj) throws JSONException {

		String mensagem = obj.getString("msg");
		escreveNoLog(outroNome + ": " + mensagem);
	}

	/*
	 * pergunta se deseja aceitar arquivo;
	 */
	private void perguntaEnviarArquivo(JSONObject obj) throws JSONException {

		String arquivo = obj.getString("arquivo");
		Long tamanho = obj.getLong("tamanho");

		int confirma = JOptionPane.showConfirmDialog(null,
				"Deseja receber o arquivo \n nome: " + arquivo + "\n Tamanho: "
						+ tamanho, "Atenção", JOptionPane.YES_NO_OPTION);

		if (confirma == JOptionPane.YES_OPTION) {

			respondeEnviarArquivo(true);

		} else {

			respondeEnviarArquivo(false);
		}

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
	
	public void tratachamaAtencao() {
		//String com o caminho do arquivo MP3 a ser tocado
	    String path = "sw_main.mp3";
	    //Instanciação de um objeto File com o arquivo MP3
	    File mp3File = new File(path);
	    //Instanciação do Objeto MP3, a qual criamos a classe.
	    MP3 musica = new MP3(mp3File);
	    //Finalmente a chamada do método que toca a música
	    musica.play();
	}

	private void respostaEnviarArquivo(String nome, String tamanho) {
		// TODO Auto-generated method stub

		int confirma = JOptionPane.showConfirmDialog(null,
				"Deseja receber o arquivo/n" + arquivo + "/n " + tamanho,
				"Atenção", JOptionPane.YES_NO_OPTION);

		if (confirma == JOptionPane.YES_OPTION) {
			try {
				JSONObject obj = new JSONObject();
				obj.put("tpTransacao", 6);
				obj.put("resposta", true);

				enviaTransacao(obj);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
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

	private class FechaJanelaListener implements WindowListener {

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(WindowEvent e) {
			TelaChat tela = (TelaChat) e.getSource();
			tela.informaDesconexao();
			dispose();
			ServidorChat.getInstance().interrupt();
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub

		}

	}
	
	private class MP3 {
		  /**
		   * Objeto para nosso arquivo MP3 a ser tocado
		   */
		  private File mp3;
		  /**
		   * Objeto Player da biblioteca jLayer. Ele tocará o arquivo
		   * MP3
		   */
		  private Player player;
		  /**
		   * Construtor que recebe o objeto File referenciando o arquivo
		   * MP3 a ser tocado e atribui ao atributo MP3 da classe.
		   *
		   * @param mp3
		   */
		  public MP3(File mp3) {
		    this.mp3 = mp3;
		  }
		  /**
		   * Método que toca o MP3
		   */
		  public void play() {
		      try {
		                FileInputStream fis     = new FileInputStream(mp3);
		                BufferedInputStream bis = new BufferedInputStream(fis);
		                this.player = new Player(bis);
		                System.out.println("Tocando!");
		                this.player.play();
		                System.out.println("Terminado!");
		            }
		            catch (Exception e) {
		                System.out.println("Problema ao tocar " + mp3);
		                e.printStackTrace();
		            }
		     }
	}
}
