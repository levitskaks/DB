package project.dao;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
// /Класс с меодами для закрытия соединения
public class DAOUtils {
	public static void close (Connection myConn, Statement myStmt, ResultSet myRs) throws SQLException{
		if (myRs!=null){
			myRs.close();
		}
		if(myStmt!=null){
			myStmt.close();
		}
		if(myRs!=null){
			myRs.close();
		}	
	}
	
	public static void close (Statement myStmt, ResultSet myRs) throws SQLException{
		close(null, myStmt, myRs);
	}
	
	public static void close (Statement myStmt) throws SQLException{
		close (null, myStmt, null);
	}

}
