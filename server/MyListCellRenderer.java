package server;

import java.awt.Component;
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
		setText("<html>"+user.name+"<br/>"+user.nickName+"<html/>");
		setIcon(new ImageIcon(user.image));
		setIconTextGap(30);
		return this;
	}
}
