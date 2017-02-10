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
	//Login GUI ������
	private JFrame loginGui = new JFrame();
	private JPanel loginPane;
	private JTextField ip_tf; //ip �޴� �ʵ�
	private JTextField port_tf; // port �޴� �ʵ�
	private JTextField id_tf;	//id �޴� �ʵ�
	private JButton login_btn = new JButton("���ӹ�ư"); // ���ӹ�ư
	
	
	//Main GUI ����
	private JPanel contentPane;
	private JTextField mseeage_tf;
	private JButton notesend_btn = new JButton("����������");
	private JButton joinroom_btn = new JButton("ä�ù�����");
	private JButton createroom_btn = new JButton("�游���");
	private JButton send_btn = new JButton("���۹�ư");
	
	private JList User_list = new JList(); // ������ ����Ʈ
	private JList Room_list = new JList();	//��ü ���� ����Ʈ
	private JTextArea chat_area = new JTextArea(); //ä��â ����
	
	//Network �ڿ�
	private Socket socket;
	private String ip;
	private int port;
	private String id;
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	//�׿� ������
	Vector user_list = new Vector();
	Vector room_list= new Vector();
	StringTokenizer st;
	
	
	Client(){	//������ 
		loginInit(); //Loginâ ȭ�� ���� �޼���
		mainInit(); //Mainâ ȭ�� ���� �޼���
		start(); //������ ���� �޼���
	}
	
	
	private void start(){
		login_btn.addActionListener(this); //�α��� ��ư ������
		notesend_btn.addActionListener(this); //���������� ��ư ������
		joinroom_btn.addActionListener(this); //ä�ù� ���� ��ư ������
		createroom_btn.addActionListener(this);	//ä�ù� ����� ��ư ������
		send_btn.addActionListener(this); //ä�� ���� ��ư ������
		
	}
	
	private void mainInit(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 592, 429);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("�� ü �� �� ��");
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
		
		
		JLabel lblNewLabel_1 = new JLabel("ä �� �� �� ��");
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
			if(socket!=null){ //���������� ������ ����Ǿ��� ���
				connection();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void connection(){ //�������� �޼��� ����κ�
		
		try{
		  is = socket.getInputStream();
		  dis = new DataInputStream(is);
		  os = socket.getOutputStream();
		  dos = new DataOutputStream(os);
		}catch(IOException e){
			e.printStackTrace();
		} // Stream ���� ��
		
		//ó�� ���ӽÿ� ID ����
		send_message(id);
		
		//User_list�� ����� �߰�
		user_list.add(id);
		User_list.setListData(user_list);
		
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {
					try {
						String msg = dis.readUTF(); // �޼��� ����
						System.out.println("�����κ��� ���ŵ� �޼���" + msg);
						
						inmessage(msg);
						
					} catch (IOException e) {

					}

				}

			}
		});
		th.start();
	}
	
	private void inmessage(String str){ //�����κ��� ������ ��� �޼���
		st = new StringTokenizer(str,"/");
		
		String protocol = st.nextToken();
		String message = st.nextToken();
		System.out.println("��������" + protocol);
		System.out.println("����" + message);
		
		if(protocol.equals("NewUser")){ //���ο� ������
			user_list.add(message);
			User_list.setListData(user_list);
		}else if(protocol.equals("OldUser")){
			user_list.add(message);
			User_list.setListData(user_list);
		}
			
		
	}
	
	
	private void send_message(String str){ //�������� �޼����� ������ �κ�
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
		//login_btn ���ӹ�ư
		if(e.getSource() == login_btn){
			System.out.println("�α��� ��ư Ŭ��");
			ip = ip_tf.getText().trim(); //ip�� �޾ƿ��ºκ�
			port =Integer.parseInt(port_tf.getText().trim()); //port�� int�� �̹Ƿ� ����ȯ�� ��Ŵ
			id = id_tf.getText().trim(); //id�� �޾ƿ��� �κ�
			netWork();
		}else if(e.getSource() == notesend_btn){
			System.out.println("���� ������ ��ư Ŭ��");
		}else if(e.getSource()==joinroom_btn){
			System.out.println("�� ���� ��ư Ŭ��");
		}else if(e.getSource()==createroom_btn){
			System.out.println("�� ����� ��ư Ŭ��");
		}else if(e.getSource()==send_btn){
			
			send_message("�ӽø޽����Դϴ�.");
			
			System.out.println("ä�� ���� ��ư Ŭ��");
		}
		
	}
}
