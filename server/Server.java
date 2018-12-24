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
	private ArrayList<User> userList;
	
	public Server()
	{
		new DBOperator().reset();
		clients=new ArrayList<Client>();
		userList=new ArrayList<User>();
		dbOperator=new DBOperator();
		createGUI();
		new ServerAcceptThread().start();
	}
	
	private void createGUI()
	{
		try 
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		setTitle("Server");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1082, 549);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
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
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e) 
			{
				dbOperator.reset();
				super.windowClosing(e);
			}
		});
		
		setVisible(true);
	}

	class ServerAcceptThread extends Thread
	{
		private ServerSocket serverSocket;

		@Override
		public void run()
		{
			try
			{
				serverSocket=new ServerSocket(2018);
				textArea_Log.append("Server Accepting...\n");
				while(true)
				{
					Socket socket=serverSocket.accept();
					textArea_Log.append("Client("+socket.getInetAddress()+":"+socket.getPort()+") Connected\n");
					Client client=new Client(socket);
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
		private Socket socket;
		private String userName,userNickName,icon,ipString,terminateString;
		private PrintWriter printWriter;
		private BufferedReader bufferedReader;
		private String string;
		private boolean isLogged;
		private Date date;
		private SimpleDateFormat simpleDateFormat;
		private boolean isLoggedout;
		private User user;
		
		@Override
		public void run()//individual listen
		{
			try
			{
				while(!socket.isClosed())
				{
					if(!isLoggedout)
					{
						string=bufferedReader.readLine();
						if(string.equals("#Register"))
						{
							String userName=bufferedReader.readLine();
							String userNickName=bufferedReader.readLine();
							String password=bufferedReader.readLine();
							switch(dbOperator.register(userName, userNickName, password))
							{
								case DBOperator.REG_USERNAME_SUCCESS:
								{
									this.userName=userName;
									this.userNickName=userNickName;
									printWriter.println("#Register Success");
									printWriter.flush();
									textArea_Log.append("UserID:" + userName + " IP:" + socket.getInetAddress() + ":" + socket.getPort() + " registered\n");
									terminateString=new String("IP:" + socket.getInetAddress() + ":" + socket.getPort() + " disconnected\n");
									break;
								}

								case DBOperator.REG_USERNAME_EXISITED:
								{
									printWriter.println("#User Existed");
									printWriter.flush();
									textArea_Log.append("IP:" + socket.getInetAddress() + ":" + socket.getPort() + " registered failed\n");
									terminateString=new String("IP:" + socket.getInetAddress() + ":" + socket.getPort() + " disconnected\n");
									break;
								}
							}
							continue;
						}

						if(string.equals("#Login"))
						{
							String userName=bufferedReader.readLine();
							String password=bufferedReader.readLine();
							terminateString=new String("IP:" + socket.getInetAddress() + ":" + socket.getPort() + " disconnected\n");
							if(!dbOperator.isLogged(userName))
							{
								switch(dbOperator.login(userName, password))
								{
									case DBOperator.LOGIN_PWD_RIGHT:
									{
										printWriter.println("#Login Success");
										isLogged=true;
										isLoggedout=false;
										this.userName=userName;
										this.userNickName=dbOperator.getUserNickName(userName);
										icon=dbOperator.getUserIcon(userName);
										printWriter.println(this.userName);
										printWriter.println(this.userNickName);
										printWriter.flush();
										userList.add(this.getUser());
										this.user=this.getUser();
										
										updateUserList();
										break;
									}

									case DBOperator.LOGIN_PWD_WRONG:
									{
										printWriter.println("#Login Failed");
										printWriter.flush();
										isLogged=false;
										break;
									}

									case DBOperator.LOGIN_USERNAME_NOT_EXISITED:
									{
										printWriter.println("#User Not Existed");
										printWriter.flush();
										isLogged=false;
										break;
									}

									case DBOperator.LOGIN_SQLERROR:
									{
										System.out.println("Database error");
										break;
									}
								}
							}
							else//user has been logged
							{
								printWriter.println("#User Logged");
								printWriter.flush();
							}
							continue;
						}
						
						if(string.equals("#Logout"))
						{
							dbOperator.logout(userName);
							terminateString=new String("IP:" + socket.getInetAddress() + ":" + socket.getPort() + " disconnected\n");
							socket.shutdownInput();
							socket.shutdownOutput();
							socket.close();
							isLogged=false;
							isLoggedout=true;
							userList.remove(getIndex(userName));
							updateUserList();
							continue;
						}
						
						if(string.equals("#Message"))
						{
							String time=bufferedReader.readLine();
							String source=bufferedReader.readLine();
							String message=bufferedReader.readLine();
							textArea_Conversation.append(time+"\r\n");
							textArea_Conversation.append(source+"\r\n");
							textArea_Conversation.append(message+"\r\n");
							sendToAll(time, source, message);
							//Unfinished:textpane
							
							continue;
						}
						
						if(string.equals("#MessageTo"))
						{
							String time=bufferedReader.readLine();
							String source=bufferedReader.readLine();
							String target=bufferedReader.readLine();
							String message=bufferedReader.readLine();
							sendTo(time, source, target, message);
							//Unfinished:private chat
							
							continue;
						}
					}
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			textArea_Log.append(terminateString);
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
		
		public Client(Socket socket)
		{
			this.socket=socket;
			isLogged=false;
			isLoggedout=false;
			ipString="IP:" + socket.getInetAddress() + ":" + socket.getPort();
			try
			{
				printWriter=new PrintWriter(socket.getOutputStream());
				bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
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
		
		private void updateUserList()
		{
			PrintWriter pWriter;
			BufferedReader bReader;
			try
			{
				sleep(10);
				list_UserList.setModel(new MyListModel<Object>(userList));
				list_UserList.setCellRenderer(new MyListCellRenderer());
//				Traversal all user and get the socket of them
//				Then traversal user list and send information to current socket
				System.out.println("\ncurrent client:"+userName);
				for(int j=0;j<userList.size();j++)
				{
					System.out.println("j:"+j+"  user:"+userList.get(j).name);
					if(userList.get(j).socket.isClosed())
						continue;
					pWriter=new PrintWriter(userList.get(j).socket.getOutputStream());
					bReader=new BufferedReader(new InputStreamReader(userList.get(j).socket.getInputStream()));
					for(int i=0;i<userList.size();i++)
					{
						System.out.println("	i:"+i+"  user:"+userList.get(i).name);
						if(userList.get(i).name.equals(userList.get(j).name))
						{
							System.out.println("	continued");
							continue;
						}
						pWriter.println("#Update List");
						pWriter.println(userList.size()-1);
						pWriter.println(userList.get(i).name);
						pWriter.println(userList.get(i).nickName);
						String iconPath=userList.get(i).icon;
						File iconFile=new File(iconPath);
						pWriter.println(iconFile.getName());
						pWriter.flush();
						DataInputStream dataInputStream=new DataInputStream(new FileInputStream(iconFile));
						DataOutputStream dataOutputStrem=new DataOutputStream(userList.get(i).socket.getOutputStream());
						byte[] bytes=new byte[2048];
						int length=dataInputStream.read(bytes);
						dataOutputStrem.write(bytes, 0, length);
						dataOutputStrem.flush();
						System.out.println();
					}
				}
				System.out.println("Update complete====================");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		private void sendToAll(String time,String source,String message)
		{
			try
			{
				for(int i=0;i<userList.size();i++)
				{
					Socket socket=userList.get(i).socket;
					PrintWriter pWriter=new PrintWriter(socket.getOutputStream());
					pWriter.println("#Message");
					pWriter.println(time);
					pWriter.println(source);
					pWriter.println(message);
					pWriter.flush();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		private void sendTo(String time,String source,String target,String message)
		{
			try
			{
				for(int i=0;i<userList.size();i++)
				{
					if(userList.get(i).name.equals(target))
					{
						Socket socket=userList.get(i).socket;
						PrintWriter pWriter=new PrintWriter(socket.getOutputStream());
						pWriter.println("#Message From");
						pWriter.println(time);
						pWriter.println(source);
						pWriter.println(message);
						pWriter.flush();
						break;
					}
				}
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
