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
import java.util.StringTokenizer;
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
	private Vector user_vc = new Vector();
	private StringTokenizer st;
	private Vector room_vc  = new Vector();
	
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
		private String nickName;
		
		private boolean roomCh = true;

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
				nickName = dis.readUTF(); //������� �г����� �޴´�.
				textArea.append(nickName+":����� ����!\n");
				
				//���� ����ڵ鿡�� ���ο� �˸�
				System.out.println("���� ���ӵ� ����� ��"+user_vc.size());
				
				broadCast("NewUser/"+nickName); // ��������ڿ��� �ڽ��� �˸���
				
				//�ڽſ��� ���� ����ڸ� �޾ƿ��� �κ�
				for(int i=0; i<user_vc.size(); i++){
					UserInfo u = (UserInfo)user_vc.elementAt(i);
					send_Message("OldUser/"+u.nickName);
				}
				
				//�ڽſ��� ���� �� ����� �޾ƿ��� �κ�
				for(int i=0; i<room_vc.size(); i++){
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					send_Message("OldRoom/"+r.room_name);
				}
				send_Message("room_list_update/ ");
				
				
					
				user_vc.add(this);//����ڿ��� �˸��� Vector�� �ڽ��� �߰�
				
				broadCast("user_list_update/ ");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		public void run() { // thread���� ó���� ����
			while (true) {
				try {
					String msg = dis.readUTF();
					textArea.append(nickName + " : ����ڷκ��� ���� �޼��� :" + msg + "\n");// �޼�������
					inMessage(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}//run �޼��� ��
		
		private void inMessage(String str){ //Ŭ���̾�Ʈ�κ��� ������ �޼��� ó��
			st = new StringTokenizer(str,"/");
			String protocol = st.nextToken();
			String message = st.nextToken();
			
			System.out.println("�������� : " +protocol);
			System.out.println("�޼��� : " +message);
			
			if(protocol.equals("Note")){
				//protocol = Note
				//message = user
				//note = �޴³���
				String note = st.nextToken();
				System.out.println("�޴»�� : "+message);
				System.out.println("�������� : "+note);
				
				//���Ϳ��� �ش� ����ڸ� ã�Ƽ� �޼��� ����
				for(int i=0; i<user_vc.size(); i++){
					UserInfo u = (UserInfo)user_vc.elementAt(i);
					
					if(u.nickName.equals(message)){
						u.send_Message("Note/"+nickName+"/"+note);
						// Note/User1/~~~~~
					}
					
				}
				
			}// if����
			else if(protocol.equals("CreateRoom")){
				//1.���� ���� ���� ���� �ϴ��� Ȯ���Ѵ�.
				for(int i=0;i<room_vc.size();i++){
					
					RoomInfo r = (RoomInfo)room_vc.elementAt(i);
					
					if(r.room_name.equals(message)){ //������� �ϴ� ���� �̹� ���� �Ҷ�
						send_Message("CreateRoomFail/ok");
						roomCh = false;
						break;
					}
				} // for ��
				
				if(roomCh){ //���� �����������
					RoomInfo new_room = new RoomInfo(message, this);
					room_vc.add(new_room); //��ü �� ���Ϳ� ���� �߰�
					send_Message("CreateRoom/"+message);
					
					broadCast("New_Room/"+message);
				}
				roomCh = true;
			}// else if �� ��
			else if(protocol.equals("Chatting")){
				
				String msg = st.nextToken();
				
				for(int i=0; i<room_vc.size(); i++){
					RoomInfo r =(RoomInfo)room_vc.elementAt(i);
					
					if(r.room_name.equals(message)){ // �ش� ���� ã������
						r.broadCast_Room("Chatting/"+nickName+"/"+msg);
					}
				}
			}//else if�� ��
			else if(protocol.equals("JoinRoom")){
				for(int i=0; i<room_vc.size(); i++){
					RoomInfo r =(RoomInfo)room_vc.elementAt(i);
					if(r.room_name.equals(message)){
						
						//���ο� ����ڸ� �˸���.
						r.broadCast_Room("Chatting/�˸�/*******"+nickName+"���� �����ϼ̽��ϴ�*******");
						
						//������߰�
						r.add_user(this);
						send_Message("JoinRoom/"+message);
					}
				}
			}
			
			
		}
		private void broadCast(String str){ //��ü ����ڿ��� �޼����� �����ºκ�
			for(int i=0; i<user_vc.size(); i++ ) {  
				UserInfo u = (UserInfo)user_vc.elementAt(i);
				u.send_Message(str);
			}
		}
		
		private void send_Message(String str){ //���ڿ��� �޾Ƽ� ����
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	} //UserInfo class ��

	class RoomInfo{
		private String room_name;
		private Vector room_user_vc = new Vector(); 
		
		public RoomInfo(String str, UserInfo u) {
			this.room_name = str;
			this.room_user_vc.add(u);
		}
		
		public void broadCast_Room(String str){ //���� ���� ��� ������� �˸���.
			for(int i=0; i<room_user_vc.size(); i++){
				UserInfo u = (UserInfo)room_user_vc.elementAt(i);
					u.send_Message(str);
				}
			}
		
		
		private void add_user(UserInfo u){
			this.room_user_vc.add(u);
		}
	
	}



}
