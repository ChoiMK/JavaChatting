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

public class Server extends JFrame implements ActionListener { //JFrame과 액션리스너를 상속   
	
	private JPanel contentPane;
	private JTextField port_tf;	//port 텍스트필드
	private JTextArea textArea = new JTextArea();
	JButton start_btn = new JButton("서버 실행");
	JButton stop_btn = new JButton("서버 중지");
	
	//Network 자원
	private ServerSocket serverSocket;
	private Socket socket;
	private int port;
	private Vector userVc = new Vector();
	
	
	
	Server(){ //생성자
		
		init(); //화면을 생성하는 메서드
		start(); //리스너 설정 메소드
	}
	
	private void start(){
		start_btn.addActionListener(this);
		stop_btn.addActionListener(this);
	}
	
	private void init(){ //화면구성
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
		
		JLabel label = new JLabel("포트 번호");
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
		this.setVisible(true); //true : 화면에 보이게  false : 화면에 보이지 않게
	}
	
	private void serverStart(){
		try {
			serverSocket = new ServerSocket(port); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	if(serverSocket!=null){ //정상적으로 포트가 열렸을 경우
		connection();
	}
	
	}
	
	private void connection(){
		//1가지의 스레드에서는 1가지의 일만 처리할수있다.
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() { //스레드에서 처리할 일을 기재한다.
				
				while(true){
				
				try {
					textArea.append("사용자 접속 대기중\n");
					socket = serverSocket.accept(); //사용자 접속대기 무한대기
					textArea.append("사용자 접속\n");
					
					UserInfo user = new UserInfo(socket);
					
					user.start(); //객체의 스레드 실행
					
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}//whil문 끝
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
			System.out.println("서버 스타트 버튼 클릭");
			port = Integer.parseInt(port_tf.getText().trim());
			serverStart(); //소켓 생성 및 사용자 접속 대기
		}
		else if(e.getSource() == stop_btn){
			System.out.println("서버 스탑 버튼 클릭");
		}
	} // 액션 이벤트 끝
	
	class UserInfo extends Thread {
		private OutputStream os;
		private InputStream is;
		private DataOutputStream dos;
		private DataInputStream dis;

		private Socket user_socket;
		private String nickNmae;

		UserInfo(Socket socket) { // 생성자 메서드
			this.user_socket = socket;
			userNetWork();
		}
	
		
		private void userNetWork(){	// 네트워크 자원설정
			try {
				is = user_socket.getInputStream();
				dis = new DataInputStream(is);
				os = user_socket.getOutputStream();
				dos = new DataOutputStream(os);
				nickNmae = dis.readUTF(); //사용자의 닉네임을 받는다.
				textArea.append(nickNmae+":사용자 접속!\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		public void run() { // thread에서 처리할 내용
			while(true) {
				try {
					String msg = dis.readUTF();
					textArea.append(nickNmae+" :사용자로부터 들어온 메세지 :"+msg+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}
}
