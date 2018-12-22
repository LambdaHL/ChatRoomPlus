package server;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class ServerGUI extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI frame = new ServerGUI();
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
	public ServerGUI() {
		setTitle("Server");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1082, 549);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane_List = new JScrollPane();
		scrollPane_List.setAutoscrolls(true);
		scrollPane_List.setBounds(761, 10, 295, 489);
		contentPane.add(scrollPane_List);
		
		JList list_UserList = new JList();
		scrollPane_List.setViewportView(list_UserList);
		
		JScrollPane scrollPane_TextArea = new JScrollPane();
		scrollPane_TextArea.setAutoscrolls(true);
		scrollPane_TextArea.setBounds(378, 10, 361, 489);
		contentPane.add(scrollPane_TextArea);
		
		JTextArea textArea_Conversation = new JTextArea();
		textArea_Conversation.setEditable(false);
		scrollPane_TextArea.setViewportView(textArea_Conversation);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(749, 10, 2, 489);
		contentPane.add(separator);
		
		JScrollPane scrollPane_TextAreaLog = new JScrollPane();
		scrollPane_TextAreaLog.setAutoscrolls(true);
		scrollPane_TextAreaLog.setBounds(10, 10, 361, 489);
		contentPane.add(scrollPane_TextAreaLog);
		
		JTextArea textArea_Log = new JTextArea();
		textArea_Log.setEditable(false);
		scrollPane_TextAreaLog.setViewportView(textArea_Log);
	}
}
