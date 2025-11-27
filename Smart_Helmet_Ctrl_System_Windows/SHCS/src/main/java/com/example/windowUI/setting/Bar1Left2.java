package com.example.windowUI.setting;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;

import com.example.MainWindow;
import com.example.SettingWindow;

import static com.example.MainWindow.*;

public class Bar1Left2 extends JPanel {
	private MainWindow main;

	private ImageIcon iconUDSelected = new ImageIcon(
			MainWindow.class.getResource("/icon/setting/icon_UD_Selected.png"));
	private ImageIcon iconUDUnSelected = new ImageIcon(
			MainWindow.class.getResource("/icon/setting/icon_UD_UnSelected.png"));
	private ImageIcon iconAPSelected = new ImageIcon(
			MainWindow.class.getResource("/icon/setting/icon_AP_Selected.png"));
	private ImageIcon iconAPUnSelected = new ImageIcon(
			MainWindow.class.getResource("/icon/setting/icon_AP_UnSelected.png"));
	private ImageIcon iconSetting = new ImageIcon(MainWindow.class.getResource("/icon/setting/icon_setup.png"));

	private GridBagConstraints gbc = new GridBagConstraints();

	private JButton btnDisplayUDList;
	private JButton btnDisplayAPList;

	public void initSelectedPnl() {
		SettingWindow objSW = (SettingWindow) main.getDlgSettingWindow();

		CardLayout cl = (CardLayout) objSW.getPnlRight().getLayout();
		cl.show(objSW.getPnlRight(), "Registered User Device Panel");

		btnDisplayUDList.setIcon(iconUDSelected);
		btnDisplayAPList.setIcon(iconAPUnSelected);
	}

	public void setButtonEvent() {
		btnDisplayUDList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingWindow objSW = (SettingWindow) main.getDlgSettingWindow();
				Pnl4List_UD2 udList = (Pnl4List_UD2) objSW.getPnlUD();

				CardLayout cl = (CardLayout) objSW.getPnlRight().getLayout();
				cl.show(objSW.getPnlRight(), "Registered User Device Panel");

				btnDisplayUDList.setIcon(iconUDSelected);
				btnDisplayAPList.setIcon(iconAPUnSelected);

				udList.display();
			}
		});

		btnDisplayAPList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingWindow objSW = (SettingWindow) main.getDlgSettingWindow();
				Pnl4List_AP2 apList = (Pnl4List_AP2) objSW.getPnlAP();

				CardLayout cl = (CardLayout) objSW.getPnlRight().getLayout();
				cl.show(objSW.getPnlRight(), "Registered Access Point Panel");

				btnDisplayUDList.setIcon(iconUDUnSelected);
				btnDisplayAPList.setIcon(iconAPSelected);

				apList.display();
			}
		});
	}

	public Bar1Left2(MainWindow m) {
		this.main = m;

		setPreferredSize(new Dimension(155, main.getSize().height));
		setBackground(new Color(236, 236, 237));

		setLayout(new GridBagLayout());
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		btnDisplayUDList = main.createImgButton(55, iconUDSelected);
		gbc.insets = new Insets(155, 0, 0, 0);
		add(btnDisplayUDList, gbc);

		btnDisplayAPList = main.createImgButton(55, iconAPUnSelected);
		gbc.insets = new Insets(70, 0, 0, 0);
		add(btnDisplayAPList, gbc);

		gbc.weighty = 1;
		add(Box.createVerticalGlue(), gbc);
		gbc.weighty = 0;

		setButtonEvent();
	}
}
