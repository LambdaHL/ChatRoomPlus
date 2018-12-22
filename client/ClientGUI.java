package client;

import java.awt.*;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ClientGUI extends JFrame {

	private JPanel contentPane;

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
		setBounds(100, 100, 855, 656);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane_OnlineUserList = new JScrollPane();
		scrollPane_OnlineUserList.setAutoscrolls(true);
		scrollPane_OnlineUserList.setBounds(630, 25, 208, 281);
		contentPane.add(scrollPane_OnlineUserList);
		
		JList list_UserList = new JList();
		list_UserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_OnlineUserList.setViewportView(list_UserList);
		
		JScrollPane scrollPane_Message = new JScrollPane();
		scrollPane_Message.setAutoscrolls(true);
		scrollPane_Message.setBounds(0, 0, 622, 471);
		contentPane.add(scrollPane_Message);
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		scrollPane_Message.setViewportView(textPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		scrollPane.setBounds(0, 515, 558, 100);
		contentPane.add(scrollPane);
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setLocation(0, 515);
		scrollPane.setViewportView(editorPane);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(UIManager.getColor("Button.background"));
		toolBar.setFloatable(false);
		toolBar.setBounds(0, 475, 620, 37);
		contentPane.add(toolBar);
		
		JToggleButton tglbtn_Bold = new JToggleButton("");
		tglbtn_Bold.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/bold.jpg")));
		toolBar.add(tglbtn_Bold);
		
		JToggleButton tglbtn_Italic = new JToggleButton("");
		tglbtn_Italic.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/italic.jpg")));
		toolBar.add(tglbtn_Italic);
		
		JToggleButton tglbtn_Underline = new JToggleButton("");
		tglbtn_Underline.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/underline.jpg")));
		toolBar.add(tglbtn_Underline);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"微软雅黑", "Consola", "Courier New"}));
		comboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		comboBox.setMaximumRowCount(3);
		toolBar.add(comboBox);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(10);
		toolBar.add(horizontalStrut_1);
		
		
		@SuppressWarnings("rawtypes")
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setFont(new Font("Courier New", Font.PLAIN, 18));
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"12", "14", "16", "18", "20", "22", "24"}));
		toolBar.add(comboBox_1);
		
		Component horizontalStrut = Box.createHorizontalStrut(200);
		toolBar.add(horizontalStrut);
		
		JButton btn_Enter = new JButton("");
		btn_Enter.setIcon(new ImageIcon(ClientGUI.class.getResource("/client/Icons/enter.png")));
		btn_Enter.setBounds(560, 515, 60, 100);
		contentPane.add(btn_Enter);
		
		JScrollPane scrollPane_Friends = new JScrollPane();
		scrollPane_Friends.setAutoscrolls(true);
		scrollPane_Friends.setBounds(630, 334, 208, 281);
		contentPane.add(scrollPane_Friends);
		
		@SuppressWarnings("rawtypes")
		JList list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_Friends.setViewportView(list);
		
		JLabel lblOnline = new JLabel("Online");
		lblOnline.setFont(new Font("Consolas", Font.PLAIN, 16));
		lblOnline.setBounds(632, 8, 68, 19);
		contentPane.add(lblOnline);
		
		JLabel lblNewLabel = new JLabel("Friends");
		lblNewLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
		lblNewLabel.setBounds(630, 316, 70, 19);
		contentPane.add(lblNewLabel);
	}
}
