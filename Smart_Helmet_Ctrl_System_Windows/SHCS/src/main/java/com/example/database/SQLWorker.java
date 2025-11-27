package com.example.database;

import static com.example.MainWindow.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SQLWorker {
	private String url = "jdbc:mysql://localhost:3306/smart_helmet_db";
	private String un = "root";
	private String pw = "qwe123!@#";
	private Connection conn = null;

	private void connectDatabase() throws SQLException {
		if (conn == null) {
			conn = DriverManager.getConnection(url, un, pw);
		}
	}

	private ResultSet exeQuery(String query) throws SQLException {
		connectDatabase();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}

	private ResultSet exeQuery2(String query) throws SQLException {
		connectDatabase();
		PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pstmt.execute();
		ResultSet rs = pstmt.getGeneratedKeys();
		return rs;
	}

	public void logUseHistory(String userName, String comName, String ipAddr) {
		String query = "INSERT INTO svr_his (svr_user, svr_name, svr_ip) VALUES ('" + userName + "', '" + comName
				+ "', '" + ipAddr + "')";

		try {
			exeQuery2(query);

		} catch (Exception e) {

		}
	}

	public ResultSet displayTable(String classifyText) throws SQLException {
		if (classifyText.equals("UD")) {
			return exeQuery("SELECT ud_id, ud_name, ud_use FROM device_info");

		} else if (classifyText.equals("AP")) {
			return exeQuery("SELECT ap_id, ap_name, ap_ssid, ap_use FROM ap_info");

		} else {
			return null;
		}
	}

	public String insertTable(String classifyText, String[] arrInput) throws SQLException {
		LocalDate date = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
		String formattedDate = date.format(formatter);

		String query = "";
		String strID = "";
		if (classifyText.equals("UD")) {
			ResultSet r = exeQuery("SELECT max(ud_id) FROM device_info WHERE ud_id LIKE '" + formattedDate + "%'");
			if (r.next()) {
				if (r.getString(1) != null) {
					strID = (r.getInt(1) + 1) + "";
					query = "INSERT INTO device_info (ud_id, ud_name, ud_use) VALUES ('" + strID + "', '" + arrInput[0]
							+ "', '" + arrInput[1] + "')";

				} else {
					strID = (formattedDate + "001") + "";
					query = "INSERT INTO device_info (ud_id, ud_name, ud_use) VALUES ('" + strID + "', '" + arrInput[0]
							+ "', '" + arrInput[1] + "')";
				}
			}

		} else if (classifyText.equals("AP")) {
			query = "INSERT INTO ap_info (ap_name, ap_ssid, ap_use) VALUES ('" + arrInput[0] + "', '" + arrInput[1]
					+ "', '" + arrInput[2] + "')";
		}

		exeQuery2(query);
		return strID;
	}

	public void modifyTable(String classifyText, String[] arrInput) throws SQLException {
		String query = "";
		if (classifyText.equals("UD")) {
			query = "UPDATE device_info SET ud_name = '" + arrInput[0] + "' WHERE ud_id = " + arrInput[1];

		} else if (classifyText.equals("AP")) {
			query = "UPDATE ap_info SET ap_name = '" + arrInput[0] + "', ap_ssid = '" + arrInput[1] + "' WHERE ap_id = "
					+ arrInput[2];
		}

		exeQuery2(query);
	}

	public void deleteTable(String classifyText, String[] arrInput) throws SQLException {
		String query = "";
		if (classifyText.equals("UD")) {
			query = "DELETE FROM device_info WHERE ud_id = " + arrInput[0];

		} else if (classifyText.equals("AP")) {
			query = "DELETE FROM ap_info WHERE ap_id =  " + arrInput[0];
		}

		exeQuery2(query);
	}

	public void deleteTable2(String[] arrInput) throws SQLException {
		String query = "DELETE FROM ud_status WHERE ud_id = " + arrInput[0];
		exeQuery2(query);
	}

	public void changeUsable(String classifyText, String[] arrInput) {
		try {
			String query = "";
			if (classifyText.equals("UD")) {
				query = "UPDATE device_info SET ud_use = '" + arrInput[0] + "' WHERE ud_id = " + arrInput[1];

			} else if (classifyText.equals("AP")) {
				query = "UPDATE ap_info SET ap_use = '" + arrInput[0] + "' WHERE ap_id = " + arrInput[1];
			}

			exeQuery2(query);

		} catch (Exception e) {

		}
	}

	public int checkExistCnt(String tableName, String columName, String input) throws SQLException {
		String query = "SELECT COUNT(1) FROM " + tableName + " WHERE " + columName + " = " + input;

		connectDatabase();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}

	public ResultSet getSpecificValue(String tableName, String selectColumnName, String columnName, String input)
			throws SQLException {
		String query = "SELECT " + selectColumnName + " FROM " + tableName + " WHERE " + columnName + " = " + input;

		connectDatabase();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		return rs;
	}

	public ResultSet displayStatusTable() throws SQLException {
		String query = "SELECT us.ud_id, ui.ud_name, ai.ap_ssid, ai.ap_name, us.status_connection, us.status_lastConnTime "
				+ "FROM ud_status AS us " + "INNER JOIN device_info AS ui " + "ON us.ud_id = ui.ud_id "
				+ "INNER JOIN ap_info AS ai " + "ON us.ap_id = ai.ap_id " + "WHERE ui.ud_use = 'Y' AND ai.ap_use = 'Y'";

		connectDatabase();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		return rs;
	}

	public ResultSet searchStatusTable(String columnName, String input) throws SQLException {
		String query = "SELECT us.ud_id, ui.ud_name, ai.ap_ssid, ai.ap_name, us.status_connection, us.status_lastConnTime "
				+ "FROM ud_status AS us " + "INNER JOIN device_info AS ui " + "ON us.ud_id = ui.ud_id "
				+ "INNER JOIN ap_info AS ai " + "ON us.ap_id = ai.ap_id " + "WHERE " + columnName + " " + " LIKE '%"
				+ input + "%' AND ui.ud_use = 'Y' AND ai.ap_use = 'Y'";

		connectDatabase();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		return rs;
	}

	public void updateDB(String ud_id, String ap_id, String status_conn) throws SQLException {
		String query = "INSERT INTO ud_status (ud_id, ap_id, status_connection, status_lastConnTime) " + "VALUES ('"
				+ ud_id + "', '" + ap_id + "', '" + status_conn + "', CURRENT_TIMESTAMP) ON DUPLICATE KEY "
				+ "UPDATE ap_id = '" + ap_id + "', status_connection = '" + status_conn
				+ "', status_lastConnTime = CURRENT_TIMESTAMP";

		connectDatabase();
		PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pstmt.execute();
	}

	public void updateDBALLClientDisconn() throws SQLException {
		String query = "UPDATE ud_status SET status_connection = 'N'";

		connectDatabase();
		PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pstmt.execute();
	}

	public ResultSet displayAPCnt() throws SQLException {
		String query = "SELECT ai.ap_ssid, ai.ap_name, COUNT(1) FROM ud_status as us "
				+ "INNER JOIN ap_info AS ai ON ai.ap_id = us.ap_id " + "WHERE ud_id IN "
				+ "( SELECT ud_id FROM device_info WHERE ud_use = 'Y' ) " + "AND ap_use = 'Y' "
				+ "GROUP BY ap_ssid, ap_name";

		connectDatabase();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		return rs;
	}

	public ResultSet searchAPCnt(String input) throws SQLException {
		String query = "SELECT ai.ap_ssid, ai.ap_name, COUNT(1) FROM ud_status as us "
				+ "INNER JOIN ap_info AS ai ON ai.ap_id = us.ap_id " + "WHERE ud_id IN "
				+ "( SELECT ud_id FROM device_info WHERE ud_name like '%" + input + "%' AND ud_use = 'Y' ) "
				+ "AND ap_use = 'Y' " + "GROUP BY ap_ssid, ap_name";

		connectDatabase();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		return rs;
	}

	public ResultSet expandChildList(String columnName) throws SQLException {
		String query = "SELECT us.ud_id, ui.ud_name, us.status_connection, us.status_lastConnTime "
				+ "FROM ud_status AS us " + "INNER JOIN device_info AS ui " + "ON us.ud_id = ui.ud_id "
				+ "INNER JOIN ap_info AS ai ON us.ap_id = ai.ap_id " + "WHERE ai.ap_ssid = '" + columnName + "' "
				+ "AND ui.ud_use = 'Y' AND ai.ap_use = 'Y' ";

		connectDatabase();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		return rs;
	}

	public ResultSet searchExpandChildList(String columnName, String input) throws SQLException {
		String query = "SELECT us.ud_id, ui.ud_name, us.status_connection, us.status_lastConnTime "
				+ "FROM ud_status AS us " + "INNER JOIN device_info AS ui " + "ON us.ud_id = ui.ud_id "
				+ "INNER JOIN ap_info AS ai ON us.ap_id = ai.ap_id " + "WHERE ai.ap_ssid = '" + columnName
				+ "' AND ui.ud_name LIKE '%" + input + "%'" + "AND ui.ud_use = 'Y' AND ai.ap_use = 'Y' ";

		connectDatabase();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		return rs;
	}
}