package io.App.CommunityService.facade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {

	@JsonProperty("id")
	private int id;

	@JsonProperty("userName")
	private String userName;

	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("lastName")
	private String lastName;

	@JsonProperty("roleId")
	private int roleId;

	@JsonProperty("email")
	private String email;

	public UserDTO() {
		// For REST only
	}

	public UserDTO(int id, String userName, String firstName, String lastName,
			String email, int roleId) {
		this.id = id;
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.roleId = roleId;
	}

	public int getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public int getRoleId() {
		return roleId;
	}

	public String getEmail() {
		return email;
	}
}
