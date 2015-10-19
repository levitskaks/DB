package project.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import project.core.User;

public class DAOUser {
	private Connection myConn;
	
	public DAOUser() throws Exception{
//		get DataBase properties
		Properties props = new Properties();
		props.load(new FileInputStream("UsPass"));
		String user = props.getProperty("user");
		String password =props.getProperty("password");
		String dbURL = props.getProperty("DBaddress");
//		соединяемся с БД
		myConn =DriverManager.getConnection(dbURL, user, password);
		System.out.println ("Connect to: " + dbURL);
	} 
	
	private User convertRowToUser (ResultSet myRs) throws Exception{
		int id = myRs.getInt("id");
		String lastName = myRs.getString("last_name");
		String firstName = myRs.getString("first_name");
		String email = myRs.getString("email");
		boolean admin = myRs.getBoolean("is_admin");
		User tempUser = new User (id, lastName, firstName, email, admin);
		return tempUser;	
	} // and convretRowToUser
	
	public List<User> getUsers (boolean admin, int userId) throws Exception{
		List<User> list = new ArrayList<User>();
		Statement myStmt = null;
		ResultSet myRs = null;
		try{
			myStmt = myConn.createStatement();
			String sql = null;
			if (admin){
				sql = "select * from users order by last_name";
			}else{
				sql = "select * from users where id="+userId+" order by last_name";
			}
			myRs = myStmt.executeQuery(sql);
			
			while (myRs.next()){
				User tempUser = convertRowToUser(myRs);
				list.add(tempUser);
			}
			return list;
		}finally{
			DAOUtils.close(myStmt, myRs);
		}
	} // and getUsers
	
	public void addUser (User theUser) throws Exception {
		PreparedStatement myStmt = null;
		try {
			myStmt = myConn.prepareStatement ("insert into user" + " (first_name, last_name, email, is_admin, password)" + " values (?, ?, ?, ?)");
			
			myStmt.setString(1, theUser.getFirstName());
			myStmt.setString(2, theUser.getLastName());
			myStmt.setString(3, theUser.getEmail());
			myStmt.setBoolean(4, theUser.isAdmin());
			
			
		}finally{
			DAOUtils.close(myStmt);
		}
		
	}

}
