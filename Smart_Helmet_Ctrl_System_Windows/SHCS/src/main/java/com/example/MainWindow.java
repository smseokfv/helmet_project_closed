package com.example;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.example.database.AuthWorker;
import com.example.database.SQLWorker;
import com.example.socket.SocketSvrWorker;
import com.example.windowUI.main.*;

import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import java.io.*;

public class MainWindow extends JFrame {
	public static void main(String[] args) {
		new MainWindow().setVisible(true);
	}

//	private String version = "(ver.1.1)"; // 최초 버전
//	private String version = "(ver.1.2)"; // 스트리밍 요청 시, 로그인 창 추가
	private String version = "(ver.1.3)"; // 헬멧에서 입력한 id,psw를 클라이언트 로그인 시 사용

	private String strPathLibVLC = "C:\\Program Files\\libvlc";

	public void setupLibVLC() {
		System.setProperty("jna.library.path", strPathLibVLC);
		System.setProperty("VLC_PLUGIN_PATH", strPathLibVLC + "/plugins");

		boolean found = new NativeDiscovery().discover();
		if (!found) {
			popupMsg(MainWindow.this, "VLC 라이브러리를 찾을 수 없습니다.");
			System.exit(1);
		}
	}

	public Font loadExternalFonts(String path) {
		InputStream is = MainWindow.class.getResourceAsStream(path);
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			return font;

		} catch (FontFormatException | IOException e) {
			popupMsg(MainWindow.this, "폰트를 불러오는데 실패했습니다.");
			System.exit(1);
			return null;
		}
	}

	public static void popupMsg(Component c, String msg) {
		JOptionPane.showMessageDialog(c, msg);
	}

	public JButton createTextButton(String inputText) {
		JButton btn = new JButton(inputText);

		btn.setBorder(null);

		btn.setFocusPainted(false);

		btn.setOpaque(false);
		btn.setBackground(new Color(230, 230, 230));

		btn.setBorder(new EmptyBorder(10, 10, 10, 10));

		btn.setContentAreaFilled(false);

		btn.setHorizontalAlignment(SwingConstants.LEFT);

		return btn;
	}

	public JButton createImgButton(int size, ImageIcon icon) {
		JButton btn = new JButton(icon);

		btn.setPreferredSize(new Dimension(size, size));

		btn.setBorder(null);

		btn.setFocusPainted(false);

		btn.setOpaque(false);
		btn.setBackground(new Color(0, 0, 0, 0));

		return btn;
	}

	public void locateWindow(Window a, Window b) {
		Point p = a.getLocation();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension pd = a.getSize();
		Dimension cd = b.getSize();

		if (p.x - cd.width < 0 && p.x + pd.width + cd.width > screenSize.width) {
			if (p.y < 0) {
				b.setLocation(0, 0);
			} else {
				b.setLocation(0, p.y);
			}

		} else if (p.x + pd.width + cd.width <= screenSize.width) {
			b.setLocation(p.x + pd.width, p.y);

		} else if (p.x - cd.width >= 0) {
			b.setLocation(p.x - cd.width, p.y);
		}
	}

	private int SOCKET_PORT = 5555;
	private int RTSP_PORT = 55555;

	private int numSocketResponseTimeout = 1000 * 7;

	private int MAX_CLIENT_CALL = 4;

	private int SelectedIndex_MainSidePnl = 1;

	private int currentSelectedIndex = SelectedIndex_MainSidePnl;

	private int ScrollMovement = 20;

	private int lmtUDNameLength = 10;

	private int lmtAPNameLength = 10;

	private int lmtAPSSIDLength = 32;

	public int getSocketPort() {
		return SOCKET_PORT;
	}

	public int getRTSPPort() {
		return RTSP_PORT;
	}

	public int getSocketResponseTimeout() {
		return numSocketResponseTimeout;
	}

	public int getMaxClientCall() {
		return MAX_CLIENT_CALL;
	}

	public int getSelectedIndex_MainSidePnl() {
		return SelectedIndex_MainSidePnl;
	}

	public void setCurrentSelectedIndex(int i) {
		currentSelectedIndex = i;
	}

	public int getCurrentSelectedIndex() {
		return currentSelectedIndex;
	}

	public int getScrollMovement() {
		return ScrollMovement;
	}

	public int getLmtUDNameLength() {
		return lmtUDNameLength;
	}

	public int getLmtAPNameLength() {
		return lmtAPNameLength;
	}

	public int getLmtAPSSIDLength() {
		return lmtAPSSIDLength;
	}

	private Map<String, JFrame> mapCallWindow = new HashMap<>();

	public Map<String, JFrame> getMapCallWindow() {
		return mapCallWindow;
	}

	private Map<String, String> mapAP_SSID = new HashMap<>();

	public Map<String, String> getMapAP_SSID() {
		return mapAP_SSID;
	}

	// 사용자 인증 정보 저장 (USER_ID -> 로그인 정보)
	private static class UserLoginInfo {
		private final String helmetId;
		private final String password;

		private UserLoginInfo(String helmetId, String password) {
			this.helmetId = helmetId;
			this.password = password;
		}

		public String getHelmetId() {
			return helmetId;
		}

		public String getPassword() {
			return password;
		}
	}

	private Map<String, UserLoginInfo> userLoginList = new HashMap<>();

	public Map<String, UserLoginInfo> getUserLoginList() {
		return userLoginList;
	}

	// 사용자 인증 정보 추가 (userId 중복 체크)
	public void addUserLogin(String helmetId, String userId, String userPassword) {
		System.out.println("[addUserLogin 호출] helmetId: \"" + helmetId + "\", userId: \"" + userId + "\", userPassword: \"" + userPassword + "\"");

		boolean hasHelmetId = helmetId != null && !helmetId.isEmpty();
		boolean hasUserId = userId != null && !userId.isEmpty();
		boolean hasPassword = userPassword != null && !userPassword.isEmpty();

		if (hasHelmetId && hasUserId && hasPassword) {
			// userId가 이미 리스트에 있으면 추가하지 않음
			if (!userLoginList.containsKey(userId)) {
				userLoginList.put(userId, new UserLoginInfo(helmetId, userPassword));
				System.out.println("[사용자 인증 정보 추가] USER_ID: " + userId + ", 저장되는 값: \"" + userId + ":" + helmetId + ":" + userPassword + "\"");
			} else {
				UserLoginInfo existing = userLoginList.get(userId);
				System.out.println("[사용자 인증 정보] 이미 존재함 - USER_ID: " + userId + ", 기존 헬멧 ID: " + existing.getHelmetId());
			}

			// 리스트 전체를 한 줄로 출력
			StringBuilder listStr = new StringBuilder();
			for (Map.Entry<String, UserLoginInfo> entry : userLoginList.entrySet()) {
				if (listStr.length() > 0) {
					listStr.append(", ");
				}
				UserLoginInfo info = entry.getValue();
				listStr.append(info.getHelmetId())
				       .append(":")
				       .append(entry.getKey())
				       .append(":")
				       .append(info.getPassword());
			}
			System.out.println("[리스트 전체] (" + userLoginList.size() + "개): " + listStr.toString());
		} else {
			System.out.println("[addUserLogin] 조건 실패 - helmetId: " + helmetId + ", userId: " + userId + ", userPassword: " + userPassword);
		}
	}

	// 사용자 인증 확인 (입력한 헬멧 아이디, 사용자 아이디, 비밀번호가 모두 일치하는지 확인)
	public boolean checkUserLogin(String helmetId, String userId, String userPassword) {
		System.out.println("[checkUserLogin 호출] helmetId: \"" + helmetId + "\", userId: \"" + userId + "\", userPassword: \"" + userPassword + "\"");
		
		if (helmetId == null || helmetId.isEmpty()) {
			System.out.println("[checkUserLogin] helmetId가 null이거나 비어있음");
			return false;
		}
		
		if (userId == null || userId.isEmpty()) {
			System.out.println("[checkUserLogin] userId가 null이거나 비어있음");
			return false;
		}
		
		if (userPassword == null || userPassword.isEmpty()) {
			System.out.println("[checkUserLogin] userPassword가 null이거나 비어있음");
			return false;
		}
		
		UserLoginInfo storedInfo = userLoginList.get(userId);
		boolean idMatches = storedInfo != null && helmetId.equals(storedInfo.getHelmetId());
		boolean passwordMatches = storedInfo != null && storedInfo.getPassword().equals(userPassword);
		boolean isValid = idMatches && passwordMatches;
		
		System.out.println("[checkUserLogin] 아이디 존재 여부: " + (storedInfo != null)
				+ ", 헬멧 ID 일치 여부: " + idMatches
				+ ", 비밀번호 일치 여부: " + passwordMatches
				+ ", 저장된 헬멧 ID: " + (storedInfo != null ? storedInfo.getHelmetId() : "없음"));
		
		// 현재 리스트 전체 출력
		StringBuilder listStr = new StringBuilder();
		for (Map.Entry<String, UserLoginInfo> entry : userLoginList.entrySet()) {
			if (listStr.length() > 0) {
				listStr.append(", ");
			}
			UserLoginInfo info = entry.getValue();
			listStr.append(entry.getKey())
			       .append(":")
			       .append(info.getHelmetId())
			       .append(":")
			       .append(info.getPassword());
		}
		System.out.println("[checkUserLogin] 현재 리스트 (" + userLoginList.size() + "개): " + listStr.toString());
		
		return isValid;
	}

	private RTSPWorker rtspWorker;

	public RTSPWorker getRtspWorker() {
		return rtspWorker;
	}

	private SocketSvrWorker sockSvrWorker;

	public SocketSvrWorker getSockSvrWorker() {
		return sockSvrWorker;
	}

	private SQLWorker sqlWorker;

	public SQLWorker getSqlWorker() {
		return sqlWorker;
	}

	private ScheduledExecutorService scheduledWorker = Executors.newSingleThreadScheduledExecutor();

	public ScheduledExecutorService getScheduledWorker() {
		return scheduledWorker;
	}

	private JPanel pnlLeft;

	private JPanel pnlRight;
	private JPanel pnlUD;
	private JPanel pnlAP;
	private JDialog dlgSettingWindow = new SettingWindow(MainWindow.this);

	public JPanel getPnlRight() {
		return pnlRight;
	}

	public JPanel getPnlUD() {
		return pnlUD;
	}

	public JPanel getPnlAP() {
		return pnlAP;
	}

	public JDialog getDlgSettingWindow() {
		return dlgSettingWindow;
	}

	public MainWindow() {
		setupLibVLC();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			setIconImage(ImageIO.read(MainWindow.class.getResource("/icon/main/icon_UD_Selected.png")));

		} catch (IOException e) {
			setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE));
		}

		setSize(590, 970);
		setMinimumSize(new Dimension(590, 970));

		setBackground(new Color(218, 218, 218));

		setLocationRelativeTo(null);

		setTitle("스마트 헬멧 관제 시스템 " + version);

		JPanel pnlBottom = new JPanel(new BorderLayout());
		pnlBottom.setBackground(new Color(248, 248, 248));

		Bar0Bottom barBottom = new Bar0Bottom(MainWindow.this);
		pnlBottom.add(barBottom, BorderLayout.CENTER);

		add(pnlBottom, BorderLayout.SOUTH);

		pnlLeft = new Bar1Left(MainWindow.this);
		add(pnlLeft, BorderLayout.WEST);

		pnlRight = new JPanel(new CardLayout());
		pnlUD = new Pnl4List_UD(MainWindow.this);
		pnlAP = new Pnl4List_AP(MainWindow.this);

		if (SelectedIndex_MainSidePnl == 1) {
			pnlRight.add(pnlUD, "Activated User Device Panel");
			pnlRight.add(pnlAP, "Activated Access Point Panel");

		} else if (SelectedIndex_MainSidePnl == 2) {
			pnlRight.add(pnlAP, "Access Point Panel");
			pnlRight.add(pnlUD, "User Device Panel");
		}

		add(pnlRight, BorderLayout.CENTER);

		rtspWorker = new RTSPWorker(MainWindow.this);

		sqlWorker = new SQLWorker();

		sockSvrWorker = new SocketSvrWorker(MainWindow.this);

		AuthWorker authWorker = new AuthWorker(MainWindow.this);
		authWorker.checkRegValue();

		Runnable task = () -> {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						rtspWorker.startStreaming();

						if (currentSelectedIndex == 1) {
							Pnl4List_UD udStatusList = (Pnl4List_UD) pnlUD;
							udStatusList.displayConnStatus();

						} else if (currentSelectedIndex == 2) {
							Pnl4List_AP apSortedList = (Pnl4List_AP) pnlAP;
							apSortedList.displayListSortedByAP();
						}
					} catch (Exception e) {
					}
				}
			});
		};
		scheduledWorker.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					sockSvrWorker.stopClientStreamingAll();
					sqlWorker.updateDBALLClientDisconn();

				} catch (SQLException e1) {

				} finally {
					System.exit(0);
				}
			}
		});
	}
}
