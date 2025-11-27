package com.example;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.example.windowUI.setting.*;

public class SettingWindow extends JDialog {
	private MainWindow main;

	private JPanel pnlLeft;

	private JPanel pnlRight;
	private JPanel pnlUD;
	private JPanel pnlAP;

	public JPanel getPnlRight() {
		return pnlRight;
	}

	public JPanel getPnlUD() {
		return pnlUD;
	}

	public JPanel getPnlAP() {
		return pnlAP;
	}

	public void detectResizeWindow() {
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {

			}
		});
	}

	public SettingWindow(MainWindow m) {
		super(m, false);

		this.main = m;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("설정");
		setSize(770, 960);
		setResizable(false);

		pnlLeft = new Bar1Left2(main);
		add(pnlLeft, BorderLayout.WEST);

		pnlRight = new JPanel(new CardLayout());
		pnlUD = new Pnl4List_UD2(main);
		pnlAP = new Pnl4List_AP2(main);
		pnlRight.add(pnlUD, "Registered User Device Panel");
		pnlRight.add(pnlAP, "Registered Access Point Panel");
		add(pnlRight, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				((Bar1Left2) pnlLeft).initSelectedPnl();
			}
		});

		detectResizeWindow();
	}
}
