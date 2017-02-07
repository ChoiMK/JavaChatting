package com.test.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame implements ActionListener { //JFrame�� �׼Ǹ����ʸ� ���   
	
	private JPanel contentPane;
	private JTextField port_tf;	//port �ؽ�Ʈ�ʵ�
	private JTextArea textArea = new JTextArea();
	JButton start_btn = new JButton("���� ����");
	JButton stop_btn = new JButton("���� ����");
	
	//Network �ڿ�
	private ServerSocket serverSocket;
	private Socket socket;
	private int port;
	private Vector userVc = new Vector();
	
	
	
	Server(){ //������
		
		init(); //ȭ���� �����ϴ� �޼���
		start(); //������ ���� �޼ҵ�
	}
	
	private void start(){
		start_btn.addActionListener(this);
		stop_btn.addActionListener(this);
	}
	
	private void init(){ //ȭ�鱸��
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 351, 385);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 311, 157);
		contentPane.add(scrollPane);
		
		
		scrollPane.setViewportView(textArea);
		
		JLabel label = new JLabel("��Ʈ ��ȣ");
		label.setBounds(12, 208, 57, 15);
		contentPane.add(label);
		
		port_tf = new JTextField();
		port_tf.setBounds(70, 205, 253, 21);
		contentPane.add(port_tf);
		port_tf.setColumns(10);
		
		
		start_btn.setBounds(12, 263, 154, 23);
		contentPane.add(start_btn);
		
		
		stop_btn.setBounds(163, 263, 161, 23);
		contentPane.add(stop_btn);
		this.setVisible(true); //true : ȭ�鿡 ���̰�  false : ȭ�鿡 ������ �ʰ�
	}
	
	private void serverStart(){
		try {
			serverSocket = new ServerSocket(port); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	if(serverSocket!=null){ //���������� ��Ʈ�� ������ ���
		connection();
	}
	
	}
	
	private void connection(){
		//1������ �����忡���� 1������ �ϸ� ó���Ҽ��ִ�.
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() { //�����忡�� ó���� ���� �����Ѵ�.
				
				while(true){
				
				try {
					textArea.append("����� ���� �����\n");
					socket = serverSocket.accept(); //����� ���Ӵ�� ���Ѵ��
					textArea.append("����� ����\n");
					
					UserInfo user = new UserInfo(socket);
					
					user.start(); //��ü�� ������ ����
					
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}//whil�� ��
		 }
		});

		th.start();
	}
	
	
	
	public static void main(String[] args) {
		new Server();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == start_btn){
			System.out.println("���� ��ŸƮ ��ư Ŭ��");
			port = Integer.parseInt(port_tf.getText().trim());
			serverStart(); //���� ���� �� ����� ���� ���
		}
		else if(e.getSource() == stop_btn){
			System.out.println("���� ��ž ��ư Ŭ��");
		}
	} // �׼� �̺�Ʈ ��
	
	class UserInfo extends Thread {
		private OutputStream os;
		private InputStream is;
		private DataOutputStream dos;
		private DataInputStream dis;

		private Socket user_socket;
		private String nickNmae;

		UserInfo(Socket socket) { // ������ �޼���
			this.user_socket = socket;
			userNetWork();
		}
	
		
		private void userNetWork(){	// ��Ʈ��ũ �ڿ�����
			try {
				is = user_socket.getInputStream();
				dis = new DataInputStream(is);
				os = user_socket.getOutputStream();
				dos = new DataOutputStream(os);
				nickNmae = dis.readUTF(); //������� �г����� �޴´�.
				textArea.append(nickNmae+":����� ����!\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		public void run() { // thread���� ó���� ����
			while(true) {
				try {
					String msg = dis.readUTF();
					textArea.append(nickNmae+" :����ڷκ��� ���� �޼��� :"+msg+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}
}
