package io.App.UserManagementService.userComponent;

public class User {

	private int id;
	private String userName;
	private String firstName;
	private String lastName;
	private String email;
	private String password;

	public User() {
		// For REST only
	}

	/**
	 * 
	 * @param id
	 *            - id of the user
	 * @param name
	 *            - name of the user
	 */
	public User(int id, String userName, String firstName, String lastName, String email, String password) {
		this.id = id;
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	/**
	 * This contructor is used to remove users from the system
	 * @param getuName
	 * @param password2
	 */
	public User(String getuName, String password2) {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return password;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}

}
