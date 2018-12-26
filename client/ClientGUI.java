package client;

import java.awt.*;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClientGUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI frame = new ClientGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientGUI() {
		setTitle("CharRoomPlus");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 855, 607);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane_OnlineUserList = new JScrollPane();
		scrollPane_OnlineUserList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane_OnlineUserList.setAutoscrolls(true);
		scrollPane_OnlineUserList.setBounds(630, 25, 208, 281);
		contentPane.add(scrollPane_OnlineUserList);
		
		JList list_UserList = new JList();
		list_UserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_OnlineUserList.setViewportView(list_UserList);
		
		JScrollPane scrollPane_Message = new JScrollPane();
		scrollPane_Message.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane_Message.setAutoscrolls(true);
		scrollPane_Message.setBounds(0, 25, 622, 446);
		contentPane.add(scrollPane_Message);
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
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
		
		JComboBox comboBox_FontName = new JComboBox();
		comboBox_FontName.setModel(new DefaultComboBoxModel(new String[] {"微软雅黑", "Consola", "Courier New"}));
		comboBox_FontName.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		comboBox_FontName.setMaximumRowCount(3);
		toolBar.add(comboBox_FontName);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(10);
		toolBar.add(horizontalStrut_1);
		
		JComboBox comboBox_FontSize = new JComboBox();
		comboBox_FontSize.setFont(new Font("Courier New", Font.PLAIN, 18));
		comboBox_FontSize.setModel(new DefaultComboBoxModel(new String[] {"14", "16", "18", "20", "22", "24"}));
		toolBar.add(comboBox_FontSize);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(10);
		toolBar.add(horizontalStrut_2);
		
		JButton button_Color = new JButton("");
		button_Color.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/color.png")));
		toolBar.add(button_Color);
		
		JButton btn_SendFile = new JButton("");
		btn_SendFile.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/file.png")));
		toolBar.add(btn_SendFile);
		
		Component horizontalStrut = Box.createHorizontalStrut(150);
		toolBar.add(horizontalStrut);
		
		JButton btn_Config = new JButton("");
		btn_Config.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/setting.png")));
		toolBar.add(btn_Config);
		
		JPopupMenu popupMenu = new JPopupMenu();
		
		btn_Config.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		});
		
		JMenuItem mntmShowRecord = new JMenuItem("Show Message Record");
		popupMenu.add(mntmShowRecord);
		
		JMenuItem mntmChangePassword = new JMenuItem("Change Password");
		popupMenu.add(mntmChangePassword);
		
		JMenuItem mntmChangeNickName = new JMenuItem("Change NickName");
		popupMenu.add(mntmChangeNickName);
		
		JMenu mnChangeIcon = new JMenu("Change Icon");
		popupMenu.add(mnChangeIcon);
		
		JMenuItem mntmChangeIcon1 = new JMenuItem(new ImageIcon(ClientGUI.class.getResource("/client/Icons/1.png")));
		mnChangeIcon.add(mntmChangeIcon1);
		
		JMenuItem mntmChangeIcon2 = new JMenuItem(new ImageIcon(ClientGUI.class.getResource("/client/Icons/2.png")));
		mnChangeIcon.add(mntmChangeIcon2);
		
		JMenuItem mntmChangeIcon3 = new JMenuItem(new ImageIcon(ClientGUI.class.getResource("/client/Icons/3.png")));
		mnChangeIcon.add(mntmChangeIcon3);
		
		JMenuItem mntmChangeIcon4 = new JMenuItem(new ImageIcon(ClientGUI.class.getResource("/client/Icons/4.png")));
		mnChangeIcon.add(mntmChangeIcon4);
		
		JMenuItem mntmChangeIcon5 = new JMenuItem(new ImageIcon(ClientGUI.class.getResource("/client/Icons/5.png")));
		mnChangeIcon.add(mntmChangeIcon5);
		
		JMenuItem mntmChangeIcon0 = new JMenuItem(new ImageIcon(ClientGUI.class.getResource("/client/Icons/0.png")));
		mnChangeIcon.add(mntmChangeIcon0);
		
		JButton btn_Enter = new JButton("");
		btn_Enter.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/enter.png")));
		btn_Enter.setBounds(560, 515, 60, 52);
		contentPane.add(btn_Enter);
		
		JScrollPane scrollPane_Friends = new JScrollPane();
		scrollPane_Friends.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane_Friends.setAutoscrolls(true);
		scrollPane_Friends.setBounds(630, 334, 208, 233);
		contentPane.add(scrollPane_Friends);
		
		@SuppressWarnings("rawtypes")
		JList list_Friends = new JList();
		list_Friends.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_Friends.setViewportView(list_Friends);
		
		JLabel lblOnline = new JLabel("Online");
		lblOnline.setFont(new Font("Consolas", Font.PLAIN, 16));
		lblOnline.setBounds(632, 8, 68, 19);
		contentPane.add(lblOnline);
		
		JLabel lblNewLabel = new JLabel("Friends");
		lblNewLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
		lblNewLabel.setBounds(630, 316, 70, 19);
		contentPane.add(lblNewLabel);
		
		JButton button_AddFriend = new JButton("");
		button_AddFriend.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/add.png")));
		button_AddFriend.setBounds(805, 306, 32, 28);
		contentPane.add(button_AddFriend);
		
		JButton button_DeleteFriend = new JButton("");
		button_DeleteFriend.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/del.png")));
		button_DeleteFriend.setBounds(770, 306, 32, 28);
		contentPane.add(button_DeleteFriend);
		
		JLabel label_Top = new JLabel("");
		label_Top.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
		label_Top.setBounds(0, 0, 622, 24);
		contentPane.add(label_Top);
		
		JToggleButton tglbtn_Italic = new JToggleButton("");
		tglbtn_Italic.setBounds(49, 475, 39, 35);
		contentPane.add(tglbtn_Italic);
		tglbtn_Italic.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/italic.png")));
		
		JToggleButton tglbtn_Bold = new JToggleButton("");
		tglbtn_Bold.setBounds(10, 475, 39, 35);
		contentPane.add(tglbtn_Bold);
		tglbtn_Bold.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/bold.png")));
	}
}
