package server;

import java.sql.*;
import java.util.ArrayList;

public class DBOperator 
{
    private static final String JDBC_DRIVER="com.mysql.cj.jdbc.Driver";
	private static final String DB_URL="jdbc:mysql://localhost:3306/UserDB?useSSL=false&serverTimezone=UTC";
	private static final String USER="root";
    private static final String PASSWORD="123456";
    public static final int LOGIN_PWD_RIGHT=0;
    public static final int LOGIN_PWD_WRONG=1;
    public static final int LOGIN_USERNAME_NOT_EXISITED=4;
    public static final int LOGIN_SQLERROR=3;
    public static final int REG_USERNAME_SUCCESS=0;
    public static final int REG_USERNAME_EXISITED=1;
    public static final int UPDATE_PWD_SUCCESS=0;
    public static final int UPDATE_PWD_OPWD_ERROR=1;
    public static final int UPDATE_PWD_SQLERROR=2;
    private Connection connection;
    private PreparedStatement preparedStatement;
    
    public DBOperator()
    {
        try
        {
            Class.forName(JDBC_DRIVER);
            connection=DriverManager.getConnection(DB_URL, USER, PASSWORD);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int register(String userName, String userNickName, String password)
    {
        try
        {
            String sql="INSERT INTO User(username,usernickname,password,icon,logstatus) VALUES(?,?,?,?,0)";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, userNickName);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, "0");
            preparedStatement.executeUpdate();
            return REG_USERNAME_SUCCESS;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return REG_USERNAME_EXISITED;
        }
    }

    public int login(String userName, String password)
    {
        try
        {
            String sql="SELECT username FROM User";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.executeQuery();
            ResultSet resultSet=preparedStatement.getResultSet();
            ArrayList<String> userNameList=new ArrayList<String>();
            while(resultSet.next())
            {
                userNameList.add(resultSet.getString(1));
            }
            if(userNameList.contains(userName))
            {
                sql="SELECT password FROM User WHERE username=?";
                preparedStatement=connection.prepareStatement(sql);
                preparedStatement.setString(1, userName);
                preparedStatement.executeQuery();
                resultSet=preparedStatement.getResultSet();
                resultSet.next();
                String currentPassword=resultSet.getString(1);
                if(password.equals(currentPassword))
                {
                	sql="UPDATE User SET logstatus=1 WHERE username=?";
                	preparedStatement=connection.prepareStatement(sql);
                	preparedStatement.setString(1, userName);
                	preparedStatement.executeUpdate();
                    return LOGIN_PWD_RIGHT;
                }
                else
                    return LOGIN_PWD_WRONG;
            }
            else
            {
                return LOGIN_USERNAME_NOT_EXISITED;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return LOGIN_SQLERROR;
        }
    }

    public void logout(String userName)
    {
    	try
    	{
    		String sql="UPDATE User SET logstatus=0 WHERE username=?";
    		preparedStatement=connection.prepareStatement(sql);
    		preparedStatement.setString(1, userName);
    		preparedStatement.executeUpdate();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    public void updateIcon(String userName, String icon)
    {
        try
        {
            String sql="UPDATE User SET icon=? WHERE username=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, icon);
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean updateNickName(String userName, String nickName)
    {
        try
        {
            String sql="UPDATE User SET usernickname=? WHERE username=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, nickName);
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public int updatePassword(String userName, String originalPassword, String newPassword)
    {
        try
        {
            String sql="SELECT password FROM User WHERE username=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.executeQuery();
            ResultSet resultSet=preparedStatement.getResultSet();
            if(resultSet.getString(1).equals(originalPassword))
            {
                sql="UPDATE User SET password=? WHERE username=?";
                preparedStatement=connection.prepareStatement(sql);
                preparedStatement.setString(1, newPassword);
                preparedStatement.setString(2, userName);
                preparedStatement.executeQuery();
                return UPDATE_PWD_SUCCESS;
            }
            else
            {
                return UPDATE_PWD_OPWD_ERROR;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return UPDATE_PWD_SQLERROR;
        }
    }

    public ResultSet getAllUserList()
    {
        try
        {
            String sql="SELECT * FROM User";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.executeQuery();
            return preparedStatement.getResultSet();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getUserName()
    {
        try
        {
            String sql="SELECT username FROM User";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.executeQuery();
            ResultSet resultSet=preparedStatement.getResultSet();
            ArrayList<String> userList=new ArrayList<String>();
            while(resultSet.next())
            {
                userList.add(resultSet.getString(1));
            }
            return userList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getUserIcon()
    {
        try
        {
            String sql="SELECT icon FROM User";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.executeQuery();
            ResultSet resultSet=preparedStatement.getResultSet();
            ArrayList<String> iconList=new ArrayList<String>();
            while(resultSet.next())
            {
                iconList.add(resultSet.getString(1));
            }
            return iconList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getUserNickName()
    {
        try
        {
            String sql="SELECT usernickname FROM User";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.executeQuery();
            ResultSet resultSet=preparedStatement.getResultSet();
            ArrayList<String> nickNameList=new ArrayList<String>();
            resultSet.next();
            nickNameList.add(resultSet.getString(1));
            return nickNameList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String getUserNickName(String userName)
    {
    	try
        {
            String sql="SELECT usernickname FROM User WHERE username=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.executeQuery();
            ResultSet resultSet=preparedStatement.getResultSet();
            String result=new String();
            resultSet.next();
            result=resultSet.getString(1);
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getUserIcon(String userName)
    {
    	try
    	{
    		String sql="SELECT icon FROM User WHERE username=?";
    		preparedStatement=connection.prepareStatement(sql);
    		preparedStatement.setString(1, userName);
    		preparedStatement.executeQuery();
    		ResultSet resultSet=preparedStatement.getResultSet();
    		String result=new String();
    		resultSet.next();
    		result=resultSet.getString(1);
    		return result;
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public boolean isLogged(String userName)
    {
    	try
    	{
    		String sql="SELECT logstatus FROM User WHERE username=?";
    		preparedStatement=connection.prepareStatement(sql);
    		preparedStatement.setString(1, userName);
    		preparedStatement.executeQuery();
    		ResultSet resultSet=preparedStatement.getResultSet();
    		resultSet.next();
    		if(resultSet.getInt(1)==1)
    			return true;
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return false;
    }
    
    public void reset()
    {
    	try
    	{
    		String sql="UPDATE User SET logstatus=0";
    		preparedStatement=connection.prepareStatement(sql);
    		preparedStatement.executeUpdate();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
