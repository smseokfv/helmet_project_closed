package com.example.windowUI.setting;

import static com.example.MainWindow.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.sql.*;

import com.example.MainWindow;
import com.example.SettingWindow;
import com.example.database.SQLWorker;
import com.example.windowUI.main.Pnl4List_AP;
import com.example.windowUI.main.Pnl4List_UD;

public class Pnl3Cell_AP2 extends JPanel {
	private MainWindow main;
	private Pnl4List_AP2 pnlList;

	private String strID;
	private String strName;
	private String strSSID;
	private String strUse;

	private JLabel lblId;
	private JLabel lblName;
	private JLabel lblSSID;
	private JLabel lblDate;
	private JButton btnCheckUse;

	private JPopupMenu menuRightClick;
	private JMenuItem itemModify;
	private JMenuItem itemDelete;

	public void setMouseEvent() {
		addMouseListener(new MouseAdapter() {
			Pnl4List_AP2 apList = (Pnl4List_AP2) ((SettingWindow) main.getDlgSettingWindow()).getPnlAP();

			@Override
			public void mouseEntered(MouseEvent e) {
				if (Pnl3Cell_AP2.this != apList.getPnlSelected())
					setBackground(new Color(230, 230, 230));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (Pnl3Cell_AP2.this != apList.getPnlSelected())
					setBackground(Color.WHITE);
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
		Pnl4List_AP2 apList = (Pnl4List_AP2) ((SettingWindow) main.getDlgSettingWindow()).getPnlAP();

		itemModify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Win5Modify_AP2 mw = new Win5Modify_AP2(main, strID, strName, strSSID);
				mw.setVisible(true);
			}
		});

		itemDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(main.getDlgSettingWindow(),
						"\'" + strName + "(" + strSSID + ")\'\nAP를 정말로 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);

				if (response == JOptionPane.YES_OPTION) {
					try {
						String[] arrInput = { strID };
						sql.deleteTable("AP", arrInput);

						String msg = "\'" + strName + "(" + strSSID + ")\'\nAP를 삭제했습니다.";
						popupMsg(main.getDlgSettingWindow(), msg);

						apList.display();

					} catch (Exception e2) {
						popupMsg(main.getDlgSettingWindow(), "AP 삭제 실패\n : " + e2.getMessage());
					}
				}
			}
		});
	}

	public void setCheckButtonEvent() {
		btnCheckUse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SQLWorker sql = main.getSqlWorker();
				if (strUse.equals("Y")) {
					String[] arrInput = { "N", strID };
					sql.changeUsable("AP", arrInput);

				} else if (strUse.equals("N")) {
					String[] arrInput = { "Y", strID };
					sql.changeUsable("AP", arrInput);
				}

				pnlList.display();

				int currentDisplayIndex = main.getCurrentSelectedIndex();
				Pnl4List_UD udStatusList = (Pnl4List_UD) main.getPnlUD();
				Pnl4List_AP apSortedList = (Pnl4List_AP) main.getPnlAP();

				if (currentDisplayIndex == 1) {
					udStatusList.displayConnStatus();

				} else if (currentDisplayIndex == 2) {
					apSortedList.displayListSortedByAP();
				}
			}
		});
	}

	public Pnl3Cell_AP2(MainWindow m, ResultSet rs, Pnl4List_AP2 p) throws SQLException {
		this.main = m;

		this.pnlList = p;

		strID = rs.getString(1);
		strName = rs.getString(2);
		strSSID = rs.getString(3);
		strUse = rs.getString(4);

		setBackground(Color.white);

		Font fontSet = main.loadExternalFonts("/font/NanumSquareRoundEB.ttf");
		fontSet = fontSet.deriveFont(20f);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		lblName = new JLabel(strName, SwingConstants.LEFT);
		lblName.setFont(fontSet);
		lblName.setForeground(new Color(0, 0, 0));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(30, 30, 10, 0);
		add(lblName, gbc);

		lblSSID = new JLabel(strSSID, SwingConstants.LEFT);
		lblSSID.setFont(fontSet);
		lblSSID.setForeground(new Color(172, 173, 177));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 30, 30, 0);
		add(lblSSID, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel(), gbc);
		gbc.weightx = 0;
		gbc.weighty = 0;

		btnCheckUse = main.createImgButton(35, null);
		btnCheckUse.setContentAreaFilled(false);
		if (strUse.equals("Y")) {
			btnCheckUse.setIcon(pnlList.getIconAPCheckUse());
		} else if (strUse.equals("N")) {
			btnCheckUse.setIcon(pnlList.getIconAPCheckUnuse());
		}
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.insets = new Insets(35, 0, 35, 30);
		add(btnCheckUse, gbc);

		menuRightClick = new JPopupMenu();
		itemModify = new JMenuItem("변경");
		itemDelete = new JMenuItem("삭제");
		menuRightClick.add(itemModify);
		menuRightClick.add(itemDelete);

		setMouseEvent();
		setRightClickEvent();
		setCheckButtonEvent();
	}
}
