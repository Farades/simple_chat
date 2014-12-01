import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatClient {
	JTextArea incoming;
	JTextField outMessageField;
	PrintWriter writer;
	BufferedReader reader;
	Socket sock;

	public void go() {
		JFrame frame = new JFrame("KMZ - chat client");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		incoming = new JTextArea(15, 30);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane scroller = new JScrollPane(incoming);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel mainPanel = new JPanel();
		outMessageField = new JTextField(20);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(scroller);
		mainPanel.add(outMessageField);
		mainPanel.add(sendButton);
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		setupNetwork();
		
		Thread readThread = new Thread(new IncomingReader());
		readThread.start();
		
		frame.setSize(400, 400);
		frame.setVisible(true);
	}
	
	public void setupNetwork() {
		try {
			sock = new Socket("127.0.0.1", 5000);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			System.out.println("Network ready - OK.");
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.go();
	}
	
	public class SendButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent ev) {
			try {
				writer.println(outMessageField.getText());
				writer.flush();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			outMessageField.setText("");
		}
	}
	
	public class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ( (message = reader.readLine()) != null ) {
					System.out.println("read: " + message);
					incoming.append(message + "\n");
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}