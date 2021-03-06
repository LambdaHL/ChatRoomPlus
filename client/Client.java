package client;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;

public class Client extends JFrame
{
	private JPanel contentPane;
	private JTextField textField_UserName;
	private JPasswordField passwordField;
	private JLabel label_ServerStatus,label_Top;
	private Socket socket;
	private JList<Object> list_UserList,list_Friends;
	private JTextPane textPane;
	private JTextField textField;
	private JToggleButton tglbtn_Bold, tglbtn_Italic;
	private JButton btn_Register, btn_Login;
	private JComboBox comboBox_FontName, comboBox_FontSize;
	private JButton btn_Config, btn_Enter, btn_Color, btn_AddFriend, btn_DeleteFriend;
	private JFrame logpad;
	private String userName,userNickName,password,icon,fontName;
	private int fontStyle,fontSize;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;
	private Font font;
	private JPopupMenu popupMenu;
	private JMenuItem mntmChangePassword, mntmShowRecoord, mntmChangeNickName;
	private JMenu mnChangeIcon;
	private JMenuItem mntmChangeIcon0,mntmChangeIcon1,mntmChangeIcon2,mntmChangeIcon3,mntmChangeIcon4,mntmChangeIcon5;
	private ArrayList<User> userList;
	private Color color;
	private ArrayList<PrivateChat> pChatList;
	private DataOutputStream dataOutputStream;
	
	public Client()
	{
		createLogPad();
		createClientGUI();
		userList=new ArrayList<>();
		pChatList=new ArrayList<>();
		new ConnectToServerThread().start();
	}
	
	class ConnectToServerThread extends Thread
	{
		@Override
		public void run()
		{
			while(true)
			{
				try
				{
					socket=new Socket("127.0.0.1", 2018);
					printWriter=new PrintWriter(socket.getOutputStream());
					bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
					dataOutputStream=new DataOutputStream(socket.getOutputStream());
					label_ServerStatus.setText("Server Status:Connected");
					label_ServerStatus.setForeground(Color.GREEN);
					break;
				}
				catch (Exception e)
				{
					label_ServerStatus.setText("Server Status:Not Connected");
					label_ServerStatus.setForeground(Color.RED);
					e.printStackTrace();
				}
			}
		}
	}
	
