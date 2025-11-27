package com.example.windowUI.main;

import static com.example.MainWindow.*;
import com.example.MainWindow;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Bar2Top extends JPanel {
	private MainWindow main;

	private ImageIcon iconTopText = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_text_helmet.png"));

	private GridBagConstraints gbc = new GridBagConstraints();

	public void detectResizeWindow() {
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				repaint();
				revalidate();
			}
		});
	}

	public Bar2Top(MainWindow m, String strPnlName, JButton btn, JPanel pnlSearch) {
		this.main = m;

		setBackground(new Color(236, 236, 237));

		setLayout(new GridBagLayout());
		gbc.insets = new Insets(35, 10, 0, 35);

		JLabel lblPnlName = new JLabel(iconTopText);
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(lblPnlName, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel(), gbc);
		gbc.weightx = 0;

		gbc.gridx = 2;
		gbc.gridy = 0;
		add(btn, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnlSearch.setBorder(new EmptyBorder(20, 20, 0, 30));
		gbc.insets = new Insets(0, 0, 0, 0);
		add(pnlSearch, gbc);
		pnlSearch.setVisible(false);

		detectResizeWindow();
	}
}
