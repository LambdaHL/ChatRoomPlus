package client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Font;
import java.util.ArrayList;

public class MessageRecordFrame extends JFrame
{
	private JPanel contentPane;

	public MessageRecordFrame(ArrayList<String> strings) 
	{
		setTitle("Group Message Record");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 512, 519);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAutoscrolls(true);
		scrollPane.setBounds(0, 0, 496, 480);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		int i=0;
		for(String string:strings)
		{
			textArea.append(string+"\n");
			i++;
			if(i==3)
			{
				i=0;
				textArea.append("\n");
			}
		}
		
		setVisible(true);
	}
}
