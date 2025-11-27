package com.example.database;

import static com.example.MainWindow.*;
import com.example.MainWindow;
import com.example.socket.SocketSvrWorker;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import java.sql.*;

public class AuthWorker {
//	private String userName = "BF17.110OSL001"; // 최초 1.1버전
	private String userName = "BK12.102OSL002"; // 2025.11.12 로그인창 추가 1.2 버전

	private MainWindow main;

	public AuthWorker(MainWindow m) {
		this.main = m;
	}

	private String url = "jdbc:mysql://1.224.254.86:3306/auth_manage";
	private String un = "smart_helmet";
	private String pw = "qwe123!@#";
	private Connection conn = null;

	private void connectDatabase() throws SQLException {
		if (conn == null) {
			conn = DriverManager.getConnection(url, un, pw);
		}
	}

	public String getStringFromDB(String input) throws SQLException {
		String query = "SELECT USER_CODE FROM prj5_monitor_info WHERE USER_CODE = '" + input + "' AND AUTH = 'W'";

		connectDatabase();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		if (rs.next()) {
			return rs.getString(1);
		}
		return null;
	}

	public void updateDB(String[] input) throws SQLException {
		String query = "UPDATE prj5_monitor_info SET COMPUTER_NAME = '" + input[1] + "', IP_ADDRESS = '" + input[2]
				+ "', UPDATE_DATE = CURRENT_TIMESTAMP, AUTH = 'C' WHERE USER_CODE = '" + input[0] + "'";

		connectDatabase();
		PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pstmt.execute();
	}

	public void checkRegValue() {
		SocketSvrWorker svr = main.getSockSvrWorker();

		try {
			String getRegValue = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\SHCS",
					"RegisteredName");

			if (!getRegValue.equals(userName)) {
				throw new Exception("userName과 일치하지 않습니다.");

			} else {
				Thread.sleep(100);
				main.getSqlWorker().logUseHistory(getRegValue, svr.getComName(), svr.getIpAddress());
			}

		} catch (Exception e) {
			if (e.getMessage().equals("userName과 일치하지 않습니다.")) {
				popupMsg(main, "사용자 인증에 실패했습니다.");
				System.exit(1);

			} else {
				requestRegValue();
			}
		}
	}

	public void requestRegValue() {
		SocketSvrWorker svr = main.getSockSvrWorker();

		try {
			String req = getStringFromDB(userName);

			if (req == null || !req.equals(userName)) {
				popupMsg(main, "사용자 인증에 실패했습니다.\n(등록되지 않았거나 이미 사용되었습니다)");
				System.exit(1);
			}

			String[] input = { userName, svr.getComName(), svr.getIpAddress() };
			updateDB(input);

			Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "Software\\SHCS");
			Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\SHCS", "RegisteredName", req);

			main.getSqlWorker().logUseHistory(req, svr.getComName(), svr.getIpAddress());

		} catch (Exception e) {
			popupMsg(main, "사용자 인증에 실패했습니다.\n(인터넷 연결상태를 확인해주세요)");
			System.exit(1);
		}
	}
}
