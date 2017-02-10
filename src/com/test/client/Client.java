package com.test.client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Client extends JFrame implements ActionListener {
	//Login GUI 변수들
	private JFrame loginGui = new JFrame();
	private JPanel loginPane;
	private JTextField ip_tf; //ip 받는 필드
	private JTextField port_tf; // port 받는 필드
	private JTextField id_tf;	//id 받는 필드
	private JButton login_btn = new JButton("접속버튼"); // 접속버튼
	
	
	//Main GUI 변수
	private JPanel contentPane;
	private JTextField mseeage_tf;
	private JButton notesend_btn = new JButton("쪽지보내기");
	private JButton joinroom_btn = new JButton("채팅방참여");
	private JButton createroom_btn = new JButton("방만들기");
	private JButton send_btn = new JButton("전송버튼");
	
	private JList User_list = new JList(); // 접속자 리스트
	private JList Room_list = new JList();	//전체 방목록 리스트
	private JTextArea chat_area = new JTextArea(); //채팅창 변수
	
	//Network 자원
	private Socket socket;
	private String ip;
	private int port;
	private String id;
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	//그외 변수들
	Vector user_list = new Vector();
	Vector room_list= new Vector();
	StringTokenizer st;
	
	
	Client(){	//생성자 
		loginInit(); //Login창 화면 구성 메서드
		mainInit(); //Main창 화면 구성 메서드
		start(); //리스너 설정 메서드
	}
	
	
	private void start(){
		login_btn.addActionListener(this); //로그인 버튼 리스너
		notesend_btn.addActionListener(this); //쪽지보내기 버튼 리스너
		joinroom_btn.addActionListener(this); //채팅방 참여 버튼 리스너
		createroom_btn.addActionListener(this);	//채팅방 만들기 버튼 리스너
		send_btn.addActionListener(this); //채팅 전송 버튼 리스너
		
	}
	
	private void mainInit(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 592, 429);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("전 체 접 속 자");
		lblNewLabel.setBounds(12, 21, 109, 15);
		contentPane.add(lblNewLabel);
		
		
		User_list.setBounds(12, 46, 109, 77);
		contentPane.add(User_list);
		
		
		Room_list.setBounds(12, 46, 109, 77);
		contentPane.add(Room_list);
		
		
		notesend_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		notesend_btn.setBounds(12, 133, 109, 23);
		contentPane.add(notesend_btn);
		
		
		JLabel lblNewLabel_1 = new JLabel("채 팅 방 목 록");
		lblNewLabel_1.setBounds(12, 166, 87, 15);
		contentPane.add(lblNewLabel_1);
		
		
		chat_area.setBounds(134, 43, 427, 301);
		contentPane.add(chat_area);
		
		JList list_2 = new JList();
		list_2.setBounds(12, 191, 109, 120);
		contentPane.add(list_2);
		
		
		joinroom_btn.setBounds(10, 321, 111, 23);
		contentPane.add(joinroom_btn);
		
		
		createroom_btn.setBounds(12, 354, 109, 23);
		contentPane.add(createroom_btn);
		
		mseeage_tf = new JTextField();
		mseeage_tf.setBounds(134, 355, 318, 21);
		contentPane.add(mseeage_tf);
		mseeage_tf.setColumns(10);
		
		
		send_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		send_btn.setBounds(464, 354, 97, 23);
		contentPane.add(send_btn);
		this.setVisible(true);
	}
	
	private void loginInit(){
		loginGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginGui.setBounds(100, 100, 301, 345);
		loginPane = new JPanel();
		loginPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		loginGui.setContentPane(loginPane);
		loginPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Server IP");
		lblNewLabel.setBounds(30, 118, 73, 15);
		loginPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Server Port");
		lblNewLabel_1.setBounds(30, 169, 87, 15);
		loginPane.add(lblNewLabel_1);
		
		JLabel lblId = new JLabel("ID");
		lblId.setBounds(33, 217, 57, 15);
		loginPane.add(lblId);
		
		ip_tf = new JTextField();
		ip_tf.setBounds(127, 115, 116, 21);
		loginPane.add(ip_tf);
		ip_tf.setColumns(10);
		
		port_tf = new JTextField();
		port_tf.setBounds(127, 166, 116, 21);
		loginPane.add(port_tf);
		port_tf.setColumns(10);
		
		id_tf = new JTextField();
		id_tf.setBounds(127, 214, 116, 21);
		loginPane.add(id_tf);
		id_tf.setColumns(10);
		
		
		login_btn.setBounds(30, 259, 213, 23);
		loginPane.add(login_btn);
		loginGui.setVisible(true);
	}
	
	private void netWork(){
		try {
			socket = new Socket(ip,port);
			if(socket!=null){ //정상적으로 소켓이 연결되었을 경우
				connection();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void connection(){ //실제적인 메서드 연결부분
		
		try{
		  is = socket.getInputStream();
		  dis = new DataInputStream(is);
		  os = socket.getOutputStream();
		  dos = new DataOutputStream(os);
		}catch(IOException e){
			e.printStackTrace();
		} // Stream 설정 끝
		
		//처음 접속시에 ID 전송
		send_message(id);
		
		//User_list에 사용자 추가
		user_list.add(id);
		User_list.setListData(user_list);
		
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {
					try {
						String msg = dis.readUTF(); // 메세지 수신
						System.out.println("서버로부터 수신된 메세지" + msg);
						
						inmessage(msg);
						
					} catch (IOException e) {

					}

				}

			}
		});
		th.start();
	}
	
	private void inmessage(String str){ //서버로부터 들어오는 모든 메세지
		st = new StringTokenizer(str,"/");
		
		String protocol = st.nextToken();
		String message = st.nextToken();
		System.out.println("프로토콜" + protocol);
		System.out.println("내용" + message);
		
		if(protocol.equals("NewUser")){ //새로운 접속자
			user_list.add(message);
			User_list.setListData(user_list);
		}else if(protocol.equals("OldUser")){
			user_list.add(message);
			User_list.setListData(user_list);
		}
			
		
	}
	
	
	private void send_message(String str){ //서버에게 메세지를 보내는 부분
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Client();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//login_btn 접속버튼
		if(e.getSource() == login_btn){
			System.out.println("로그인 버튼 클릭");
			ip = ip_tf.getText().trim(); //ip를 받아오는부분
			port =Integer.parseInt(port_tf.getText().trim()); //port는 int형 이므로 형변환을 시킴
			id = id_tf.getText().trim(); //id를 받아오는 부분
			netWork();
		}else if(e.getSource() == notesend_btn){
			System.out.println("쪽지 보내기 버튼 클릭");
		}else if(e.getSource()==joinroom_btn){
			System.out.println("방 참여 버튼 클릭");
		}else if(e.getSource()==createroom_btn){
			System.out.println("방 만들기 버튼 클릭");
		}else if(e.getSource()==send_btn){
			
			send_message("임시메시지입니다.");
			
			System.out.println("채팅 전송 버튼 클릭");
		}
		
	}
}
