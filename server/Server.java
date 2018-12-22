package server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.ArrayList;
import java.net.*;
import java.util.Date; 

public class Server extends JFrame
{
	private JPanel contentPane;
	private JScrollPane scrollPane_List, scrollPane_TextArea, scrollPane_TextAreaLog;
	private JList<Object> list_UserList;
	private JTextArea textArea_Conversation, textArea_Log;
	private ArrayList<Client> clients;
	private DBOperator dbOperator;
	private Date date;
	private SimpleDateFormat simpleDateFormat;
	private ArrayList<User> userList;
	
	public Server()
	{
		clients=new ArrayList<Client>();
		userList=new ArrayList<User>();
		dbOperator=new DBOperator();
		date=new Date();
		simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		createGUI();
		new ServerAcceptThread().start();
	}
	
	private void createGUI()
	{
		setTitle("Server");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1082, 549);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				OutputStream outputStream;
				for(int i=0;i<clients.size();i++)
				{
					try 
					{
//						outputStream=clients.get(i).getSocket().getOutputStream();
//						outputStream.write("~".getBytes());
//						outputStream.flush();
//						clients.get(i).getSocket().shutdownInput();
//						clients.get(i).getSocket().shutdownOutput();
//						clients.get(i).getSocket().close();
					} 
					catch (Exception E) 
					{
						E.printStackTrace();
					}
					
				}
				super.windowClosing(e);
			}
		});
		
		scrollPane_List = new JScrollPane();
		scrollPane_List.setBounds(761, 10, 295, 489);
		contentPane.add(scrollPane_List);
		
		list_UserList = new JList<>();
		scrollPane_List.setViewportView(list_UserList);
		
		scrollPane_TextArea = new JScrollPane();
		scrollPane_TextArea.setBounds(378, 10, 361, 489);
		scrollPane_TextArea.setAutoscrolls(true);
		contentPane.add(scrollPane_TextArea);
		
		textArea_Conversation = new JTextArea();
		textArea_Conversation.setEditable(false);
		scrollPane_TextArea.setViewportView(textArea_Conversation);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(749, 10, 2, 489);
		contentPane.add(separator);
		
		scrollPane_TextAreaLog = new JScrollPane();
		scrollPane_TextAreaLog.setBounds(10, 10, 361, 489);
		scrollPane_TextAreaLog.setAutoscrolls(true);
		contentPane.add(scrollPane_TextAreaLog);
		
		textArea_Log = new JTextArea();
		textArea_Log.setEditable(false);
		scrollPane_TextAreaLog.setViewportView(textArea_Log);
		
		setVisible(true);
	}

	class ServerAcceptThread extends Thread
	{
		private ServerSocket serverSocket,serverSocket2;

		@Override
		public void run()
		{
			try
			{
				serverSocket=new ServerSocket(2018);
				serverSocket2=new ServerSocket(2019);
				textArea_Log.append("Server Accepting...\n");
				while(true)
				{
					Socket socket=serverSocket.accept();
					Socket socket2=serverSocket2.accept();
					textArea_Log.append("Client("+socket.getInetAddress()+":"+socket.getPort()+") Connected\n");
					Client client=new Client(socket,socket2);
					clients.add(client);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	class Client extends Thread
	{
		private Socket socket,socket2;
		private String userName;
		private String userNickName;
		private String icon;
		private String ipString;
		private InputStream inputStream;
		private OutputStream outputStream;
		private byte[] bytes;
		private String string;
		private boolean isLogged;
		private DBOperator dbOperator;
		private Date date;
		private SimpleDateFormat simpleDateFormat;
		private int indexInUserList;
		private boolean isLoggedout;
		
		@Override
		public void run()
		{
			try
			{
				inputStream=socket.getInputStream();
				outputStream=socket.getOutputStream();
				while(isLoggedout==false)
				{
					int len;
					if(inputStream.available()!=0)
					{
						bytes=new byte[10240];
						len=inputStream.read(bytes);
						string=new String(bytes, 0, len);
						switch (string.charAt(0))
						{
							case '@':// register
							{
								String temp = new String(bytes, 0, len).substring(1);
								String[] strings = temp.split("\\|");
								if (dbOperator.register(strings[0], strings[1],	strings[2]) == DBOperator.REG_USERNAME_SUCCESS) 
								{
									this.userName = strings[0];
									this.userNickName = strings[1];
									outputStream.write("@".getBytes());
									outputStream.flush();
									textArea_Log.append("UserID:" + userName + " IP:" + socket.getInetAddress() + ":" + socket.getPort() + " registered\n");
								} 
								else 
								{
									outputStream.write("!".getBytes());
									outputStream.flush();
									textArea_Log.append("IP:" + socket.getInetAddress() + ":" + socket.getPort() + " registered failed\n");
								}
								break;
							}

							case '>':// login
							{
								String temp = new String(bytes, 0, len).substring(1);
								String[] strings =temp.split("\\|");
								switch (dbOperator.login(strings[0], strings[1])) 
								{
									case DBOperator.LOGIN_PWD_RIGHT: 
									{
										isLogged = true;
										isLoggedout = false;
										userName = strings[0];
										userNickName = dbOperator.getUserNickName(userName);
										icon = dbOperator.getUserIcon(userName);
										outputStream.write(("^" + userName + "|" + userNickName).getBytes());
										outputStream.flush();
										userList.add(this.getUser());
										indexInUserList = userList.size()-1;
										//new ServerUpdateThread(userName).start();
										textArea_Log.append("UserID:" + userName + " IP:" + socket.getInetAddress() + ":" + socket.getPort() + " logged in\n");
										new MessageThread(socket, socket2).start();
										//UpdateUserList();
										list_UserList.setModel(new MyListModel<Object>(userList));
										list_UserList.setCellRenderer(new MyListCellRenderer());
										break;
									}

									case DBOperator.LOGIN_PWD_WRONG:
									{
										isLogged = false;
										outputStream.write("!".getBytes());
										outputStream.flush();
										break;
									}
									
									case DBOperator.LOGIN_USERNAME_NOT_EXISITED:
									{
										isLogged = false;
										outputStream.write("!E".getBytes());
										outputStream.flush();
										break;
									}
									
									case DBOperator.LOGIN_SQLERROR:
									{
										isLogged = false;
										outputStream.write("!Q".getBytes());
										outputStream.flush();
										break;
									}
								}
								break;
							}

							case '$':// icon file
							{
								String fileIconPath = System.getProperty("user.dir") + "\\Server\\Icons";
								File fileIcon = new File(fileIconPath, new String(bytes));
								DataOutputStream dataOutputStream;
								byte[] tempBytes = new byte[4096];
								int tempLen;
								tempLen = inputStream.read(tempBytes);
								fileIcon.delete();
								fileIcon.createNewFile();
								dataOutputStream = new DataOutputStream(new FileOutputStream(fileIcon));
								dataOutputStream.write(tempBytes, 0, tempLen);
								dataOutputStream.close();
								this.icon = fileIcon.getName();
								dbOperator.updateIcon(userName, icon);
								break;
							}
							
							case '~'://client close
							{
								outputStream.write("~".getBytes());
								socket.shutdownInput();
								socket.shutdownOutput();
								isLoggedout=true;
								isLogged=false;
								socket.close();
							}
						}
					}
					if(socket.isClosed())
					{
						userList.remove(getIndex(userName));
						list_UserList.setModel(new MyListModel<Object>(userList));
						list_UserList.setCellRenderer(new MyListCellRenderer());
						textArea_Log.append("UserID:" + userName + " " + ipString + " logged out\n" );
					}
					
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		private void UpdateUserList()
		{
			int k;
			try
			{
				//Update Server User List
				list_UserList.setModel(new MyListModel<Object>(userList));
				list_UserList.setCellRenderer(new MyListCellRenderer());
				if(userList.size()>0)
				{
					//Update Client User List
					for(k=0;k<userList.size();k++)//get the current location
					{
						if(userList.get(k).name.equals(userName))
						{
							break;
						}
					}
					for(int i=0;i<userList.size();i++)//Traversal userlist
					{
						/*//send current user info
						if(i==k && i!=0)//skip when traversal to current user*******************************
						{
							System.out.println("i:"+i);
							System.out.println("k:"+k);
							continue;
						}*/
						String onlineListString="&"+userList.get(k).nickName + "|" + userList.size();
						//i always begin at 0
						
						System.out.println("onlineListString:"+onlineListString);
						byte[] bytes=onlineListString.getBytes();
						outputStream=userList.get(k).socket.getOutputStream();
						outputStream.write(bytes, 0, bytes.length);
						outputStream.flush();
						System.out.println("onlineListString send");
						
						/*//wait for a response that client had received previous data
						inputStream=userList.get(k).socket.getInputStream();
						int len=inputStream.read(bytes);//read the char "+" that client has return
						
						String temp=new String(bytes, 0, len);
						System.out.println("temp:"+temp);
						DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
						File iconFile=new File(userList.get(k).icon);
						DataInputStream dataInputStream=new DataInputStream(new FileInputStream(iconFile));
						if(temp.charAt(0)=='+')//client's response confirmed
						{
							System.out.println("+ recieved");
							bytes=new byte[10240];
							len=dataInputStream.read(bytes);
							outputStream.write(bytes, 0, len);//send itself file
							outputStream.flush();
						}//file sent
						*/
						//send all user info
						System.out.println("userList.size:"+userList.size());
						for(int j=0;j<userList.size();j++)
						{
							System.out.println("Entered No."+ j +" circulation");
							String temp=userList.get(j).name + "|" + userList.get(j).nickName;
							outputStream.write(temp.getBytes());
							outputStream.flush();
														
							bytes=new byte[1024];
							System.out.println("current username:"+userName);
							System.out.println("available:"+inputStream.available());
							while(inputStream.available()==0) {}
							int len=inputStream.read(bytes);	//block
							temp=new String(bytes, 0 ,len);
							System.out.println("temp:"+temp);
							if((new String(bytes, 0, len)).charAt(0)=='&')
							{							
								//send icon
								DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
								File iconFile=new File(userList.get(i).icon);
								DataInputStream dataInputStream=new DataInputStream(new FileInputStream(iconFile));
								
								len=0;
								len=dataInputStream.read(bytes);
								dataOutputStream.write(bytes, 0, len);
								dataOutputStream.flush();
							}
						}
						System.out.println("circulation end\n");
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		private int getIndex(String name)
		{
			for(int i=0;i<userList.size();i++)
			{
				if(userList.get(i).name.equals(name))
					return i;
			}
			return 0;
		}
		
		public Client(Socket socket,Socket socket2)
		{
			this.socket=socket;
			this.socket2=socket2;
			dbOperator=new DBOperator();
			date=new Date();
			simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			isLogged=false;
			isLoggedout=false;
			ipString="IP:" + socket.getInetAddress() + ":" + socket.getPort();
			this.start();
		}

		public String getUserName()
		{
			return userName;
		}

		public String getUserNickName()
		{
			return userNickName;
		}

		public String getIcon()
		{
			return icon;
		}

		public Socket getSocket()
		{
			return socket;
		}

		public boolean isLogged()
		{
			return isLogged;
		}

		public User getUser()
		{
			User user=new User(userName, userNickName, icon, socket.getInetAddress().toString()+":"+socket.getPort(), socket);
			return user;
		}
	}
	
	class MessageThread extends Thread
	{
		private Socket socket,socket2;
		private InputStream inputstream;
		private OutputStream outputStream;
		private byte[] bytes=new byte[1024];
		private String string;
		private String[] strings;
		private boolean isConversationEnd;
		
		@Override
		public void run()
		{
			try
			{
				while(isConversationEnd==false)
				{
					if(inputstream.available()!=0) 
					{
						int len=inputstream.read(bytes);
						string=new String(bytes, 0, len);
						if(string.charAt(0)=='#')
						{
							string=string.substring(1);
							switch (string.charAt(0))
							{
								case '*'://to all
								{
									string=string.substring(1);
									for(int i=0;i<userList.size();i++)//traversal all users
									{
										
									}
									
								}
								
								case '@'://to someone
								{
									string=string.substring(1);
									strings=string.split("\\|");
									
								}
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
		
		public MessageThread(Socket socket,Socket socket2)
		{
			this.socket=socket;
			this.socket2=socket2;
			isConversationEnd=false;
			try
			{
				inputstream=socket2.getInputStream();
				outputStream=socket2.getOutputStream();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Server();
	}
}
