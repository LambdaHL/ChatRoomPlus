package server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
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
		private Font font;
		private String fontName;
		private int fontStyle,fontSize;
		
		@Override
		public void run()//individual listen
		{
			try
			{
				while(!socket.isClosed())
				{
					if(!isLoggedout)
					{
						while(!bufferedReader.ready()) {sleep(500);}
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
										this.font=dbOperator.getFont(userName);
										this.fontName=dbOperator.getFontName(userName);
										this.fontStyle=dbOperator.getFontStyle(userName);
										this.fontSize=dbOperator.getFontSize(userName);
										icon=dbOperator.getUserIcon(userName);
										printWriter.println(this.userName);
										printWriter.println(this.userNickName);
										printWriter.println(this.icon);
										printWriter.println(this.fontName);
										printWriter.println(this.fontStyle);
										printWriter.println(this.fontSize);
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
							printWriter.println("#Confirm");
							printWriter.flush();
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
							textArea_Conversation.append("\r\n");
							updateUserList();
							sendToAll(time, source, message);
							continue;
						}
						
						if(string.equals("#MessageTo"))
						{
							String time=bufferedReader.readLine();
							String source=bufferedReader.readLine();
							String target=bufferedReader.readLine();
							String message=bufferedReader.readLine();
							updateUserList();
							sendTo(time, source, target, message);
							//Unfinished:private chat
							
							continue;
						}
						
						if(string.equals("#Update Icon"))
						{
							String icon=bufferedReader.readLine();
							this.icon=icon;
							for(int i=0;i<userList.size();i++)
							{
								if(userList.get(i).name.equals(userName))
								{
									userList.get(i).icon=icon;
									userList.get(i).refresh();
								}
							}
							dbOperator.updateIcon(userName, icon);
							updateUserList();
							continue;
						}
						
						if(string.equals("#Update Nickname"))
						{
							String nickname=bufferedReader.readLine();
							userNickName=nickname;
							for(int i=0;i<userList.size();i++)
							{
								if(userList.get(i).name.equals(userName))
								{
									userList.get(i).nickName=nickname;
								}
							}
							dbOperator.updateNickName(userName, nickname);
							updateUserList();
							continue;
						}
						
						if(string.equals("#Update Password"))
						{
							String password=bufferedReader.readLine();
							dbOperator.updatePassword(userName, password);
							continue;
						}
						
						if(string.equals("#Update Font"))
						{
							fontName=bufferedReader.readLine();
							fontStyle=Integer.parseInt(bufferedReader.readLine());
							fontSize=Integer.parseInt(bufferedReader.readLine());
							for(int i=0;i<userList.size();i++)
							{
								if(userList.get(i).name.equals(userName))
								{
									userList.get(i).fontName=fontName;
									userList.get(i).fontStyle=fontStyle;
									userList.get(i).fontSize=fontSize;
									userList.get(i).font=new Font(fontName,fontStyle,fontSize);
								}
							}
							dbOperator.updateFont(userName, fontName, fontStyle, fontSize);
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
			User user=new User(userName, userNickName, icon, socket.getInetAddress().toString()+":"+socket.getPort(), socket, fontName, fontStyle, fontSize);
			return user;
		}
		
		private synchronized void updateUserList()
		{
			PrintWriter pWriter;
			BufferedReader bReader;
			try
			{
				sleep(10);
				list_UserList.setModel(new MyListModel<Object>(userList));
				list_UserList.setCellRenderer(new MyListCellRenderer());
				for(int j=0;j<userList.size();j++)
				{
					if(userList.get(j).socket.isClosed())
						continue;
					pWriter=new PrintWriter(userList.get(j).socket.getOutputStream());
					bReader=new BufferedReader(new InputStreamReader(userList.get(j).socket.getInputStream()));
					pWriter.println("#Update List");
					pWriter.println(userList.size()-1);
					for(int i=0;i<userList.size();i++)
					{
						if(userList.get(i).name.equals(userList.get(j).name))
							continue;
						pWriter.println(userList.get(i).name);
						pWriter.println(userList.get(i).nickName);
						pWriter.println(userList.get(i).icon);
						pWriter.println(userList.get(i).fontName);
						pWriter.println(userList.get(i).fontStyle);
						pWriter.println(userList.get(i).fontSize);
					}
					pWriter.flush();
				}
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
