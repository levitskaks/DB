package project.core;
import java.math.BigDecimal;

public class Employee {
	private int id;
	private String lastName;
	private String firstName;
	private String email;
	private BigDecimal salary;
	
	public Employee(int id, String lastName, String firstName, String email, BigDecimal salary) {
		this.setId(id);
		this.setLastName(lastName);
		this.setFirstName(firstName);
		this.setEmail(email);
		this.setSalary(salary);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String ToString (){
		return String.format("Employee [id=%s, lastName=%s, firstName=%s, email=%s, salary=%s]" , id, lastName, firstName, email, salary);	
	}
}
