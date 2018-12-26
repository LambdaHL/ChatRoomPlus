package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.Socket;
import javax.swing.ImageIcon;

public class User  
{
	public String name;
	public String nickName;
	public String icon;
	public Image image;
	public ImageIcon imageIcon;
	public String ip;
	public Socket socket;
	public Font font;
	public String fontName;
	public int fontStyle,fontSize;
	public Color color;
	
	public User(String name, String nickName, String icon, String ip, Socket socket, String fontName, int fontStyle,int fontSize, Color color)
	{
		this.name=name;
		this.nickName=nickName;
		this.icon=icon;
		this.ip=ip;
		this.socket=socket;
		this.fontName=fontName;
		this.fontStyle=fontStyle;
		this.fontSize=fontSize;
		this.color=color;
		font=new Font(fontName,fontStyle,fontSize);
		try
		{
			String iconPath=System.getProperty("user.dir")+"\\client\\Icons\\"+icon+".png";
			imageIcon=new ImageIcon(iconPath);
			image=imageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
