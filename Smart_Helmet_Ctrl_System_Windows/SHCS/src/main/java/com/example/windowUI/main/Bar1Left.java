package com.example.windowUI.main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.example.MainWindow;
import com.example.SettingWindow;
import com.example.windowUI.setting.Pnl4List_UD2;

public class Bar1Left extends JPanel {
	private MainWindow main;

	private ImageIcon iconUDSelected = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_UD_Selected.png"));
	private ImageIcon iconUDUnSelected = new ImageIcon(
			MainWindow.class.getResource("/icon/main/icon_UD_UnSelected.png"));
	private ImageIcon iconAPSelected = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_AP_Selected.png"));
	private ImageIcon iconAPUnSelected = new ImageIcon(
			MainWindow.class.getResource("/icon/main/icon_AP_UnSelected.png"));
	private ImageIcon iconSetting = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_setup.png"));

	private GridBagConstraints gbc = new GridBagConstraints();

	private JButton btnDisplayUDList;
	private JButton btnDisplayAPList;
	private JButton btnDisplaySettingWindow;

	public void setButtonEvent() {
		btnDisplayUDList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) main.getPnlRight().getLayout();
				cl.show(main.getPnlRight(), "Activated User Device Panel");

				btnDisplayUDList.setIcon(iconUDSelected);
				btnDisplayAPList.setIcon(iconAPUnSelected);

				main.setCurrentSelectedIndex(1);

				Pnl4List_UD udStatusList = (Pnl4List_UD) main.getPnlUD();
				udStatusList.displayConnStatus();
			}
		});

		btnDisplayAPList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) main.getPnlRight().getLayout();
				cl.show(main.getPnlRight(), "Activated Access Point Panel");

				btnDisplayUDList.setIcon(iconUDUnSelected);
				btnDisplayAPList.setIcon(iconAPSelected);

				main.setCurrentSelectedIndex(2);

				Pnl4List_AP apSortedList = (Pnl4List_AP) main.getPnlAP();
				apSortedList.displayListSortedByAP();
			}
		});

		btnDisplaySettingWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingWindow objSW = (SettingWindow) main.getDlgSettingWindow();
				Pnl4List_UD2 udList = (Pnl4List_UD2) objSW.getPnlUD();

				main.locateWindow(main, objSW);

				if (!objSW.isVisible()) {
					objSW.setVisible(true);

					udList.display();

				} else {
					objSW.toFront();
				}
			}
		});
	}

	public Bar1Left(MainWindow m) {
		this.main = m;

		setPreferredSize(new Dimension(100, main.getSize().height));
		setBackground(new Color(236, 236, 237));

		setLayout(new GridBagLayout());
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		btnDisplayUDList = main.createImgButton(60, iconUDUnSelected);
		gbc.insets = new Insets(40, 0, 0, 0);
		add(btnDisplayUDList, gbc);

		btnDisplayAPList = main.createImgButton(60, iconAPUnSelected);
		gbc.insets = new Insets(40, 0, 0, 0);
		add(btnDisplayAPList, gbc);

		gbc.weighty = 1;
		add(Box.createVerticalGlue(), gbc);
		gbc.weighty = 0;

		btnDisplaySettingWindow = main.createImgButton(55, iconSetting);
		gbc.insets = new Insets(0, 0, 35, 0);
		add(btnDisplaySettingWindow, gbc);

		if (main.getSelectedIndex_MainSidePnl() == 1) {
			btnDisplayUDList.setIcon(iconUDSelected);

		} else if (main.getSelectedIndex_MainSidePnl() == 2) {
			btnDisplayAPList.setIcon(iconAPSelected);
		}

		setButtonEvent();
	}
}