	class ListenThread extends Thread
	{
		@Override
		public void run()
		{
			String string;
			synchronized (this)
			{
				try
				{
					while(!socket.isClosed())
					{
						while(!bufferedReader.ready()) {sleep(200);}
						string=bufferedReader.readLine();
						if(string!=null)
						{
							if(string.equals("#Update List"))
							{
								int size=Integer.parseInt(bufferedReader.readLine());
								userList.clear();
								for(int i=0;i<size;i++)
								{
									String name=bufferedReader.readLine();
									String nickName=bufferedReader.readLine();
									String icon=bufferedReader.readLine();
									String fontName=bufferedReader.readLine();
									int fontStyle=Integer.parseInt(bufferedReader.readLine());
									int fontSize=Integer.parseInt(bufferedReader.readLine());
									int rgb=Integer.parseInt(bufferedReader.readLine());
									Color color=new Color(rgb);
									User user=new User(name, nickName, icon, fontName, fontStyle, fontSize, color);
									userList.add(user);
								}
								if(size==0)
									userList=new ArrayList<>();
								list_UserList.setModel(new MyListModel<Object>(userList));
								list_UserList.setCellRenderer(new MyListCellRenderer());
								continue;
							}

							if(string.equals("#Message"))//received group message
							{
								String time=bufferedReader.readLine();
								String source=bufferedReader.readLine();
								String message=bufferedReader.readLine();
								insert(time, source, message);
								continue;
							}

							if(string.equals("#Message From"))
							{
								String time=bufferedReader.readLine();
								String source=bufferedReader.readLine();
								String message=bufferedReader.readLine();
								if(pChatList.size()==0)
								{
									PrivateChat pChat=new PrivateChat(source);
									pChatList.add(pChat);
									pChat.appendMessage(time, source, message);
									continue;
								}
								for(int i=0;i<pChatList.size();i++)
								{
									if(pChatList.get(i).targetName.equals(source))
									{
										pChatList.get(i).appendMessage(time, source, message);
									}
									else
									{
										PrivateChat pChat=new PrivateChat(source);
										pChatList.add(pChat);
										pChat.appendMessage(time, source, message);
									}
								}
								continue;
							}
							
							if(string.equals("#Group Record"))
							{
								int size=Integer.parseInt(bufferedReader.readLine());
								ArrayList<String> strings=new ArrayList<String>();
								for(int i=0;i<size;i++)
								{
									strings.add(bufferedReader.readLine());
								}
								new MessageRecordFrame(strings);
								continue;
							}
							
							if(string.equals("#File"))
							{
								String fileName=bufferedReader.readLine();
								String sourceName=bufferedReader.readLine();
								byte[] bytes=new byte[2048];
								DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
								int len=dataInputStream.read(bytes);
								JFileChooser jFileChooser=new JFileChooser();
								jFileChooser.setMultiSelectionEnabled(false);
								jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								if(JOptionPane.showConfirmDialog(null, new JLabel("Save or ignore?"), sourceName+" send you a file", JOptionPane.YES_NO_OPTION)==JOptionPane.OK_OPTION)
								{
									if(jFileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
									{
										File file=new File(jFileChooser.getSelectedFile(), fileName);
										if(!file.exists())
											file.createNewFile();
										DataOutputStream dataOutputStream=new DataOutputStream(new FileOutputStream(file));
										dataOutputStream.write(bytes, 0, len);
										dataOutputStream.close();
									}
								}
								else
								{
									dataInputStream.skip(dataInputStream.available());
								}
								continue;
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
	}
	
	class FileThread extends Thread
	{
		String targetName;
		File file;
		
		public FileThread(File file,String targetName) 
		{
			this.file=file;
			this.targetName=targetName;
		}
		
		@Override
		public void run()
		{
			try
			{
				printWriter.println("#Send File");
				printWriter.flush();
				printWriter.println(targetName);
				printWriter.println(file.getName());
				DataInputStream dataInputStream=new DataInputStream(new FileInputStream(file));
				byte[] bytes=new byte[2048];
				int len=dataInputStream.read(bytes);
				printWriter.println(len+"\r");
				printWriter.flush();
				sleep(250);
				dataOutputStream.write(bytes,0,len);
				dataOutputStream.flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void insert(String time,String source,String message) 
	{
		SimpleAttributeSet simpleAttributeSet=new SimpleAttributeSet();
		SimpleAttributeSet infoAttributeSet=new SimpleAttributeSet();
		StyleConstants.setFontFamily(infoAttributeSet, "Consolas");
		StyleConstants.setAlignment(infoAttributeSet, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_RIGHT);
		Document document=textPane.getDocument();
		if(source.equals(userName))
		{
			StyleConstants.setFontFamily(simpleAttributeSet, fontName);
			StyleConstants.setFontSize(simpleAttributeSet, fontSize);
			switch (fontStyle)
			{
				case Font.PLAIN:
				{
					StyleConstants.setBold(simpleAttributeSet, false);
					StyleConstants.setItalic(simpleAttributeSet, false);
					break;
				}
				case Font.BOLD:
				{
					StyleConstants.setBold(simpleAttributeSet, true);
					StyleConstants.setItalic(simpleAttributeSet, false);
					break;
				}
				case Font.ITALIC:
				{
					StyleConstants.setBold(simpleAttributeSet, false);
					StyleConstants.setItalic(simpleAttributeSet, true);
					break;
				}
				case Font.BOLD+Font.ITALIC:
				{
					StyleConstants.setBold(simpleAttributeSet, true);
					StyleConstants.setItalic(simpleAttributeSet, true);
					break;
				}
			}
			StyleConstants.setForeground(simpleAttributeSet, color);
			try
			{
				int length=document.getLength();
				StyledDocument styledDocument=textPane.getStyledDocument();
				document.insertString(length, time+"\r\n", infoAttributeSet);
				styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
				length=document.getLength();
				document.insertString(length, source+"\r\n", infoAttributeSet);
				styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
				length=document.getLength();				
				document.insertString(length, message+"\r\n", simpleAttributeSet);
				styledDocument.setParagraphAttributes(length, document.getLength(), simpleAttributeSet, false);
				length=document.getLength();
				document.insertString(length, "\r\n", infoAttributeSet);
				styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
				textPane.setCaretPosition(textPane.getDocument().getLength());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return;
		}
		StyleConstants.setAlignment(infoAttributeSet, StyleConstants.ALIGN_LEFT);
		StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_LEFT);
		for(int i=0;i<userList.size();i++)
		{
			if(userList.get(i).name.equals(source))
			{
				StyleConstants.setFontFamily(simpleAttributeSet, userList.get(i).fontName);
				StyleConstants.setFontSize(simpleAttributeSet, userList.get(i).fontSize);
				StyleConstants.setFontFamily(infoAttributeSet, "Consolas");
				switch (userList.get(i).fontStyle)
				{
					case Font.PLAIN:
					{
						StyleConstants.setBold(simpleAttributeSet, false);
						StyleConstants.setItalic(simpleAttributeSet, false);
						break;
					}
					case Font.BOLD:
					{
						StyleConstants.setBold(simpleAttributeSet, true);
						StyleConstants.setItalic(simpleAttributeSet, false);
						break;
					}
					case Font.ITALIC:
					{
						StyleConstants.setBold(simpleAttributeSet, false);
						StyleConstants.setItalic(simpleAttributeSet, true);
						break;
					}
					case Font.BOLD+Font.ITALIC:
					{
						StyleConstants.setBold(simpleAttributeSet, true);
						StyleConstants.setItalic(simpleAttributeSet, true);
						break;
					}
				}
				StyleConstants.setForeground(simpleAttributeSet, userList.get(i).color);
				try
				{
					int length=document.getLength();
					StyledDocument styledDocument=textPane.getStyledDocument();
					document.insertString(length, time+"\r\n", infoAttributeSet);
					styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
					length=document.getLength();
					document.insertString(length, source+"\r\n", infoAttributeSet);
					styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
					length=document.getLength();				
					document.insertString(length, message+"\r\n", simpleAttributeSet);
					styledDocument.setParagraphAttributes(length, document.getLength(), simpleAttributeSet, false);
					length=document.getLength();
					document.insertString(length, "\r\n", infoAttributeSet);
					styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
 	class PrivateChat extends JFrame
	{
		private JScrollPane scrollPane;
		private JLabel label_Me,label_Target;
		private JToggleButton tglbtn_Bold,tglbtn_Italic;
		private JButton button_Color,button_SendFile,button_Enter;
		private JComboBox comboBox_Font,comboBox_Size;	
		private JTextPane textPane;
		private JTextField textField;
		private JPanel contentPane;
		String targetName;
		
		private void updateFontBar()
		{
			textField.setForeground(color);
			textField.setFont(font);
			switch (fontSize)
			{
				case 14:
				{
					comboBox_Size.setSelectedIndex(0);
					break;
				}
				case 16:
				{
					comboBox_Size.setSelectedIndex(1);
					break;
				}
				case 18:
				{
					comboBox_Size.setSelectedIndex(2);
					break;
				}
				case 20:
				{
					comboBox_Size.setSelectedIndex(3);
					break;
				}
				case 22:
				{
					comboBox_Size.setSelectedIndex(4);
					break;
				}
				case 24:
				{
					comboBox_Size.setSelectedIndex(5);
					break;
				}
			}
			switch (fontStyle)
			{
				case Font.PLAIN:
				{
					tglbtn_Bold.setSelected(false);
					tglbtn_Italic.setSelected(false);
					break;
				}
				case Font.BOLD:
				{
					tglbtn_Bold.setSelected(true);
					break;
				}
				case Font.ITALIC:
				{
					tglbtn_Italic.setSelected(true);
					break;
				}
				case Font.BOLD+Font.ITALIC:
				{
					tglbtn_Bold.setSelected(true);
					tglbtn_Italic.setSelected(true);
					break;
				}
			}
			switch (fontName)
			{
				case "Microsoft YaHei":
				{
					comboBox_Font.setSelectedIndex(0);
					break;
				}
				case "Consolas":
				{
					comboBox_Font.setSelectedIndex(1);
					break;
				}
				case "Courier New":
				{
					comboBox_Font.setSelectedIndex(2);
					break;
				}
			}
		}
		
		public void setFontBold()
		{
			tglbtn_Bold.doClick();
		}
		
		public void setFontItalic()
		{
			tglbtn_Italic.doClick();
		}
		
		public void setFont()
		{
			textField.setFont(font);
		}
		
		public void setColor()
		{
			textField.setForeground(color);
		}
		
		public void appendMessage(String time,String source,String message)
		{
			SimpleAttributeSet simpleAttributeSet=new SimpleAttributeSet();
			SimpleAttributeSet infoAttributeSet=new SimpleAttributeSet();
			StyleConstants.setFontFamily(infoAttributeSet, "Consolas");
			StyleConstants.setAlignment(infoAttributeSet, StyleConstants.ALIGN_RIGHT);
			StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_RIGHT);
			Document document=textPane.getDocument();
			if(source.equals(userName))
			{
				StyleConstants.setFontFamily(simpleAttributeSet, fontName);
				StyleConstants.setFontSize(simpleAttributeSet, fontSize);
				switch (fontStyle)
				{
					case Font.PLAIN:
					{
						StyleConstants.setBold(simpleAttributeSet, false);
						StyleConstants.setItalic(simpleAttributeSet, false);
						break;
					}
					case Font.BOLD:
					{
						StyleConstants.setBold(simpleAttributeSet, true);
						StyleConstants.setItalic(simpleAttributeSet, false);
						break;
					}
					case Font.ITALIC:
					{
						StyleConstants.setBold(simpleAttributeSet, false);
						StyleConstants.setItalic(simpleAttributeSet, true);
						break;
					}
					case Font.BOLD+Font.ITALIC:
					{
						StyleConstants.setBold(simpleAttributeSet, true);
						StyleConstants.setItalic(simpleAttributeSet, true);
						break;
					}
				}
				StyleConstants.setForeground(simpleAttributeSet, color);
				try
				{
					int length=document.getLength();
					StyledDocument styledDocument=textPane.getStyledDocument();
					document.insertString(length, time+"\r\n", infoAttributeSet);
					styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
					length=document.getLength();
					document.insertString(length, source+"\r\n", infoAttributeSet);
					styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
					length=document.getLength();				
					document.insertString(length, message+"\r\n", simpleAttributeSet);
					styledDocument.setParagraphAttributes(length, document.getLength(), simpleAttributeSet, false);
					length=document.getLength();
					document.insertString(length, "\r\n", infoAttributeSet);
					styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
					textPane.setCaretPosition(textPane.getDocument().getLength());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return;
			}
			StyleConstants.setAlignment(infoAttributeSet, StyleConstants.ALIGN_LEFT);
			StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_LEFT);
			for(int i=0;i<userList.size();i++)
			{
				if(userList.get(i).name.equals(source))
				{
					StyleConstants.setFontFamily(simpleAttributeSet, userList.get(i).fontName);
					StyleConstants.setFontSize(simpleAttributeSet, userList.get(i).fontSize);				
					StyleConstants.setFontFamily(infoAttributeSet, "Consolas");
					switch (userList.get(i).fontStyle)
					{
						case Font.PLAIN:
						{
							StyleConstants.setBold(simpleAttributeSet, false);
							StyleConstants.setItalic(simpleAttributeSet, false);
							break;
						}
						case Font.BOLD:
						{
							StyleConstants.setBold(simpleAttributeSet, true);
							StyleConstants.setItalic(simpleAttributeSet, false);
							break;
						}
						case Font.ITALIC:
						{
							StyleConstants.setBold(simpleAttributeSet, false);
							StyleConstants.setItalic(simpleAttributeSet, true);
							break;
						}
						case Font.BOLD+Font.ITALIC:
						{
							StyleConstants.setBold(simpleAttributeSet, true);
							StyleConstants.setItalic(simpleAttributeSet, true);
							break;
						}
					}
					StyleConstants.setForeground(simpleAttributeSet, userList.get(i).color);
					try
					{
						int length=document.getLength();
						StyledDocument styledDocument=textPane.getStyledDocument();
						document.insertString(length, time+"\r\n", infoAttributeSet);
						styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
						length=document.getLength();
						document.insertString(length, source+"\r\n", infoAttributeSet);
						styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
						length=document.getLength();				
						document.insertString(length, message+"\r\n", simpleAttributeSet);
						styledDocument.setParagraphAttributes(length, document.getLength(), simpleAttributeSet, false);
						length=document.getLength();
						document.insertString(length, "\r\n", infoAttributeSet);
						styledDocument.setParagraphAttributes(length, document.getLength(), infoAttributeSet, true);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		public PrivateChat(String targetName)
		{
			try 
			{
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			this.targetName=targetName;
			
			setTitle("ChatRoomPlus   -Chating with "+targetName);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setResizable(false);
			setBounds(100, 100, 451, 574);
			
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			textField = new JTextField();
			textField.setBounds(0, 465, 385, 68);
			contentPane.add(textField);
			textField.setColumns(10);
			textField.setFont(font);
			
			button_Enter = new JButton("");
			button_Enter.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/enter.png")));
			button_Enter.setBounds(386, 465, 49, 68);
			button_Enter.setEnabled(false);
			contentPane.add(button_Enter);

			scrollPane = new JScrollPane();
			scrollPane.setAutoscrolls(true);
			scrollPane.setBounds(0, 56, 435, 368);
			contentPane.add(scrollPane);

			textPane = new JTextPane();
			scrollPane.setViewportView(textPane);
			textPane.setEditable(false);
			
			label_Me = new JLabel("");
			label_Me.setBounds(0, 0, 435, 25);
			contentPane.add(label_Me);

			label_Target = new JLabel("");
			label_Target.setBounds(0, 29, 435, 25);
			contentPane.add(label_Target);

			label_Me.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/"+icon+".png")));
			label_Me.setText(userName+"    "+userNickName);
			label_Me.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
			String targetIcon=new String();
			String targetNickName=new String();
			for(int i=0;i<userList.size();i++)
			{
				if(userList.get(i).name.equals(targetName))
				{
					targetIcon=userList.get(i).icon;
					targetNickName=userList.get(i).nickName;
				}
			}
			label_Target.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			label_Target.setText(targetName+"    "+targetNickName);
			label_Target.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
			label_Target.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/"+targetIcon+".png")));
			
			tglbtn_Bold = new JToggleButton("");
			tglbtn_Bold.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/bold.png")));
			tglbtn_Bold.setBounds(0, 427, 39, 35);
			contentPane.add(tglbtn_Bold);

			tglbtn_Italic = new JToggleButton("");
			tglbtn_Italic.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/italic.png")));
			tglbtn_Italic.setBounds(39, 427, 39, 35);
			contentPane.add(tglbtn_Italic);

			comboBox_Font = new JComboBox();
			comboBox_Font.setModel(new DefaultComboBoxModel(new String[] {"Microsoft YaHei", "Consola", "Courier New"}));
			comboBox_Font.setMaximumRowCount(3);
			comboBox_Font.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
			comboBox_Font.setBounds(88, 427, 145, 35);
			contentPane.add(comboBox_Font);

			comboBox_Size = new JComboBox();
			comboBox_Size.setModel(new DefaultComboBoxModel(new String[] {"14", "16", "18", "20", "22", "24"}));
			comboBox_Size.setFont(new Font("Courier New", Font.PLAIN, 18));
			comboBox_Size.setBounds(243, 427, 93, 35);
			contentPane.add(comboBox_Size);

			button_Color = new JButton("");
			button_Color.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/color.png")));
			button_Color.setBounds(346, 427, 39, 35);
			contentPane.add(button_Color);

			button_SendFile = new JButton("");
			button_SendFile.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/file.png")));
			button_SendFile.setBounds(386, 427, 49, 35);
			contentPane.add(button_SendFile);

			updateFontBar();
			
			setVisible(true);
			
			addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					try
					{
						for(int i=0;i<pChatList.size();i++)
						{
							if(pChatList.get(i).targetName.equals(targetName))
								pChatList.remove(i);
						}
					}
					catch (Exception E)
					{
						E.printStackTrace();
					}
				}
			});
			
			tglbtn_Bold.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e) 
				{	
					if(tglbtn_Bold.isSelected())
					{
						if(tglbtn_Italic.isSelected())
						{
							font=new Font(fontName,Font.BOLD+Font.ITALIC,font.getSize());
							fontStyle=Font.BOLD+Font.ITALIC;
							textField.setFont(font);
							Client.this.tglbtn_Bold.setSelected(true);
							Client.this.tglbtn_Italic.setSelected(true);
						}
						else
						{
							font=new Font(fontName,Font.BOLD,font.getSize());
							fontStyle=Font.BOLD;
							textField.setFont(font);
							Client.this.tglbtn_Bold.setSelected(true);
							Client.this.tglbtn_Italic.setSelected(false);
						}
					}
					else
					{
						if(tglbtn_Italic.isSelected())
						{
							font=new Font(fontName,Font.ITALIC,font.getSize());
							fontStyle=Font.ITALIC;
							textField.setFont(font);
							Client.this.tglbtn_Bold.setSelected(false);
							Client.this.tglbtn_Italic.setSelected(true);
						}
						else
						{
							font=new Font(fontName,Font.PLAIN,font.getSize());
							fontStyle=Font.PLAIN;
							textField.setFont(font);
							Client.this.tglbtn_Bold.setSelected(false);
							Client.this.tglbtn_Italic.setSelected(false);
						}
					}
				}
			});

			tglbtn_Italic.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					if(tglbtn_Bold.isSelected())
					{
						if(tglbtn_Italic.isSelected())
						{
							font=new Font(fontName,Font.BOLD+Font.ITALIC,font.getSize());
							fontStyle=Font.BOLD+Font.ITALIC;
							textField.setFont(font);
							Client.this.tglbtn_Bold.setSelected(true);
							Client.this.tglbtn_Italic.setSelected(true);
						}
						else
						{
							font=new Font(fontName,Font.BOLD,font.getSize());
							fontStyle=Font.BOLD;
							textField.setFont(font);
							Client.this.tglbtn_Bold.setSelected(true);
							Client.this.tglbtn_Italic.setSelected(false);
						}
					}
					else
					{
						if(tglbtn_Italic.isSelected())
						{
							font=new Font(fontName,Font.ITALIC,font.getSize());
							fontStyle=Font.ITALIC;
							textField.setFont(font);
							Client.this.tglbtn_Bold.setSelected(false);
							Client.this.tglbtn_Italic.setSelected(true);
						}
						else
						{
							font=new Font(fontName,Font.PLAIN,font.getSize());
							fontStyle=Font.PLAIN;
							textField.setFont(font);
							Client.this.tglbtn_Bold.setSelected(false);
							Client.this.tglbtn_Italic.setSelected(false);
						}
					}
				}
			});

			button_Color.addActionListener(new ActionListener()
			{			
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					JColorChooser jColorChooser=new JColorChooser(Color.BLACK);
					color=jColorChooser.showDialog(null, "Set text color", Color.BLACK);
					textField.setForeground(color);
					Client.this.textField.setForeground(color);
				}
			});

			comboBox_Font.addItemListener(new ItemListener()
			{
				@Override
				public void itemStateChanged(ItemEvent e) 
				{
					if(e.getStateChange()==ItemEvent.SELECTED)
					{
						switch (comboBox_FontName.getSelectedIndex())
						{
						case 0://Microsoft YaHei
						{
							font=new Font("Microsoft YaHei",font.getStyle(),font.getSize());
							fontName="Microsoft YaHei";
							textField.setFont(font);
							comboBox_FontName.setSelectedIndex(comboBox_Font.getSelectedIndex());
							Client.this.textField.setFont(font);
							break;
						}
						case 1://Consolas
						{
							font=new Font("Consolas",font.getStyle(),font.getSize());
							fontName="Consolas";
							textField.setFont(font);
							comboBox_FontName.setSelectedIndex(comboBox_Font.getSelectedIndex());
							Client.this.textField.setFont(font);
							break;
						}
						case 2://Courier New
						{
							font=new Font("Courier New",font.getStyle(),font.getSize());
							fontName="Courier New";
							textField.setFont(font);
							comboBox_FontName.setSelectedIndex(comboBox_Font.getSelectedIndex());
							Client.this.textField.setFont(font);
							break;
						}
						}
					}
				}
			});

			comboBox_Size.addItemListener(new ItemListener()
			{
				@Override
				public void itemStateChanged(ItemEvent e) 
				{
					if(e.getStateChange()==ItemEvent.SELECTED)
					{
						switch (comboBox_FontSize.getSelectedIndex())
						{
						case 0://14
						{
							font=new Font(font.getFontName(), font.getStyle(), 14);
							fontSize=14;
							textField.setFont(font);
							comboBox_FontSize.setSelectedIndex(comboBox_Size.getSelectedIndex());
							Client.this.textField.setFont(font);
							break;
						}
						case 1://16
						{
							font=new Font(font.getFontName(), font.getStyle(), 16);
							fontSize=16;
							textField.setFont(font);
							comboBox_FontSize.setSelectedIndex(comboBox_Size.getSelectedIndex());
							Client.this.textField.setFont(font);
							break;
						}
						case 2://18
						{
							font=new Font(font.getFontName(), font.getStyle(), 18);
							fontSize=18;
							textField.setFont(font);
							comboBox_FontSize.setSelectedIndex(comboBox_Size.getSelectedIndex());
							Client.this.textField.setFont(font);
							break;
						}
						case 3://20
						{
							font=new Font(font.getFontName(), font.getStyle(), 20);
							fontSize=20;
							textField.setFont(font);
							comboBox_FontSize.setSelectedIndex(comboBox_Size.getSelectedIndex());
							Client.this.textField.setFont(font);
							break;
						}
						case 4://22
						{
							font=new Font(font.getFontName(), font.getStyle(), 22);
							fontSize=22;
							textField.setFont(font);
							comboBox_FontSize.setSelectedIndex(comboBox_Size.getSelectedIndex());
							Client.this.textField.setFont(font);
							break;
						}
						case 5://24
						{
							font=new Font(font.getFontName(), font.getStyle(), 24);
							fontSize=24;
							textField.setFont(font);
							comboBox_FontSize.setSelectedIndex(comboBox_Size.getSelectedIndex());
							Client.this.textField.setFont(font);
							break;
						}
						}
					}
				}
			});
			
			textField.addKeyListener(new KeyAdapter()
			{
				@Override
				public void keyReleased(KeyEvent e)
				{
					if(textField.getText().isEmpty())
						button_Enter.setEnabled(false);
					else
						button_Enter.setEnabled(true);
					if(e.getKeyChar()==KeyEvent.VK_ENTER && button_Enter.isEnabled())
						button_Enter.doClick();
				}
			});
			
			button_Enter.addActionListener(new ActionListener() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					printWriter.println("#Update Color");
					printWriter.println(color.getRGB());
					printWriter.println("#Update Font");
					printWriter.println(fontName);
					printWriter.println(fontStyle);
					printWriter.println(fontSize);
					printWriter.flush();
					printWriter.println("#MessageTo");
					SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					String time=simpleDateFormat.format(new Date());
					printWriter.println(time);
					String source=userName;
					printWriter.println(source);
					printWriter.println(targetName);
					String message=textField.getText();
					printWriter.println(message);
					printWriter.flush();
					appendMessage(time, userName, message);
					textField.setText("");
					button_Enter.setEnabled(false);
				}
			});
			
			button_SendFile.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					JFileChooser jFileChooser=new JFileChooser();
					jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					jFileChooser.setMultiSelectionEnabled(false);
					int op=jFileChooser.showOpenDialog(new JLabel("Select file to send"));
					if(op==JFileChooser.APPROVE_OPTION)
					{
						File file=jFileChooser.getSelectedFile();
						new FileThread(file, targetName).start();
					}
				}
			});
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createClientGUI()
	{
		try 
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		setTitle("ChatRoomPlus");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 855, 607);
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
		scrollPane_Message.setBounds(0, 25, 622, 446);
		contentPane.add(scrollPane_Message);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setAutoscrolls(true);
		scrollPane_Message.setViewportView(textPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		scrollPane.setBounds(0, 515, 558, 52);
		contentPane.add(scrollPane);
		
		textField = new JTextField();
		textField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
		scrollPane.setViewportView(textField);
		textField.setColumns(10);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(UIManager.getColor("Button.background"));
		toolBar.setFloatable(false);
		toolBar.setBounds(98, 475, 522, 37);
		contentPane.add(toolBar);
		
		tglbtn_Bold = new JToggleButton("");
		tglbtn_Bold.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/bold.png")));
		tglbtn_Bold.setBounds(10, 475, 39, 35);
		contentPane.add(tglbtn_Bold);
		
		tglbtn_Italic = new JToggleButton("");
		tglbtn_Italic.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/italic.png")));
		tglbtn_Italic.setBounds(49, 475, 39, 35);
		contentPane.add(tglbtn_Italic);
		
		comboBox_FontName = new JComboBox();
		comboBox_FontName.setModel(new DefaultComboBoxModel(new String[] {"Microsoft YaHei", "Consolas", "Courier New"}));
		comboBox_FontName.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
		comboBox_FontName.setMaximumRowCount(3);
		toolBar.add(comboBox_FontName);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(10);
		toolBar.add(horizontalStrut_1);
		
		comboBox_FontSize = new JComboBox();
		comboBox_FontSize.setFont(new Font("Courier New", Font.PLAIN, 18));
		comboBox_FontSize.setModel(new DefaultComboBoxModel(new String[] {"14", "16", "18", "20", "22", "24"}));
		toolBar.add(comboBox_FontSize);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(10);
		toolBar.add(horizontalStrut_2);
		
		btn_Color = new JButton("");
		btn_Color.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/color.png")));
		toolBar.add(btn_Color);
		
		Component horizontalStrut = Box.createHorizontalStrut(180);
		toolBar.add(horizontalStrut);
		
		btn_Config = new JButton("");
		btn_Config.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/setting.png")));
		toolBar.add(btn_Config);
		
		popupMenu = new JPopupMenu();
		
		mntmShowRecoord = new JMenuItem("Show Message Record");
		popupMenu.add(mntmShowRecoord);
		
		mntmChangePassword = new JMenuItem("Change Password");
		popupMenu.add(mntmChangePassword);

		mntmChangeNickName = new JMenuItem("Change NickName");
		popupMenu.add(mntmChangeNickName);
		
		mnChangeIcon = new JMenu("Change Icon");
		popupMenu.add(mnChangeIcon);

		mntmChangeIcon1 = new JMenuItem(new ImageIcon(Client.class.getResource("/client/Icons/1.png")));
		mnChangeIcon.add(mntmChangeIcon1);
		
		mntmChangeIcon2 = new JMenuItem(new ImageIcon(Client.class.getResource("/client/Icons/2.png")));
		mnChangeIcon.add(mntmChangeIcon2);
		
		mntmChangeIcon3 = new JMenuItem(new ImageIcon(Client.class.getResource("/client/Icons/3.png")));
		mnChangeIcon.add(mntmChangeIcon3);
		
		mntmChangeIcon4 = new JMenuItem(new ImageIcon(Client.class.getResource("/client/Icons/4.png")));
		mnChangeIcon.add(mntmChangeIcon4);
		
		mntmChangeIcon5 = new JMenuItem(new ImageIcon(Client.class.getResource("/client/Icons/5.png")));
		mnChangeIcon.add(mntmChangeIcon5);
		
		mntmChangeIcon0 = new JMenuItem(new ImageIcon(Client.class.getResource("/client/Icons/0.png")));
		mnChangeIcon.add(mntmChangeIcon0);

		btn_Enter = new JButton("");
		btn_Enter.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/enter.png")));
		btn_Enter.setBounds(560, 515, 60, 52);
		btn_Enter.setEnabled(false);
		contentPane.add(btn_Enter);
		
		JScrollPane scrollPane_Friends = new JScrollPane();
		scrollPane_Friends.setAutoscrolls(true);
		scrollPane_Friends.setBounds(630, 334, 208, 233);
		scrollPane_Friends.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
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
		
		btn_AddFriend = new JButton("");
		btn_AddFriend.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/add.png")));
		btn_AddFriend.setBounds(805, 306, 32, 28);
		contentPane.add(btn_AddFriend);
		
		btn_DeleteFriend = new JButton("");
		btn_DeleteFriend.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/del.png")));
		btn_DeleteFriend.setBounds(770, 306, 32, 28);
		contentPane.add(btn_DeleteFriend);
		
		label_Top = new JLabel("");
		label_Top.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
		label_Top.setBounds(0, 0, 622, 24);
		contentPane.add(label_Top);
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				try
				{
					printWriter.println("#Logout");
					printWriter.flush();
					if(bufferedReader.readLine()=="#Confirm")
					{
						socket.shutdownInput();
						socket.shutdownOutput();
						socket.close();
					}
					//super.windowClosing(e);
				}
				catch (Exception E)
				{
					E.printStackTrace();
				}
			}
		});

		btn_Config.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		});
		
