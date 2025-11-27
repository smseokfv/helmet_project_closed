package com.example.windowUI.setting;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.sql.*;

import com.example.CustomScrollBar;
import com.example.MainWindow;
import static com.example.MainWindow.*;

public class Pnl4List_UD2 extends JPanel {
	private MainWindow main;

	private ImageIcon iconSetting = new ImageIcon(MainWindow.class.getResource("/icon/setting/icon_setup.png"));
	private ImageIcon iconUDAddList = new ImageIcon(MainWindow.class.getResource("/icon/setting/icon_plus.png"));
	private ImageIcon iconUDCheckUse = new ImageIcon(MainWindow.class.getResource("/icon/setting/icon_use.png"));
	private ImageIcon iconUDCheckUnuse = new ImageIcon(MainWindow.class.getResource("/icon/setting/icon_unuse.png"));

	public ImageIcon getIconUDCheckUse() {
		return iconUDCheckUse;
	}

	public ImageIcon getIconUDCheckUnuse() {
		return iconUDCheckUnuse;
	}

	private int bakScrollPosition;

	private GridBagConstraints gbc = new GridBagConstraints();

	private JPanel pnlTop;
	private JButton btnAddUD;

	private JPanel pnlUDList = new JPanel();
	private JScrollPane scrollPnlUDList = new JScrollPane(pnlUDList);

	private JPanel pnlSelected = null;

	private Win5Add_UD2 dlgAddUDWindow;

	public void setPnlSelected(JPanel p) {
		pnlSelected = p;
	}

	public JScrollPane getScrollPnlUDList() {
		return scrollPnlUDList;
	}

	public JPanel getPnlSelected() {
		return pnlSelected;
	}

	public Win5Add_UD2 getDlgAddUDWindow() {
		return dlgAddUDWindow;
	}

	public void setAddUDButton() {
		btnAddUD.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlgAddUDWindow = new Win5Add_UD2(main);
				dlgAddUDWindow.setVisible(true);
			}
		});
	}

	public void display() {
		try {
			ResultSet rs = main.getSqlWorker().displayTable("UD");

			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.weighty = 0;

			bakScrollPosition = scrollPnlUDList.getVerticalScrollBar().getValue();

			remove(scrollPnlUDList);

			pnlUDList = new JPanel(new GridBagLayout());

			pnlUDList.setBorder(new LineBorder(Color.black));
			pnlUDList.setBackground(Color.white);
			pnlUDList.setBorder(null);

			while (rs.next()) {
				Pnl3Cell_UD2 cell = new Pnl3Cell_UD2(main, rs, Pnl4List_UD2.this);
				pnlUDList.add(cell, gbc);
			}

			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			pnlUDList.add(new JLabel(), gbc);

			scrollPnlUDList = new JScrollPane(pnlUDList);
			scrollPnlUDList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			scrollPnlUDList.setBorder(null);
			scrollPnlUDList.getVerticalScrollBar().setUI(new CustomScrollBar());
			scrollPnlUDList.getVerticalScrollBar().setPreferredSize(new Dimension(10, 10));

			JScrollBar scrollMovement = scrollPnlUDList.getVerticalScrollBar();
			scrollMovement.setUnitIncrement(main.getScrollMovement());

			add(scrollPnlUDList, BorderLayout.CENTER);

			scrollPnlUDList.revalidate();
			scrollPnlUDList.repaint();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollPnlUDList.getVerticalScrollBar().setValue(bakScrollPosition);

					repaint();
					revalidate();
				}
			});

		} catch (Exception e) {
			popupMsg(main.getDlgSettingWindow(), "목록 표시에 실패했습니다.\n실행환경 확인 후, 프로그램을 다시 실행시켜주세요.\n" + e.getMessage());
		}
	}

	public Pnl4List_UD2(MainWindow m) {
		this.main = m;

		setBackground(Color.white);

		setLayout(new BorderLayout());

		btnAddUD = main.createImgButton(30, iconUDAddList);
		pnlTop = new Bar2Top2(main, iconSetting, btnAddUD);
		add(pnlTop, BorderLayout.NORTH);

		add(scrollPnlUDList, BorderLayout.CENTER);

		setAddUDButton();
	}
}
