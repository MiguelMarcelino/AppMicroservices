package io.App.CommunityService.databaseAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import io.App.CommunityService.business.Community;
import io.App.CommunityService.business.Role;
import io.App.CommunityService.business.User;
import io.App.CommunityService.facade.exceptions.CommunityAlreadyExistsException;
import io.App.CommunityService.facade.exceptions.CommunityDoesNotExistException;
import io.App.CommunityService.facade.exceptions.InternalAppException;

public class CommunityDatabaseConnection {

	// import class for establishing SQL connection
	private DatabaseConnection databaseConnection;

	// SQL Queries
	private static final String GET_ALL_COMMUNITIES_SQL = "SELECT c.id, c.name, c.description, u.id, "
			+ "u.user_name, u.first_name, u.last_name, u.email, r.id, r.name FROM communities AS c"
			+ "INNER JOIN users u ON c.community_owner_id = u.id"
			+ "INNER JOIN roles r ON r.id = u.role_id;";
	private static final String INSERT_COMMUNITY_SQL = "INSERT INTO communities "
			+ "(name, description, community_owner_id) "
			+ "VALUES (?, ?, ?);";
	private static final String DELETE_COMMUNITY_SQL = "DELETE FROM communities WHERE id = ?";
	private static final String GET_COMMUNITY_BY_ID = "SELECT c.id, c.name, c.description, u.id, "
			+ "u.user_name, u.first_name, u.last_name, u.email, r.id, r.name"
			+ "FROM communities AS c WHERE (c.id = ?) "
			+ "INNER JOIN users u ON c.community_owner_id = u.id"
			+ "INNER JOIN roles r ON r.id = u.role_id;";
	private static final String GET_COMMUNITY_BY_NAME = "SELECT c.id, c.name, c.description, u.id, "
			+ "u.user_name, u.first_name, u.last_name, u.email, r.id, r.name FROM communities AS c"
			+ "INNER JOIN users u ON c.community_owner_id = u.id"
			+ "INNER JOIN roles r ON r.id = u.role_id;"
			+ "WHERE (c.name = ?)";
	private static final String USER_CREATED_COMMUNITIES_SQL = "SELECT c.id, c.name, c.description, u.id, "
			+ "u.user_name, u.first_name, u.last_name, u.email, r.id, r.name FROM communities "
			+ "AS c WHERE (community_owner_id = ?) "
			+ "INNER JOIN users u ON c.community_owner_id = u.id"
			+ "INNER JOIN roles r ON r.id = u.role_id;";
	private static final String GET_COMMUNITY_LIST_WITH_SUB_INFO = "SELECT c.id, "
			+ "c.name, c.description, u.id, u.user_name, u.first_name, u.last_name, "
			+ "u.email, r.id, r.name "
			+ "IF(usc.user_id  IS NULL, FALSE, TRUE) as isRegistered"
			+ "FROM communities c"
			+ "LEFT JOIN users u ON (c.community_owner_id = u.id)"
			+ "INNER JOIN roles r ON r.id = u.role_id;"
			+ "LEFT JOIN user_subscribed_communities usc ON ((usc.user_id =  ?)"
			+ "AND (usc.community_id = c.id));";

	public CommunityDatabaseConnection() {
		databaseConnection = new DatabaseConnection();
	}