		btn_Enter.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				printWriter.println("#Update Color");
				printWriter.println(color.getRGB());
				printWriter.println("#Update Font");
				printWriter.println(fontName);
				printWriter.println(fontStyle);
				printWriter.println(fontSize);
				printWriter.flush();
				printWriter.println("#Message");
				SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String time=simpleDateFormat.format(new Date());
				printWriter.println(time);
				String source=userName;
				printWriter.println(source);
				String message=textField.getText();
				printWriter.println(message);
				printWriter.flush();
				textField.setText("");
				btn_Enter.setEnabled(false);
			}
		});
		
		textField.addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(textField.getText().isEmpty())
					btn_Enter.setEnabled(false);
				else
					btn_Enter.setEnabled(true);
				if(e.getKeyChar()==KeyEvent.VK_ENTER && btn_Enter.isEnabled())
					btn_Enter.doClick();
			}
		});
		
		mntmChangeIcon0.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				label_Top.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/0.png")));
				icon="0";
				printWriter.println("#Update Icon");
				printWriter.println("0");
				printWriter.flush();
			}
		});
		
		mntmChangeIcon1.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				label_Top.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/1.png")));
				icon="1";
				printWriter.println("#Update Icon");
				printWriter.println("1");
				printWriter.flush();
			}
		});
		
		mntmChangeIcon2.addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				label_Top.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/2.png")));
				icon="2";
				printWriter.println("#Update Icon");
				printWriter.println("2");
				printWriter.flush();
			}
		});
		
		mntmChangeIcon3.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				label_Top.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/3.png")));
				icon="3";
				printWriter.println("#Update Icon");
				printWriter.println("3");
				printWriter.flush();
			}
		});
		
		mntmChangeIcon4.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				label_Top.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/4.png")));
				icon="4";
				printWriter.println("#Update Icon");
				printWriter.println("4");
				printWriter.flush();
			}
		});
		
		mntmChangeIcon5.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				label_Top.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/5.png")));
				icon="5";
				printWriter.println("#Update Icon");
				printWriter.println("5");
				printWriter.flush();
			}
		});
		
		mntmChangeNickName.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String nickname=JOptionPane.showInputDialog(null, new JLabel("Input nickname:"));
				if(nickname!=null)
				{
					userNickName=nickname;
					label_Top.setText(userName+"    "+nickname);
					printWriter.println("#Update Nickname");
					printWriter.println(nickname);
					printWriter.flush();
				}
			}
		});
		
		mntmChangePassword.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String pwd=JOptionPane.showInputDialog(null, new JLabel("Input old password:"));
				if(pwd!=null)
				{
					if(pwd.equals(password))
					{
						pwd=JOptionPane.showInputDialog(null, new JLabel("Input new password:"));
						if(pwd!=null)
						{
							password=pwd;
							printWriter.println("#Update Password");
							printWriter.println(pwd);
							printWriter.flush();
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, new JLabel("Old password wrong"), "Warning", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		});
		
		tglbtn_Italic.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(tglbtn_Bold.isSelected())
				{
					if(tglbtn_Italic.isSelected())
					{
						font=new Font(fontName,Font.BOLD+Font.ITALIC,font.getSize());
						fontStyle=Font.BOLD+Font.ITALIC;
						textField.setFont(font);
						if(!pChatList.isEmpty())
						{
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFontBold();
								pChatList.get(i).setFontItalic();
							}
						}
					}
					else
					{
						font=new Font(fontName,Font.BOLD,font.getSize());
						fontStyle=Font.BOLD;
						textField.setFont(font);
						if(!pChatList.isEmpty())
						{
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFontBold();
							}
						}
					}
				}
				else
				{
					if(tglbtn_Italic.isSelected())
					{
						font=new Font(fontName,Font.ITALIC,font.getSize());
						fontStyle=Font.ITALIC;
						textField.setFont(font);
						if(!pChatList.isEmpty())
						{
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFontItalic();
							}
						}
					}
					else
					{
						font=new Font(fontName,Font.PLAIN,font.getSize());
						fontStyle=Font.PLAIN;
						textField.setFont(font);
						if(!pChatList.isEmpty())
						{
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFontBold();
								pChatList.get(i).setFontItalic();
							}
						}
					}
				}
			}
		});
		
		tglbtn_Bold.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(tglbtn_Bold.isSelected())
				{
					if(tglbtn_Italic.isSelected())
					{
						font=new Font(fontName,Font.BOLD+Font.ITALIC,font.getSize());
						fontStyle=Font.BOLD+Font.ITALIC;
						textField.setFont(font);
						if(!pChatList.isEmpty())
						{
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFontBold();
								pChatList.get(i).setFontItalic();
							}
						}
					}
					else
					{
						font=new Font(fontName,Font.BOLD,font.getSize());
						fontStyle=Font.BOLD;
						textField.setFont(font);
						if(!pChatList.isEmpty())
						{
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFontBold();
							}
						}
					}
				}
				else
				{
					if(tglbtn_Italic.isSelected())
					{
						font=new Font(fontName,Font.ITALIC,font.getSize());
						fontStyle=Font.ITALIC;
						textField.setFont(font);
						if(!pChatList.isEmpty())
						{
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFontItalic();
							}
						}
					}
					else
					{
						font=new Font(fontName,Font.PLAIN,font.getSize());
						fontStyle=Font.PLAIN;
						textField.setFont(font);
						if(!pChatList.isEmpty())
						{
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFontBold();
								pChatList.get(i).setFontItalic();
							}
						}
					}
				}
			}
		});
		
		comboBox_FontName.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				if(e.getStateChange()==ItemEvent.SELECTED)
				{
					switch (comboBox_FontName.getSelectedIndex())
					{
						case 0://Microsoft YaHei
						{
							font=new Font("Microsoft YaHei",font.getStyle(),font.getSize());
							fontName="Microsoft YaHei";
							textField.setFont(font);
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFont();
							}
							break;
						}
						case 1://Consolas
						{
							font=new Font("Consolas",font.getStyle(),font.getSize());
							fontName="Consolas";
							textField.setFont(font);
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFont();
							}
							break;
						}
						case 2://Courier New
						{
							font=new Font("Courier New",font.getStyle(),font.getSize());
							fontName="Courier New";
							textField.setFont(font);
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFont();
							}
							break;
						}
					}
				}
			}
		});
		
		comboBox_FontSize.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				if(e.getStateChange()==ItemEvent.SELECTED)
				{
					switch (comboBox_FontSize.getSelectedIndex())
					{
						case 0://14
						{
							font=new Font(font.getFontName(), font.getStyle(), 14);
							fontSize=14;
							textField.setFont(font);
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFont();
							}
							break;
						}
						case 1://16
						{
							font=new Font(font.getFontName(), font.getStyle(), 16);
							fontSize=16;
							textField.setFont(font);
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFont();
							}
							break;
						}
						case 2://18
						{
							font=new Font(font.getFontName(), font.getStyle(), 18);
							fontSize=18;
							textField.setFont(font);
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFont();
							}
							break;
						}
						case 3://20
						{
							font=new Font(font.getFontName(), font.getStyle(), 20);
							fontSize=20;
							textField.setFont(font);
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFont();
							}
							break;
						}
						case 4://22
						{
							font=new Font(font.getFontName(), font.getStyle(), 22);
							fontSize=22;
							textField.setFont(font);
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFont();
							}
							break;
						}
						case 5://24
						{
							font=new Font(font.getFontName(), font.getStyle(), 24);
							fontSize=24;
							textField.setFont(font);
							for(int i=0;i<pChatList.size();i++)
							{
								pChatList.get(i).setFont();
							}
							break;
						}
					}
				}
			}
		});
	
		btn_Color.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JColorChooser jColorChooser=new JColorChooser(Color.BLACK);
				color=jColorChooser.showDialog(null, "Set text color", Color.BLACK);
				textField.setForeground(color);
				for(int i=0;i<pChatList.size();i++)
				{
					pChatList.get(i).setColor();
				}
			}
		});
		
		list_UserList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e) 
			{
				if(e.getClickCount()==2)
				{
					User user=(User)list_UserList.getSelectedValue();
					for(int i=0;i<pChatList.size();i++)
					{
						if(pChatList.get(i).targetName.equals(user.name))
						{
							return;
						}
					}
					PrivateChat pChat=new PrivateChat(user.name);
					pChatList.add(pChat);
				}
				super.mouseReleased(e);
			}
		});
		
		mntmShowRecoord.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					printWriter.println("#Group Record");
					printWriter.flush();
				}
				catch (Exception E)
				{
					E.printStackTrace();
				}
			}
		});
	}
	
	private void createLogPad()
	{
		logpad=new JFrame();
		try 
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					String nickname=JOptionPane.showInputDialog(logpad, new JLabel("Input nickname:"));
					if(nickname==null)
						return;
					printWriter.println("#Register");
					printWriter.println(textField_UserName.getText());
					printWriter.println(nickname);
					printWriter.println(new String(passwordField.getPassword()));
					printWriter.flush();
					String response=bufferedReader.readLine();
					if(response.equals("#Register Success"))
					{
						JOptionPane.showMessageDialog(logpad, new JLabel("Register success"), "Notice", JOptionPane.PLAIN_MESSAGE);
					}
					else
						if(response.equals("#User Existed"))
						{
							JOptionPane.showMessageDialog(logpad, new JLabel("User existed"), "Warning", JOptionPane.WARNING_MESSAGE);
						}
				}
				catch (Exception E)
				{
					E.printStackTrace();
				}
			}
		});
		
		btn_Login.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					printWriter.println("#Login");
					printWriter.println(textField_UserName.getText());
					printWriter.println(new String(passwordField.getPassword()));
					password=new String(passwordField.getPassword());
					printWriter.flush();
					String response=bufferedReader.readLine();
					if(response.equals("#Login Success"))
					{
						userName=bufferedReader.readLine();
						userNickName=bufferedReader.readLine();
						icon=bufferedReader.readLine();
						fontName=bufferedReader.readLine();
						fontStyle=Integer.parseInt(bufferedReader.readLine());
						fontSize=Integer.parseInt(bufferedReader.readLine());
						font=new Font(fontName, fontStyle, fontSize);
						int rgb=Integer.parseInt(bufferedReader.readLine());
						color=new Color(rgb);
						textField.setForeground(color);
						textField.setFont(font);
						switch (fontSize)
						{
							case 14:
							{
								comboBox_FontSize.setSelectedIndex(0);
								break;
							}
							case 16:
							{
								comboBox_FontSize.setSelectedIndex(1);
								break;
							}
							case 18:
							{
								comboBox_FontSize.setSelectedIndex(2);
								break;
							}
							case 20:
							{
								comboBox_FontSize.setSelectedIndex(3);
								break;
							}
							case 22:
							{
								comboBox_FontSize.setSelectedIndex(4);
								break;
							}
							case 24:
							{
								comboBox_FontSize.setSelectedIndex(5);
								break;
							}
						}
						switch (fontStyle)
						{
							case Font.PLAIN:
							{
								tglbtn_Bold.setSelected(false);
								tglbtn_Italic.setSelected(false);
								break;
							}
							case Font.BOLD:
							{
								tglbtn_Bold.setSelected(true);
								break;
							}
							case Font.ITALIC:
							{
								tglbtn_Italic.setSelected(true);
								break;
							}
							case Font.BOLD+Font.ITALIC:
							{
								tglbtn_Bold.setSelected(true);
								tglbtn_Italic.setSelected(true);
								break;
							}
						}
						switch (fontName)
						{
							case "Microsoft YaHei":
							{
								comboBox_FontName.setSelectedIndex(0);
								break;
							}
							case "Consolas":
							{
								comboBox_FontName.setSelectedIndex(1);
								break;
							}
							case "Courier New":
							{
								comboBox_FontName.setSelectedIndex(2);
								break;
							}
						}
						label_Top.setIcon(new ImageIcon(Client.class.getResource("/client/Icons/"+icon+".png")));
						label_Top.setText(userName+"    "+userNickName);
						logpad.setVisible(false);
						setVisible(true);
						new ListenThread().start();
					}
					else
						if(response.equals("#Login Failed"))
						{
							JOptionPane.showMessageDialog(logpad, new JLabel("Login failed(Error password)"), "Warning", JOptionPane.WARNING_MESSAGE);
							passwordField.setText("");
						}
						else
							if(response.equals("#User Not Existed"))
							{
								JOptionPane.showMessageDialog(logpad, new JLabel("Login failed(User doesn't existed)"), "Warning", JOptionPane.WARNING_MESSAGE);
								textField_UserName.setText("");
								passwordField.setText("");
							}
							else
								if(response.equals("#User Logged"))
								{
									JOptionPane.showMessageDialog(logpad, new JLabel("Login failed(User is already logged)"), "Warning", JOptionPane.WARNING_MESSAGE);
									textField_UserName.setText("");
									passwordField.setText("");
								}
				}
				catch (Exception E)
				{
					E.printStackTrace();
				}
			}
		});
		
		logpad.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		new Client();
	}
}
