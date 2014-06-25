package br.feevale;
	
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
	/**
	 * Porta utilizada: 6789
	 * @author nicolasvinicius
	 *
	 */
public class Main extends JFrame implements WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField nome;

	public Main() {

		super("Chat");
		setBounds(100, 100, 300, 200);
		setLayout(null);

		nome = new JTextField();
		nome.setBounds(30, 40, 200, 23);
		getContentPane().add(nome);

		JButton btn = new JButton("Iniciar Servidor");
		btn.setBounds(30, 80, 150, 25);
		getContentPane().add(btn);

		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					controlaServidor( (JButton) ev.getSource());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		btn = new JButton("Conectar");
		btn.setBounds(30, 110, 150, 25);
		getContentPane().add(btn);

		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				conectar();
			}
		});

		addWindowListener(this);

		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	protected void conectar() {

		try {
			Socket socket = new Socket("localhost", 6789);
			new TelaChat(socket, TelaChat.TpTela.CLIENTE, nome.getText());
			System.out.println("Meu socket: " + socket);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Não foi possível conectar -> " + e.getMessage());
		}
	}

	public void controlaServidor(JButton btn) throws IOException {

		if (nome.getText().trim().length() == 0) {
			JOptionPane.showMessageDialog(this,
					"Para inicializar, digite seu nome");
			nome.requestFocusInWindow();
			return;
		}
		// Pega instância da classe ServidorChat que é um Singleton
		ServidorChat servidor = ServidorChat.getInstance();
		
		if (!servidor.isAlive()) {
			try {
				servidor.inicia(6789);
				btn.setText("Finalizar Servidor");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this,
						"Não foi possível iniciar o chat -> " + e.getMessage());
			}
		}else{
			btn.setText("Iniciar Servidor");
			servidor.finaliza();
		}
	}

	public static void main(String[] args) {

		System.out.println("oi");
		new Main();
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {

		ServidorChat servidor = ServidorChat.getInstance();

		if (servidor.isAlive()) {
			try {
				servidor.finaliza();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}