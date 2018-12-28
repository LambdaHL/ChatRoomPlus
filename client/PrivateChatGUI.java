package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JComboBox;
import java.awt.Font;
import javax.swing.DefaultComboBoxModel;

public class PrivateChatGUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PrivateChatGUI frame = new PrivateChatGUI();
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
	public PrivateChatGUI() {
		setTitle("ChatRoomPlus");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 451, 574);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(0, 465, 385, 68);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btn_Enter = new JButton("");
		btn_Enter.setIcon(new ImageIcon(PrivateChatGUI.class.getResource("/client/Icons/enter.png")));
		btn_Enter.setBounds(386, 465, 49, 68);
		contentPane.add(btn_Enter);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		scrollPane.setBounds(0, 56, 435, 368);
		contentPane.add(scrollPane);
		
		JTextPane textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		textPane.setEditable(false);
		
		JLabel label_Me = new JLabel("");
		label_Me.setBounds(0, 0, 435, 25);
		contentPane.add(label_Me);
		
		JLabel label_Source = new JLabel("");
		label_Source.setBounds(0, 29, 435, 25);
		contentPane.add(label_Source);
		
		JToggleButton tglbtn_Bold = new JToggleButton("");
		tglbtn_Bold.setIcon(new ImageIcon(PrivateChatGUI.class.getResource("/client/Icons/bold.png")));
		tglbtn_Bold.setBounds(0, 427, 39, 35);
		contentPane.add(tglbtn_Bold);
		
		JToggleButton tglbtn_Italic = new JToggleButton("");
		tglbtn_Italic.setIcon(new ImageIcon(PrivateChatGUI.class.getResource("/client/Icons/italic.png")));
		tglbtn_Italic.setBounds(39, 427, 39, 35);
		contentPane.add(tglbtn_Italic);
		
		JComboBox comboBox_Font = new JComboBox();
		comboBox_Font.setModel(new DefaultComboBoxModel(new String[] {"微软雅黑", "Consola", "Courier New"}));
		comboBox_Font.setMaximumRowCount(3);
		comboBox_Font.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
		comboBox_Font.setBounds(88, 427, 145, 35);
		contentPane.add(comboBox_Font);
		
		JComboBox comboBox_Size = new JComboBox();
		comboBox_Size.setModel(new DefaultComboBoxModel(new String[] {"14", "16", "18", "20", "22", "24"}));
		comboBox_Size.setFont(new Font("Courier New", Font.PLAIN, 18));
		comboBox_Size.setBounds(243, 427, 93, 35);
		contentPane.add(comboBox_Size);
		
		JButton button_Color = new JButton("");
		button_Color.setIcon(new ImageIcon(PrivateChatGUI.class.getResource("/client/Icons/color.png")));
		button_Color.setBounds(346, 427, 39, 35);
		contentPane.add(button_Color);
		
		JButton button_SendFile = new JButton("");
		button_SendFile.setIcon(new ImageIcon(PrivateChatGUI.class.getResource("/client/Icons/file.png")));
		button_SendFile.setBounds(386, 427, 49, 35);
		contentPane.add(button_SendFile);
	}
}
