package io.App.UserManagementService.userComponent;

public class User {

	private int id;
	private String name;
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
	public User(int id, String name, String email, String password) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return password;
	}

}
