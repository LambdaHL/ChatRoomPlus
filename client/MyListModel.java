package client;

import java.util.ArrayList;
import javax.swing.AbstractListModel;

public class MyListModel<E> extends AbstractListModel<Object>
{
	ArrayList<User> userList;
	
	public MyListModel(ArrayList<User> userList)
	{
		this.userList=userList;
	}

	@Override
	public int getSize()
	{
		return userList.size();
	}
	
	@Override	
	public Object getElementAt(int index) 
	{
		return userList.get(index);
	}
}

