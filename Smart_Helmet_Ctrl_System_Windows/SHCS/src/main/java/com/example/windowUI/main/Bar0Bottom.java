package com.example.windowUI.main;

import java.awt.*;
import javax.swing.*;

import com.example.MainWindow;

public class Bar0Bottom extends JPanel {
	private MainWindow main;

	private ImageIcon iconBottomLogo = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_bottom_logo.png"));
	private Image img = iconBottomLogo.getImage();

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int pw = getSize().width;
		int ph = getSize().height;
		int tw = img.getWidth(this);
		int th = img.getHeight(this);

		g.drawImage(img, (pw - tw) / 2, (ph - th) / 2, tw, th, this);
	}

	public Bar0Bottom(MainWindow m) {
		this.main = m;

		setPreferredSize(new Dimension(m.getSize().width, 145));
		setBackground(new Color(248, 248, 248));
	}
}
