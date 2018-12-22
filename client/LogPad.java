package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JToolBar;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.ImageIcon;

public class LogPad extends JFrame {

	private JPanel contentPane;
	private JTextField textField_UserName;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogPad frame = new LogPad();
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
	public LogPad() {
		setTitle("CharRoomPlus");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 459, 218);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JToolBar toolBar = new JToolBar();
		toolBar.setBorder(null);
		toolBar.setFloatable(false);
		contentPane.add(toolBar, BorderLayout.SOUTH);
		
		JLabel lblServerStatus = new JLabel("Server Status:");
		lblServerStatus.setFont(new Font("Courier New", Font.PLAIN, 14));
		toolBar.add(lblServerStatus);
		
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
		textField_UserName.setBounds(137, 25, 266, 25);
		panel.add(textField_UserName);
		textField_UserName.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Consolas", Font.PLAIN, 18));
		passwordField.setBounds(137, 67, 266, 25);
		panel.add(passwordField);
		
		JButton btn_Register = new JButton("Register");
		btn_Register.setFont(new Font("Consolas", Font.PLAIN, 14));
		btn_Register.setEnabled(false);
		btn_Register.setBounds(77, 112, 103, 25);
		panel.add(btn_Register);
		
		JButton btn_Login = new JButton("Login");
		btn_Login.setFont(new Font("Consolas", Font.PLAIN, 14));
		btn_Login.setEnabled(false);
		btn_Login.setBounds(263, 112, 97, 25);
		panel.add(btn_Login);
	}
}
