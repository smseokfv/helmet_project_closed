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

public class Pnl4List_AP2 extends JPanel {
	private MainWindow main;

	private ImageIcon iconSetting = new ImageIcon(MainWindow.class.getResource("/icon/setting/icon_setup.png"));
	private ImageIcon iconAPAddList = new ImageIcon(MainWindow.class.getResource("/icon/setting/icon_plus.png"));
	private ImageIcon iconAPCheckUse = new ImageIcon(MainWindow.class.getResource("/icon/setting/icon_use.png"));
	private ImageIcon iconAPCheckUnuse = new ImageIcon(MainWindow.class.getResource("/icon/setting/icon_unuse.png"));

	public ImageIcon getIconAPCheckUse() {
		return iconAPCheckUse;
	}

	public ImageIcon getIconAPCheckUnuse() {
		return iconAPCheckUnuse;
	}

	private int bakScrollPosition;

	private GridBagConstraints gbc = new GridBagConstraints();

	private JPanel pnlTop;
	private JButton btnAddAP;

	private JPanel pnlAPList = new JPanel();
	private JScrollPane scrollPnlAPList = new JScrollPane(pnlAPList);

	private JPanel pnlSelected = null;

	private Win5Add_AP2 dlgAddAPWindow;

	public void setPnlSelected(JPanel p) {
		pnlSelected = p;
	}

	public JScrollPane getScrollPnlAPList() {
		return scrollPnlAPList;
	}

	public JPanel getPnlSelected() {
		return pnlSelected;
	}

	public void setAddAPButton() {
		btnAddAP.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlgAddAPWindow = new Win5Add_AP2(main);
				dlgAddAPWindow.setVisible(true);
			}
		});
	}

	public void display() {
		try {
			ResultSet rs = main.getSqlWorker().displayTable("AP");

			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.weighty = 0;

			bakScrollPosition = scrollPnlAPList.getVerticalScrollBar().getValue();

			remove(scrollPnlAPList);

			pnlAPList = new JPanel(new GridBagLayout());

			pnlAPList.setBorder(new LineBorder(Color.black));
			pnlAPList.setBackground(Color.white);
			pnlAPList.setBorder(null);

			while (rs.next()) {
				Pnl3Cell_AP2 cell = new Pnl3Cell_AP2(main, rs, Pnl4List_AP2.this);
				pnlAPList.add(cell, gbc);
			}

			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			pnlAPList.add(new JLabel(), gbc);

			scrollPnlAPList = new JScrollPane(pnlAPList);
			scrollPnlAPList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			scrollPnlAPList.setBorder(null);
			scrollPnlAPList.getVerticalScrollBar().setUI(new CustomScrollBar());
			scrollPnlAPList.getVerticalScrollBar().setPreferredSize(new Dimension(10, 10));

			JScrollBar scrollMovement = scrollPnlAPList.getVerticalScrollBar();
			scrollMovement.setUnitIncrement(main.getScrollMovement());

			add(scrollPnlAPList, BorderLayout.CENTER);

			scrollPnlAPList.revalidate();
			scrollPnlAPList.repaint();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollPnlAPList.getVerticalScrollBar().setValue(bakScrollPosition);

					repaint();
					revalidate();
				}
			});

		} catch (Exception e) {
			popupMsg(main.getDlgSettingWindow(), "목록 표시에 실패했습니다.\n실행환경 확인 후, 프로그램을 다시 실행시켜주세요.\n" + e.getMessage());
		}
	}

	public Pnl4List_AP2(MainWindow m) {
		this.main = m;

		setBackground(Color.white);

		setLayout(new BorderLayout());

		btnAddAP = main.createImgButton(30, iconAPAddList);
		pnlTop = new Bar2Top2(main, iconSetting, btnAddAP);
		add(pnlTop, BorderLayout.NORTH);

		add(scrollPnlAPList, BorderLayout.CENTER);

		setAddAPButton();
	}
}
