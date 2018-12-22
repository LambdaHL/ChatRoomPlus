package client;

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
	
	public User(String name, String nickName, String icon, String ip, Socket socket)
	{
		this.name=name;
		this.nickName=nickName;
		this.icon=icon;
		this.ip=ip;
		this.socket=socket;
		try
		{
			imageIcon=new ImageIcon(icon);
			image=imageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
