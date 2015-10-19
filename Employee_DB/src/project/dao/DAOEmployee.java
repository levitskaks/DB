package project.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import project.core.AuditHistory;
import project.core.Employee;

public class DAOEmployee {
	private Connection myConn;
	public DAOEmployee() throws Exception{
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
//	SQL query for updating information about employee
	public void updateEmployee (Employee theEmployee, int userId) throws SQLException {
		PreparedStatement myStmt =null;
		try{
			myStmt = myConn.prepareStatement("update employees" + " set first_name=?, last_name=?, email=?, salary=?" + " where id=?");
//			set params for query
			myStmt.setString(1, theEmployee.getFirstName());
			myStmt.setString(2, theEmployee.getLastName());
			myStmt.setString(3, theEmployee.getEmail());
			myStmt.setBigDecimal(4, theEmployee.getSalary());
			myStmt.setInt(5, theEmployee.getId());
//			execute sql query update
			myStmt.executeUpdate();
			
//			insert into audithistory action UPDATE
			myStmt = myConn.prepareStatement("insert into audit_history" + " (user_id, employee_id, action, action_date_time)" + " values (?, ?, ?, ?)");
			myStmt.setInt(1, userId);
			myStmt.setInt(2, theEmployee.getId());
			myStmt.setString(3, "Updating employee: "+theEmployee.getLastName());
			myStmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
//			execute sql query
			myStmt.executeUpdate();
		}finally{
			DAOUtils.close(myStmt);
		}
	} //And UpdateEmployee
//	SQL query for adding new employee
	public void addEmployee (Employee theEmployee, int userId) throws Exception{
		PreparedStatement myStmt = null;
		try{
			myStmt = myConn.prepareStatement("insert into employees " + "(first_name, last_name, email, salary) " + "values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
//			RETURN_GENERATED_KEYS is generated id for new employee
			myStmt.setString(1, theEmployee.getFirstName());
			myStmt.setString(2, theEmployee.getLastName());
			myStmt.setString(3, theEmployee.getEmail());
			myStmt.setBigDecimal(4, theEmployee.getSalary());
			
			myStmt.executeUpdate();
			
			ResultSet generatedKeys = myStmt.getGeneratedKeys();
			if (generatedKeys.next()){
				theEmployee.setId(generatedKeys.getInt(1));
			}else{
				throw new SQLException ("Error generating key for new employee");
			}
			
//			insert into audithistory action INSERT
			myStmt = myConn.prepareStatement("insert into audit_history" + " (user_id, employee_id, action, action_date_time)" + " values (?, ?, ?, ?)");
			myStmt.setInt(1, userId);
			myStmt.setInt(2, theEmployee.getId());
			myStmt.setString(3, "Adding employee: "+theEmployee.getLastName());
			myStmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
//			execute sql query
			myStmt.executeUpdate();
				
		}finally{
			
		}
	} //End AddEmployee
	
	public List<Employee> getAllEmployees() throws Exception {
		List<Employee> list = new ArrayList<Employee>();
		Statement myStmt = null;
		ResultSet myRs = null;
		try{
			myStmt = myConn.createStatement();
			myRs = myStmt.executeQuery("select * from employees order by last_name");
			while(myRs.next()){
				Employee tempEmployee = convertRowToEmployee(myRs);
				list.add(tempEmployee);
			}
			return list;
		}finally{
			DAOUtils.close(myStmt, myRs);
		}	
	} //End getAllEmployess
	
	private Employee convertRowToEmployee(ResultSet myRs) throws SQLException {
		int id = myRs.getInt("id");
		String lastName = myRs.getString("last_name");
		String firstName = myRs.getString("first_name");
		String email = myRs.getString("email");
		BigDecimal salary = myRs.getBigDecimal("salary");
		
		Employee tempEmployee = new Employee (id, lastName, firstName, email, salary);
		return tempEmployee;
	} //End convertRowToEmployee
	
	public List<Employee> searchEmployees (String lastName) throws Exception {
		List<Employee> list = new ArrayList<Employee>();
		PreparedStatement myStmt = null;
		ResultSet myRs= null;
		try{
			lastName+= "%";
			myStmt = myConn.prepareStatement("select * from employees where last_name like ? order by last_name");
			myStmt.setString(1, lastName);
			myRs = myStmt.executeQuery();
			while (myRs.next()){
				Employee tempEmployee = convertRowToEmployee(myRs);
				list.add(tempEmployee);
			}
			return list;
		}finally{
			DAOUtils.close(myStmt, myRs);
		}
	}// end searchEmployees 
	
	public List<AuditHistory> getAuditHistory (int EmployeeId) throws Exception{
		List<AuditHistory> list = new ArrayList<AuditHistory>();
		Statement myStmt = null;
		ResultSet myRs = null;
		try{
			myStmt = myConn.createStatement();
			String sql = "Select history.user_id, history.employee_id, history.action, history.action_date_time, users.first_name, users.last_name "+
			" from audit_history history, users users where history.user_id=user.id AND history.employee_id=employeeId";
			myRs = myStmt.executeQuery(sql);
			while(myRs.next()){
				int userId = myRs.getInt("history.user_id");
				int employeeId = myRs.getInt("history.employee_id");
				String action = myRs.getString("history.action");
				Timestamp timeStamp = myRs.getTimestamp("history.action_date_time");
				java.util.Date actionDateTime = new java.util.Date(timeStamp.getTime());
				String userFirstName = myRs.getString("users.first_name");
				String userLastName = myRs.getString("users.last_name");
				AuditHistory temp = new AuditHistory (userId, employeeId, action, actionDateTime, userFirstName, userLastName);
				list.add(temp);
			}
			return list;
		}finally{
			DAOUtils.close(myStmt, myRs);
		}
	} //End getAuditHistory
	
	public static void main (String[] args) throws Exception{
		DAOEmployee dao = new DAOEmployee();
	}
}
