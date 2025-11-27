package com.example.windowUI.setting;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;

import com.example.MainWindow;

public class Bar2Top2 extends JPanel {
	private MainWindow main;

	private GridBagConstraints gbc = new GridBagConstraints();

	public void detectResizeWindow() {
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				repaint();
				revalidate();
			}
		});
	}

	public Bar2Top2(MainWindow m, ImageIcon img, JButton btn) {
		this.main = m;

		setBackground(new Color(236, 236, 237));
		setPreferredSize(new Dimension(main.getSize().width, 100));

		setLayout(new GridBagLayout());
		gbc.insets = new Insets(0, 30, 0, 35);
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;

		gbc.gridwidth = 1;
		JLabel lblPnlName = new JLabel(img);
		lblPnlName.setPreferredSize(new Dimension(55, 55));
		add(lblPnlName, gbc);

		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(Box.createHorizontalGlue(), gbc);

		gbc.weightx = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(45, 0, 0, 45);
		add(btn, gbc);

		detectResizeWindow();
	}
}
