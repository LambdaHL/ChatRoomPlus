package server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class MyListCellRenderer extends JLabel implements ListCellRenderer<Object> 
{
	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
	{
		User user=(User)value;
		setFont(new Font(getFont().getName(), Font.BOLD, 16));
		setText("<html>"+user.name+"<br/>"+user.nickName+"<html/>");
		setIcon(new ImageIcon(user.image));
		setIconTextGap(10);
		Color bg,fg;
		JList.DropLocation dropLocation = list.getDropLocation();
		if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) 
		{
			bg = Color.WHITE;
			fg = Color.CYAN;
		}
		else 
			if (isSelected) 
			{
				bg = Color.WHITE;
				fg = Color.BLUE;
			} 
			else 
			{
				bg = Color.WHITE;
				fg = Color.BLACK;
			}
		setBackground(bg);
		setForeground(fg);
		return this;
	}
}