	/**
	 * This method return a list of all communities found on the database
	 * 
	 * @return a list of all communities found on the database
	 */
	public List<Community> getCommunityDatabaseList()
			throws InternalAppException {
		Connection con = databaseConnection.connectToDatabase();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<Community> communityList = new ArrayList<Community>();

		try {
			stmt = con.prepareStatement(GET_ALL_COMMUNITIES_SQL);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Role role = new Role(rs.getInt(9), rs.getString(10));
				User cOwner = new User(rs.getInt(4), rs.getString(5),
						rs.getString(6), rs.getString(7), rs.getString(8),
						role);
				Community community = new Community(rs.getInt(1),
						rs.getString(2), rs.getString(3), cOwner);
				communityList.add(community);
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
			if (stmt != null) {
				try {
					stmt.close();
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

		return communityList;
	}

	/**
	 * This method adds a community to the Communities Database
	 * 
	 * @param community - the community to add
	 * @throws CommunityAlreadyExistsException
	 */
	public void addCommunityToDatabase(Community community)
			throws CommunityAlreadyExistsException, InternalAppException {
		Connection con = databaseConnection.connectToDatabase();
		PreparedStatement st = null;

		try {
			st = con.prepareStatement(INSERT_COMMUNITY_SQL);
			st.setString(1, community.getName());
			st.setString(2, community.getDescription());
			st.setInt(3, community.getCommunityOwner().getId());
			st.executeUpdate();
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new CommunityAlreadyExistsException(community.getName());
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
		}

	}

	/**
	 * This method remove a community from the Communities Database
	 * 
	 * @param community - the community to remove
	 * @throws CommunityDoesNotExistException
	 */
	public void removeCommunityFromDatabase(Community community)
			throws InternalAppException {
		Connection con = databaseConnection.connectToDatabase();
		PreparedStatement st1 = null;
		PreparedStatement st2 = null;

		try {
			st1 = con.prepareStatement(DELETE_COMMUNITY_SQL);
			st1.setInt(1, community.getId());
			st1.executeUpdate();

			// There is no need to delete the community on
			// user_subscribed_communities
			// table since it has ON_DELETE_CASCADE
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
			if (st1 != null) {
				try {
					st1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st2 != null) {
				try {
					st2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * This method returns a community name given a community id
	 * 
	 * @param id - the id of the community to get the name
	 * @return the name of the given community id
	 * @throws CommunityDoesNotExistException
	 */
	public Community getCommunityById(int id) throws InternalAppException {
		Connection con = databaseConnection.connectToDatabase();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Community community = null;

		try {
			stmt = con.prepareStatement(GET_COMMUNITY_BY_ID);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			// get community
			rs.next();
			Role role = new Role(rs.getInt(9), rs.getString(10));
			User cOwner = new User(rs.getInt(4), rs.getString(5),
					rs.getString(6), rs.getString(7), rs.getString(8), role);
			community = new Community(rs.getInt(1), rs.getString(2),
					rs.getString(3), cOwner);
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
			if (stmt != null) {
				try {
					stmt.close();
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

		return community;

	}

	public Community getCommunityByName(String cName)
			throws InternalAppException {
		Connection con = databaseConnection.connectToDatabase();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Community community = null;

		try {
			stmt = con.prepareStatement(GET_COMMUNITY_BY_NAME);
			stmt.setString(1, cName);
			rs = stmt.executeQuery();

			// Get next community
			rs.next();
			Role role = new Role(rs.getInt(9), rs.getString(10));
			User cOwner = new User(rs.getInt(4), rs.getString(5),
					rs.getString(6), rs.getString(7), rs.getString(8), role);
			community = new Community(rs.getInt(1), rs.getString(2),
					rs.getString(3), cOwner);
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
			if (stmt != null) {
				try {
					stmt.close();
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

		return community;
	}

	/**
	 * 
	 * @param ownerUserName
	 * @return
	 * @throws InternalAppException
	 */
	public ArrayList<Community> userCreatedCommunities(String ownerUserName)
			throws InternalAppException {
		Connection con = databaseConnection.connectToDatabase();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<Community> lC = new ArrayList<>();

		try {
			stmt = con.prepareStatement(USER_CREATED_COMMUNITIES_SQL);
			stmt.setString(1, ownerUserName);
			rs = stmt.executeQuery();
			while (rs.next()) {
				/*
				 * 1 - community_id, 2 - community_name, 3 -
				 * community_description 4 - community_owner id, 5 - owner_name,
				 * 6 - owner_first_name 7 - owner_last_name, 8 - owner_role:id,
				 * 9 - owner_email
				 */
				Role role = new Role(rs.getInt(9), rs.getString(10));
				User cOwner = new User(rs.getInt(4), rs.getString(5),
						rs.getString(6), rs.getString(7), rs.getString(8),
						role);
				Community community = new Community(rs.getInt(1),
						rs.getString(2), rs.getString(3), cOwner);
				lC.add(community);
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
			if (stmt != null) {
				try {
					stmt.close();
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

		return lC;
	}

	public List<Community> getCommunityListWithSubInfo(int uID)
			throws InternalAppException {
		Connection con = databaseConnection.connectToDatabase();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<Community> lC = new ArrayList<>();

		try {
			stmt = con.prepareStatement(GET_COMMUNITY_LIST_WITH_SUB_INFO);
			stmt.setInt(1, uID);
			rs = stmt.executeQuery();
			while (rs.next()) {
				boolean isRegistered = rs.getInt(10) == 1;
				Role role = new Role(rs.getInt(9), rs.getString(10));
				User cOwner = new User(rs.getInt(4), rs.getString(5),
						rs.getString(6), rs.getString(7), rs.getString(8),
						role);
				Community community = new Community(rs.getInt(1),
						rs.getString(2), rs.getString(3), cOwner, isRegistered);
				lC.add(community);
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
			if (stmt != null) {
				try {
					stmt.close();
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

		return lC;
	}

}