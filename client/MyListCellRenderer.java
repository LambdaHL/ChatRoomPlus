package client;

import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

public class MyListCellRenderer extends DefaultListCellRenderer
{
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
	{
		User user=(User)value;
		setFont(new Font(getFont().getName(), Font.BOLD, 16));
		setText("<html>"+user.name+"<br/>"+user.nickName+"<html/>");
		setIcon(new ImageIcon(user.image));
		setIconTextGap(10);
		if (isSelected) 
		{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} 
		else 
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		return this;
	}
}
