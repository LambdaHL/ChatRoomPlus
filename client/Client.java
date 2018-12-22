package client;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.io.*;

public class Client extends JFrame
{
	private JPanel contentPane;
	private JTextField textField_UserName;
	private JPasswordField passwordField;
	private JLabel label_ServerStatus;
	private Socket socket,socket2;
	private JList<Object> list_UserList,list_Friends;
	private JTextPane textPane;
	private JEditorPane editorPane;
	private JToggleButton tglbtn_Bold, tglbtn_Italic, tglbtn_Underline;
	private JButton btn_Register, btn_Login;
	private JComboBox comboBox, comboBox_1;
	private JButton btn_Enter;
	private JFrame logpad;
	private String userName,userNickName;
	private ArrayList<User> userList=new ArrayList<User>();
	private InputStream inputStream,inputStream2;
	private OutputStream outputStream,outputStream2;
	
	public Client()
	{
		createLogPad();
		createClientGUI();
		new ConnectToServerThread().start();
	}
	
	class ConnectToServerThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				socket=new Socket("127.0.0.1", 2018);
				socket2=new Socket("127.0.0.1", 2019);
				inputStream=socket.getInputStream();
				outputStream=socket.getOutputStream();
				inputStream2=socket2.getInputStream();
				outputStream2=socket2.getOutputStream();
				label_ServerStatus.setText("Server Status:Connected");
				label_ServerStatus.setForeground(Color.GREEN);
			}
			catch (Exception e)
			{
				label_ServerStatus.setText("Server Status:Not Connected");
				label_ServerStatus.setForeground(Color.RED);
				e.printStackTrace();
			}
		}
	}
	
	class ListenThread extends Thread
	{
		String string;
		byte[] bytes;
		
		@Override
		public void run()
		{
			try
			{
				inputStream=socket.getInputStream();
				outputStream=socket.getOutputStream();
				while(true)
				{
					bytes=new byte[1024];
					int len;
					if(socket.isClosed())
						break;
					while(inputStream.available()!=0)
					{
						
						len=inputStream.read(bytes);//received onlineListString
						string=new String(bytes, 0, len);
						switch (string.charAt(0))
						{
							case '&'://Update User List
							{
								userList.clear();
								//get logged user
								String temp=string.substring(1);
								String[] strings=temp.split("\\|");
								for(String s:strings)
								{
									System.out.println(s);
								}
								int userCount=Integer.parseInt(strings[1]);
								System.out.println("userCount:"+userCount);
								
								/*//response server that client had received previous data and ask for next
								outputStream.write("+".getBytes());//send response signal
								outputStream.flush();
								
								//receive icon
								File iconSelfDir=new File(System.getProperty("user.dir") + "\\Client\\" + userName);
								if(!iconSelfDir.exists())
									iconSelfDir.mkdir();
								File iconSelfFile=new File(System.getProperty("user.dir") + "\\Client\\" + userName + "\\usericon.png");
								if(iconSelfFile.exists())
									iconSelfFile.delete();
								iconSelfFile.createNewFile();
								DataInputStream dataInputStream=new DataInputStream(inputStream);
								DataOutputStream dataOutputStream=new DataOutputStream(new FileOutputStream(iconSelfFile));
								bytes=new byte[1024];
								
								len=dataInputStream.read(bytes);
								dataOutputStream.write(bytes, 0, len);
								dataOutputStream.flush();*/
								
								User metaUser;
								
								//get all user
								for(int i=0;i<userCount;i++)
								{
									System.out.println("circulation entered");
									System.out.println("available:"+inputStream.available());
									while(inputStream.available()==0){}
									len=inputStream.read(bytes);	//block
									System.out.println("read complete");
									temp=new String(bytes, 0, len);
									String[] inStrings=temp.split("\\|");
									outputStream.write("&".getBytes());
									outputStream.flush();
									
									for(String s:strings)
									{
										System.out.println(s);
									}
									for(String s:inStrings)
									{
										System.out.println(s);
									}
									
									File userDir=new File(System.getProperty("user.dir") + "\\Client\\" + strings[0]);//User Directory
									if(!userDir.exists())
										userDir.mkdir();
									File iconFile=new File(System.getProperty("user.dir") + "\\Client\\" + strings[0] + "\\" + inStrings[0] + ".png");
									if(iconFile.exists())
										iconFile.delete();
									iconFile.createNewFile();
									DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
									bytes=new byte[1024];
									while(dataInputStream.available()==0) {}
									len=dataInputStream.read(bytes);
									System.out.println("file read complete");
									DataOutputStream dataOutputStream=new DataOutputStream(new FileOutputStream(iconFile));
									dataOutputStream.write(bytes, 0, len);
									dataOutputStream.flush();
									
									//receive icon
									
									metaUser=new User(inStrings[0], inStrings[1], iconFile.getAbsolutePath(), null, null);
									userList.add(metaUser);
								}
								list_UserList.setModel(new MyListModel<Object>(userList));
								list_UserList.setCellRenderer(new MyListCellRenderer());
								System.out.println("list set\n");
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createClientGUI()
	{
		setTitle("CharRoomPlus");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 855, 656);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane_OnlineUserList = new JScrollPane();
		scrollPane_OnlineUserList.setAutoscrolls(true);
		scrollPane_OnlineUserList.setBounds(630, 25, 208, 281);
		contentPane.add(scrollPane_OnlineUserList);
		
		list_UserList = new JList<>();
		scrollPane_OnlineUserList.setViewportView(list_UserList);
		
		JScrollPane scrollPane_Message = new JScrollPane();
		scrollPane_Message.setAutoscrolls(true);
		scrollPane_Message.setBounds(0, 0, 622, 471);
		contentPane.add(scrollPane_Message);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		scrollPane_Message.setViewportView(textPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		scrollPane.setBounds(0, 515, 558, 100);
		contentPane.add(scrollPane);
		
		editorPane = new JEditorPane();
		editorPane.setLocation(0, 515);
		scrollPane.setViewportView(editorPane);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(UIManager.getColor("Button.background"));
		toolBar.setFloatable(false);
		toolBar.setBounds(0, 475, 620, 37);
		contentPane.add(toolBar);
		
		tglbtn_Bold = new JToggleButton("");
		tglbtn_Bold.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/bold.png")));
		toolBar.add(tglbtn_Bold);
		
		tglbtn_Italic = new JToggleButton("");
		tglbtn_Italic.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/italic.png")));
		toolBar.add(tglbtn_Italic);
		
		tglbtn_Underline = new JToggleButton("");
		tglbtn_Underline.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/underline.png")));
		toolBar.add(tglbtn_Underline);
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"微软雅黑", "Consola", "Courier New"}));
		comboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		comboBox.setMaximumRowCount(3);
		toolBar.add(comboBox);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(10);
		toolBar.add(horizontalStrut_1);
		
		comboBox_1 = new JComboBox();
		comboBox_1.setFont(new Font("Courier New", Font.PLAIN, 18));
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"12", "14", "16", "18", "20", "22", "24"}));
		toolBar.add(comboBox_1);
		
		Component horizontalStrut = Box.createHorizontalStrut(200);
		toolBar.add(horizontalStrut);
		
		btn_Enter = new JButton("");
		btn_Enter.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/enter.png")));
		btn_Enter.setBounds(560, 515, 60, 100);
		contentPane.add(btn_Enter);
		
		JScrollPane scrollPane_Friends = new JScrollPane();
		scrollPane_Friends.setAutoscrolls(true);
		scrollPane_Friends.setBounds(630, 334, 208, 281);
		contentPane.add(scrollPane_Friends);
		
		list_Friends = new JList<>();
		scrollPane_Friends.setViewportView(list_Friends);
		
		JLabel lblOnline = new JLabel("Online");
		lblOnline.setFont(new Font("Consolas", Font.PLAIN, 16));
		lblOnline.setBounds(632, 8, 68, 19);
		contentPane.add(lblOnline);
		
		JLabel lblNewLabel = new JLabel("Friends");
		lblNewLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
		lblNewLabel.setBounds(630, 316, 70, 19);
		contentPane.add(lblNewLabel);
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				try
				{
					System.out.println("closing");
					OutputStream outputStream=socket.getOutputStream();
					outputStream.write("~".getBytes());
					outputStream.flush();
					
					byte[] bytes=new byte[1024];
					InputStream inputStream=socket.getInputStream();
					int len=inputStream.read(bytes);
					socket.shutdownInput();
					socket.shutdownOutput();
					socket.close();
				}
				catch (Exception E)
				{
					E.printStackTrace();
				}
			}
		});
		
		btn_Enter.setMnemonic(KeyEvent.VK_ENTER);
		btn_Enter.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String string=editorPane.getText();
				sendMessage(string);
			}
		});

	}
	
	class LoginThread extends Thread
	{
		private InputStream inputStream;
		private OutputStream outputStream;
		private	byte[] bytes;
		private String string;
		
		public LoginThread() {}
		
		@Override
		public void run()
		{
			try
			{
				inputStream=socket.getInputStream();
				outputStream=socket.getOutputStream();
				int len;
				String pwd=new String(passwordField.getPassword());
				string=">"+textField_UserName.getText()+"|"+pwd;
				bytes=string.getBytes();
				len=bytes.length;
				outputStream.write(bytes, 0, len);
				outputStream.flush();
				bytes=new byte[1024];
				boolean isResponse=false;
				while(!isResponse)
				{
					len=inputStream.read(bytes);
					string=new String(bytes);
					switch (string.charAt(0))
					{
						case '^'://login success
						{
							logpad.setVisible(false);
							String[] strings=(string.substring(1)).split("\\|");
							userName=strings[0];
							userNickName=strings[1];
							setVisible(true);
							addWindowListener(new WindowAdapter() {});
							isResponse=true;
							new ListenThread().start();
							break;
						}
						
						case '!'://login failed
						{
							if(string.length()==1)
							{
								JOptionPane.showMessageDialog(null, new JLabel("Login failed.(Incorrect password)"), "Warning", JOptionPane.INFORMATION_MESSAGE);
								passwordField.setText("");
								btn_Login.setEnabled(false);
								btn_Register.setEnabled(false);
								isResponse=true;
								break;
							}
							if(string.charAt(1)=='E')
							{
								JOptionPane.showMessageDialog(null, new JLabel("Login failed.(Username doesn't exisited)"), "Warning", JOptionPane.INFORMATION_MESSAGE);
								passwordField.setText("");
								btn_Login.setEnabled(false);
								btn_Register.setEnabled(false);
								isResponse=true;
								break;
							}
							if(string.charAt(1)=='Q')
							{
								JOptionPane.showMessageDialog(null, new JLabel("Login failed.(Database query error)"), "Warning", JOptionPane.INFORMATION_MESSAGE);
								passwordField.setText("");
								btn_Login.setEnabled(false);
								btn_Register.setEnabled(false);
								isResponse=true;
								break;
							}
						}
					}
				}
			}
			catch (Exception E)
			{
				E.printStackTrace();
			}
		}
	}
	
	private void createLogPad()
	{
		logpad=new JFrame();
		logpad.setTitle("LogPad");
		logpad.setResizable(false);
		logpad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		logpad.setBounds(100, 100, 459, 218);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		logpad.setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JToolBar toolBar = new JToolBar();
		toolBar.setBorder(null);
		toolBar.setFloatable(false);
		contentPane.add(toolBar, BorderLayout.SOUTH);
		
		label_ServerStatus = new JLabel("Server Status:Not Connected");
		label_ServerStatus.setForeground(Color.RED);
		label_ServerStatus.setFont(new Font("Courier New", Font.BOLD, 14));
		toolBar.add(label_ServerStatus);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblUserName = new JLabel("User Name:");
		lblUserName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUserName.setFont(new Font("Consolas", Font.PLAIN, 18));
		lblUserName.setBounds(31, 25, 100, 22);
		panel.add(lblUserName);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword.setFont(new Font("Consolas", Font.PLAIN, 18));
		lblPassword.setBounds(31, 67, 100, 22);
		panel.add(lblPassword);
		
		textField_UserName = new JTextField();
		textField_UserName.setFont(new Font("Consolas", Font.PLAIN, 18));
		textField_UserName.setBounds(138, 25, 266, 21);
		panel.add(textField_UserName);
		textField_UserName.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Consolas", Font.PLAIN, 18));
		passwordField.setBounds(138, 67, 266, 21);
		panel.add(passwordField);
		
		btn_Register = new JButton("Register");
		btn_Register.setFont(new Font("Consolas", Font.PLAIN, 14));
		btn_Register.setBounds(77, 112, 103, 25);
		btn_Register.setEnabled(false);
		panel.add(btn_Register);
		
		btn_Login = new JButton("Login");
		btn_Login.setFont(new Font("Consolas", Font.PLAIN, 14));
		btn_Login.setBounds(263, 112, 97, 25);
		btn_Login.setEnabled(false);
		panel.add(btn_Login);
		
		textField_UserName.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyChar()>=KeyEvent.VK_0 && e.getKeyChar()<=KeyEvent.VK_9)
				{
					
				}
				else
				{
					if(e.getKeyChar()>='A' && e.getKeyChar()<='Z')
					{
						
					}
					else
					{
						if(e.getKeyChar()>='a' && e.getKeyChar()<='z')
						{
							
						}
						else
						{
							e.consume();
						}
					}
				}
				String pwd=new String(passwordField.getPassword());
				if(pwd.isEmpty() || textField_UserName.getText().isEmpty())
				{
					btn_Login.setEnabled(false);
					btn_Register.setEnabled(false);
				}
				else
				{
					btn_Login.setEnabled(true);
					btn_Register.setEnabled(true);
				}
				super.keyTyped(e);
			}
		});
		
		passwordField.addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				String pwd=new String(passwordField.getPassword());
				if(pwd.isEmpty() || textField_UserName.getText().isEmpty())
				{
					btn_Login.setEnabled(false);
					btn_Register.setEnabled(false);
				}
				else
				{
					btn_Login.setEnabled(true);
					btn_Register.setEnabled(true);
				}
				if(e.getKeyChar()==KeyEvent.VK_SPACE)
				{
					e.consume();
				}
				super.keyPressed(e);
			}
		});
		
		btn_Register.addActionListener(new ActionListener()
		{
			byte[] bytes;
			String string;
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String tempNickName=JOptionPane.showInputDialog(null, new JLabel("Set your nickname:"), "Register", JOptionPane.INFORMATION_MESSAGE);
				if(!tempNickName.isEmpty())
				{
					try
					{
						inputStream=socket.getInputStream();
						outputStream=socket.getOutputStream();
						int len;
						String pwd=new String(passwordField.getPassword());
						string="@"+textField_UserName.getText()+"|"+tempNickName+"|"+pwd;
						bytes=string.getBytes();
						len=bytes.length;
						outputStream.write(bytes, 0, len);
						outputStream.flush();
						bytes=new byte[1024];
						boolean isResponse=false;
						while(!isResponse)
						{
							len=inputStream.read(bytes);
							string=new String(bytes, 0, len);
							switch (string.charAt(0))
							{
								case '@'://success
								{
									JOptionPane.showMessageDialog(null, new JLabel("Register success."), "Notice", JOptionPane.INFORMATION_MESSAGE);
									isResponse=true;
									break;
								}

								case '!'://failed
								{
									JOptionPane.showMessageDialog(null, new JLabel("Register failed.(Account existed)"), "Warning", JOptionPane.INFORMATION_MESSAGE);
									isResponse=true;
									break;
								}
							}
						}
					}
					catch (Exception E)
					{
						E.printStackTrace();
					}
				}
			}
		});
		
		btn_Login.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new LoginThread().start();
			}
		});
		
		/*addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				try
				{
					System.out.println("closing");
					OutputStream outputStream=socket.getOutputStream();
					outputStream.write("~".getBytes());
					outputStream.flush();
					
					byte[] bytes=new byte[1024];
					InputStream inputStream=socket.getInputStream();
					int len=inputStream.read(bytes);
					String string=new String(bytes, 0, len);
					if(string.charAt(0)=='~');
				}
				catch (Exception E)
				{
					E.printStackTrace();
				}
			}
		});*/
		
		logpad.setVisible(true);
	}
	
	private void sendMessage(String string)
	{
		try
		{
			string="#*"+string;
			byte[] bytes=string.getBytes();
			outputStream2.write(bytes);
			outputStream2.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String string,String target)
	{
		try
		{
			string="#@"+target+"|"+string;
			byte[] bytes=string.getBytes();
			outputStream2.write(bytes);
			outputStream2.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args)
	{
		new Client();
	}
}
