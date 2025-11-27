package com.example.windowUI.main;

import static com.example.MainWindow.*;
import com.example.MainWindow;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.sql.*;

public class Pnl3Cell_AP_Parent extends JPanel {
	private MainWindow main;

	private ImageIcon iconExpanded = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_expand.png"));
	private ImageIcon iconUnExpanded = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_expand_not.png"));

	private String strAP_SSID;
	private String strAP_Name;
	private int intLocCount;

	public String getStrAP_SSID() {
		return strAP_SSID;
	}

	private GridBagConstraints gbc = new GridBagConstraints();

	private JLabel lblExpandStatus;
	private JLabel lblAPName;
	private JLabel lblAPSSID;
	private JLabel lblLocCount;

	public void setMouseEvent() {
		addMouseListener(new MouseAdapter() {
			Pnl4List_AP apSortedList = (Pnl4List_AP) main.getPnlAP();

			@Override
			public void mouseEntered(MouseEvent e) {
				setBackground(new Color(230, 230, 230));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setBackground(Color.WHITE);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!main.getMapAP_SSID().containsKey(strAP_SSID)) {
					main.getMapAP_SSID().put(strAP_SSID, "o");
					lblExpandStatus.setText("<html><font size='5'>-</font></html>");
					apSortedList.displayListSortedByAP();

				} else {
					main.getMapAP_SSID().remove(strAP_SSID);
					lblExpandStatus.setText("<html><font size='5'>+</font></html>");
					apSortedList.displayListSortedByAP();
				}
				repaint();
				revalidate();
			}
		});
	}

	public Pnl3Cell_AP_Parent(MainWindow m, ResultSet rs) throws SQLException {
		this.main = m;

		Font fontSet = main.loadExternalFonts("/font/NanumSquareRoundEB.ttf");
		fontSet = fontSet.deriveFont(24f);

		strAP_SSID = rs.getString(1);
		strAP_Name = rs.getString(2);
		intLocCount = rs.getInt(3);

		setBackground(Color.white);

		setLayout(new GridBagLayout());

		if (main.getMapAP_SSID().containsKey(strAP_SSID)) {
			lblExpandStatus = new JLabel(iconExpanded);

		} else {
			lblExpandStatus = new JLabel(iconUnExpanded);
		}
		lblExpandStatus.setPreferredSize(new Dimension(25, 25));
		gbc.insets = new Insets(30, 30, 30, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(lblExpandStatus, gbc);

		lblAPName = new JLabel(strAP_Name, SwingConstants.LEFT);
		lblAPName.setFont(fontSet);
		lblAPName.setForeground(new Color(0, 0, 0));
		gbc.insets = new Insets(0, 15, 0, 0);
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(lblAPName, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel(), gbc);
		gbc.weightx = 0;

		lblLocCount = new JLabel("(" + intLocCount + ")", SwingConstants.RIGHT);
		lblLocCount.setFont(fontSet);
		lblLocCount.setForeground(new Color(0, 0, 0));
		gbc.insets = new Insets(0, 0, 0, 30);
		gbc.gridx = 3;
		gbc.gridy = 0;
		add(lblLocCount, gbc);

		setMouseEvent();
	}
}
