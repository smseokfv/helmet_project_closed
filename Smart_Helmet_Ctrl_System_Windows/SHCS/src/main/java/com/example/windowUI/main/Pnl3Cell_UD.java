package com.example.windowUI.main;

import static com.example.MainWindow.*;
import com.example.MainWindow;
import com.example.RTSPWorker;
import com.example.SettingWindow;
import com.example.database.SQLWorker;
import com.example.windowUI.loginUI.LoginDialog;
import com.example.windowUI.setting.Pnl3Cell_UD2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.sql.*;
import java.util.Map;

public class Pnl3Cell_UD extends JPanel {
	private MainWindow main;

	private ImageIcon iconConnected = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_device_ON.png"));
	private ImageIcon iconDisConnected = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_device_OFF.png"));

	private String strUD_ID;
	private String strUD_Name;
	private String strStatus_conn;
	

	public String getStrUD_ID() {
		return strUD_ID;
	}

	public String getStrUD_Name() {
		return strUD_Name;
	}

	public String getStatus_conn() {
		return strStatus_conn;
	}

	private GridBagConstraints gbc = new GridBagConstraints();

	private JLabel lblConnStatus;
	private JLabel lblUDName;
	private JLabel lblAPName;
	private JLabel lblLastConnDate;

	private JPopupMenu menuRightClick;
	private JMenuItem itemDelete;

	public void setMouseEvent() {
		addMouseListener(new MouseAdapter() {
			Pnl4List_UD udStatusList = (Pnl4List_UD) main.getPnlUD();

			@Override
			public void mouseEntered(MouseEvent e) {
				if (Pnl3Cell_UD.this != udStatusList.getPnlSelected())
					setBackground(new Color(230, 230, 230));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (Pnl3Cell_UD.this != udStatusList.getPnlSelected()) {
					setBackground(Color.WHITE);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					 Map<String, JFrame> callWins = main.getMapCallWindow();
					 
					if (callWins.size() < main.getMaxClientCall() && !callWins.containsKey(strUD_ID)
							&& strStatus_conn.equals("Y")) {

						// 로그인 창 표시
						LoginDialog dialog = new LoginDialog(main);
						dialog.setVisible(true);
						
						// 로그인 성공 시에만 스트리밍 시작
						if (!dialog.isAuthenticated()) {
							return;
						}

						Win5CallUD callWin = new Win5CallUD(main, Pnl3Cell_UD.this.getStrUD_Name(),
								Pnl3Cell_UD.this.getStrUD_ID());

						callWin.setVisible(true);

						main.locateWindow(main, callWin);

						callWins.put(strUD_ID, callWin);

						main.getSockSvrWorker().startStreaming(strUD_ID);

					} else if (callWins.containsKey(strUD_ID)) {
						popupMsg(main, "이미 해당 클라이언트 화면이 표시되고 있습니다.");
						callWins.get(strUD_ID).toFront();

					} else if (callWins.size() >= main.getMaxClientCall()) {
						popupMsg(main, "현재 최대 " + main.getMaxClientCall() + "개의 클라이언트 화면만 표시할 수 있습니다.");

					} else if (!strStatus_conn.equals("Y")) {
						popupMsg(main, "연결되어있지 않은 클라이언트는 영상을 호출할 수 없습니다.");
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					menuRightClick.show(e.getComponent(), e.getX() + 1, e.getY() + 1);
				}
			}
		});
	}

	public void setRightClickEvent() {
		SQLWorker sql = main.getSqlWorker();
		Pnl4List_UD udStatusList = (Pnl4List_UD) main.getPnlUD();

		itemDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(main, "선택하신 목록을 정말로 삭제하시겠습니까?", "확인",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (response == JOptionPane.YES_OPTION) {
					try {
						String[] arrInput = { strUD_ID };
						sql.deleteTable2(arrInput);

						udStatusList.displayConnStatus();

						popupMsg(main, "목록 삭제를 완료했습니다.");

					} catch (Exception e2) {
						popupMsg(main, "선택하신 목록 제거에 실패했습니다.\n : " + e2.getMessage());
					}
				}
			}
		});
	}

	public Pnl3Cell_UD(MainWindow m, ResultSet rs) throws SQLException {
		this.main = m;

		strUD_ID = rs.getString(1);
		strUD_Name = rs.getString(2);
		String AP_Name = rs.getString(4);
		String AP_SSID = rs.getString(3);
		strStatus_conn = rs.getString(5);
		String last_conn_time = rs.getString(6);

		setBackground(Color.white);

		Font fontSet = main.loadExternalFonts("/font/NanumSquareRoundEB.ttf");
		fontSet = fontSet.deriveFont(20f);

		setLayout(new GridBagLayout());

		lblConnStatus = new JLabel();
		lblConnStatus.setHorizontalAlignment(SwingConstants.LEFT);
		if (strStatus_conn.equals("Y")) {
			lblConnStatus.setIcon(iconConnected);
		} else if (strStatus_conn.equals("N")) {
			lblConnStatus.setIcon(iconDisConnected);
		}
		lblConnStatus.setPreferredSize(new Dimension(25, 25));
		gbc.insets = new Insets(30, 30, 30, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(lblConnStatus, gbc);

		lblUDName = new JLabel(strUD_Name, SwingConstants.LEFT);
		lblUDName.setFont(fontSet);
		lblUDName.setForeground(new Color(0, 0, 0));
		gbc.insets = new Insets(0, 15, 0, 0);
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(lblUDName, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel(), gbc);
		gbc.weightx = 0;

		lblAPName = new JLabel(AP_Name, SwingConstants.RIGHT);
		lblAPName.setFont(fontSet);
		lblAPName.setForeground(new Color(172, 173, 177));
		gbc.insets = new Insets(0, 0, 0, 30);
		gbc.gridx = 3;
		gbc.gridy = 0;
		add(lblAPName, gbc);

		menuRightClick = new JPopupMenu();
		itemDelete = new JMenuItem("삭제");
		menuRightClick.add(itemDelete);

		setMouseEvent();
		setRightClickEvent();
	}
}
