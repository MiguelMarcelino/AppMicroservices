package io.App.CommunityService.databaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.App.CommunityService.communityComponent.Operation;
import io.App.CommunityService.exceptions.InternalAppException;
import io.App.CommunityService.exceptions.UserDoesNotExistException;

public class UserDatabaseConnection {

	// import class for establishing SQL connection
	private DatabaseConnection databaseConnection;

	private static final String GET_USER_ROLE_BY_NAME_SQL = "SELECT role_id FROM users WHERE (user_name = ?);";
	private static final String GET_OPERATIONS_FOR_ROLE_ID = "SELECT * FROM opeations WHERE (role_id = ?)";

	public UserDatabaseConnection() {
		databaseConnection = new DatabaseConnection();
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws InternalAppException
	 * @throws UserDoesNotExistException
	 */
	public int getUserRoleID(String username)
			throws InternalAppException, UserDoesNotExistException {
		Connection con = databaseConnection.connectToDatabase();
		PreparedStatement st = null;
		ResultSet rs = null;
		int roleID;

		try {
			st = con.prepareStatement(GET_USER_ROLE_BY_NAME_SQL);
			st.setString(1, username);
			rs = st.executeQuery();

			// get user
			if (rs.next()) {
				roleID = rs.getInt(1);
			} else {
				throw new UserDoesNotExistException();
			}

		} catch (SQLException e) {
			throw new InternalAppException(e.getMessage());
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return roleID;
	}

	public List<Operation> getOperations(int roleID)
			throws InternalAppException {
		Connection con = databaseConnection.connectToDatabase();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Operation> operations = new ArrayList<>();

		try {
			st = con.prepareStatement(GET_OPERATIONS_FOR_ROLE_ID);
			st.setInt(1, roleID);
			rs = st.executeQuery();

			// get operations
			while (rs.next()) {
				operations.add(new Operation(rs.getInt(1), rs.getString(2),
						rs.getInt(3)));
			}

		} catch (SQLException e) {
			throw new InternalAppException(e.getMessage());
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return operations;
	}
}
