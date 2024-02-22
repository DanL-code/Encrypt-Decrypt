package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataModule {

	private String JDBC_URL;
	private String USERNAME;
	private String PASSWORD;

	public void setJDBC_URL(String jDBC_URL) {
		JDBC_URL = jDBC_URL;
	}

	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public void createAccountMethod(String newUserName, String newPassword, String encryptPassword,
			DataModuleListener dataModuleListener) {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			Statement statement = connection.createStatement();

			String checkExistingUserSql = "SELECT * FROM users WHERE Name = ?";
			PreparedStatement checkStatement = connection.prepareStatement(checkExistingUserSql);
			checkStatement.setString(1, newUserName);
			ResultSet checkResultSet = checkStatement.executeQuery();

			if (checkResultSet.next()) {
				dataModuleListener.onFaild("");
			} else {
				String sql = "INSERT INTO users (Name, Password) VALUES (\"" + newUserName + "\",\"" + encryptPassword
						+ "\") ON DUPLICATE KEY UPDATE Name = Name ";
				String sqlTheme = "INSERT INTO theme_setting (User_Name, Theme_Value) VALUES (\"" + newUserName
						+ "\", \"defaultTheme.css\") ON DUPLICATE KEY UPDATE User_Name = User_Name ";

				System.out.println(sql);
				System.out.println(sqlTheme);

				int rowsAffected = statement.executeUpdate(sql);

				if (rowsAffected > 0) {
					dataModuleListener.onSuccess("");

				}
				statement.executeUpdate(sqlTheme);
				statement.close();
				connection.close();

			}

		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	public void storeMsg(String userName, String encryptMsg, String hashedKey, String msgIndex,
			String pickedAlgorithm) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			String storeMsgSql = "INSERT INTO messages (user_name, encrypt_msg, hashed_key, msg_index, chosen_algorithm) VALUES (?,?,?,?,?) ";
			PreparedStatement storeMsgStatement = connection.prepareStatement(storeMsgSql);
			storeMsgStatement.setString(1, userName);
			storeMsgStatement.setString(2, encryptMsg);
			storeMsgStatement.setString(3, hashedKey);
			storeMsgStatement.setString(4, msgIndex);
			storeMsgStatement.setString(5, pickedAlgorithm);

			storeMsgStatement.executeUpdate();

			System.out.println(storeMsgStatement);

			storeMsgStatement.close();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String checkChosenAlgorithm(String userName, String inputIndex) {
		String chosenAlgorithmStr = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			String sql = "SELECT chosen_algorithm FROM messages WHERE user_name = \"" + userName
					+ "\" AND msg_index = \"" + inputIndex + "\"";
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				chosenAlgorithmStr = resultSet.getString("chosen_algorithm");
				System.out.println("ldld: " + chosenAlgorithmStr);
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chosenAlgorithmStr;
	}

	public String checkSQLMsg(String userName, String inputIndex) {
		String sqlMsg = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			String sql = "SELECT encrypt_msg FROM messages WHERE user_name = \"" + userName + "\" AND msg_index = \""
					+ inputIndex + "\"";
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				sqlMsg = resultSet.getString("encrypt_msg");
				System.out.println("ldld22222: " + sqlMsg);
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqlMsg;
	}

	public boolean checkIndex(String userName, String inputIndex) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			String sql = "SELECT id FROM messages WHERE user_name = \"" + userName + "\" AND msg_index = \""
					+ inputIndex + "\"";
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void storeTheme(String userName, String theme) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			String sql = "UPDATE theme_setting SET Theme_Value = ? WHERE User_Name = \"" + userName + "\"";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, theme);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadTheme(String userName, DataModuleListener dataModuleListener) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			String sql = "SELECT Theme_Value FROM theme_setting WHERE User_Name =\"" + userName + "\"";
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				String theme = resultSet.getString("Theme_Value");
				dataModuleListener.onSuccess(theme);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean validatePassword(String userName, String password, String inputP) {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			Statement statement = connection.createStatement();

			String sql = "SELECT Password from users where Name = \"" + userName + "\"";
			System.out.println(sql);

			ResultSet resultSet = statement.executeQuery(sql);

			if (resultSet.next()) {
				String sqlPassword = resultSet.getString("Password");
				if (password != null && inputP.equals(sqlPassword)) {
					return true;
				} else {
					return false;
				}
			}

			resultSet.close();
			statement.close();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void loginMethod(String userName, String password, String inputP, DataModuleListener dataModuleListener) {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			Statement statement = connection.createStatement();

			String sql = "SELECT Password from users where Name = \"" + userName + "\"";
			System.out.println(sql);

			ResultSet resultSet = statement.executeQuery(sql);

			if (resultSet.next()) {
				String sqlPassword = resultSet.getString("Password");

				if (password != null && inputP.equals(sqlPassword)) {
					dataModuleListener.onSuccess("");
					
				} else {
					dataModuleListener.onFaild("Wrong Password");
				}

				resultSet.close();
				statement.close();
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			dataModuleListener.onError( "Please choose a database.");
		}
	}

}
