package com.example.windowUI.main;

import static com.example.MainWindow.*;

import com.example.CustomScrollBar;
import com.example.CustomTextField;
import com.example.MainWindow;
import com.example.database.SQLWorker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.sql.*;

public class Pnl4List_AP extends JPanel {
	private MainWindow main;

	private ImageIcon iconSearchAP = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_Search.png"));
	private ImageIcon iconSearchBox = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_search_box.png"));

	private String strPnlName = "AP별 사용자 기기 목록";
	private int bakScrollPosition;

	private GridBagConstraints gbc = new GridBagConstraints();

	private JPanel pnlTop;
	private JButton btnSearchAP;
	private JPanel pnlSearch;
	private JLabel lblSearchIcon;
	private JTextField inputSearchAP;

	private JPanel pnlAPSortedList = new JPanel();
	private JScrollPane scrollPnlAPSortedList = new JScrollPane(pnlAPSortedList);

	public JTextField getInputSearchAP() {
		return inputSearchAP;
	}

	public void setSearchAPButton() {
		btnSearchAP.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!pnlSearch.isVisible()) {
					pnlSearch.setVisible(true);
					inputSearchAP.requestFocus();

				} else {
					inputSearchAP.setText("");
					pnlSearch.setVisible(false);
				}
				repaint();
				revalidate();
			}
		});
	}

	public void setInputSearchAP() {
		inputSearchAP.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				work();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				work();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				work();
			}

			public void work() {
				displayListSortedByAP();
			}
		});
	}

	public synchronized void displayListSortedByAP() {
		try {
			SQLWorker sql = main.getSqlWorker();

			ResultSet rs;
			if (inputSearchAP.getText().equals("")) {
				rs = sql.displayAPCnt();
			} else {
				rs = sql.searchAPCnt(inputSearchAP.getText());
			}

			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.weighty = 0;

			bakScrollPosition = scrollPnlAPSortedList.getVerticalScrollBar().getValue();

			remove(scrollPnlAPSortedList);

			pnlAPSortedList = new JPanel(new GridBagLayout());

			pnlAPSortedList.setBorder(new LineBorder(Color.black));
			pnlAPSortedList.setBackground(Color.white);
			pnlAPSortedList.setBorder(null);

			while (rs.next()) {
				Pnl3Cell_AP_Parent cell = new Pnl3Cell_AP_Parent(main, rs);
				cell.setBorder(new EmptyBorder(0, 0, 0, 0));
				pnlAPSortedList.add(cell, gbc);

				if (main.getMapAP_SSID().containsKey(cell.getStrAP_SSID())) {
					ResultSet rs2;
					if (inputSearchAP.getText().equals("")) {
						rs2 = sql.expandChildList(cell.getStrAP_SSID());
					} else {
						rs2 = sql.searchExpandChildList(cell.getStrAP_SSID(), inputSearchAP.getText());
					}

					while (rs2.next()) {
						Pnl3Cell_AP_Child cell_child = new Pnl3Cell_AP_Child(main, rs2);
						pnlAPSortedList.add(cell_child, gbc);
					}
				}
			}

			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			pnlAPSortedList.add(new JLabel(), gbc);

			scrollPnlAPSortedList = new JScrollPane(pnlAPSortedList);
			scrollPnlAPSortedList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPnlAPSortedList.setBorder(null);
			scrollPnlAPSortedList.getVerticalScrollBar().setUI(new CustomScrollBar());
			scrollPnlAPSortedList.getVerticalScrollBar().setPreferredSize(new Dimension(10, 10));

			JScrollBar scrollMovement = scrollPnlAPSortedList.getVerticalScrollBar();
			scrollMovement.setUnitIncrement(main.getScrollMovement());

			add(scrollPnlAPSortedList, BorderLayout.CENTER);

			scrollPnlAPSortedList.revalidate();
			scrollPnlAPSortedList.repaint();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollPnlAPSortedList.getVerticalScrollBar().setValue(bakScrollPosition);
					repaint();
					revalidate();
				}
			});

		} catch (Exception e) {
			main.getScheduledWorker().shutdown();
			popupMsg(main.getDlgSettingWindow(), "목록 표시에 실패했습니다.\n실행환경 확인 후, 프로그램을 다시 실행시켜주세요.\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	public Pnl4List_AP(MainWindow m) {
		this.main = m;

		Font fontSet = main.loadExternalFonts("/font/NanumSquareRoundEB.ttf");
		fontSet = fontSet.deriveFont(20f);

		setBackground(Color.white);

		setLayout(new BorderLayout());

		btnSearchAP = main.createImgButton(35, iconSearchAP);
		pnlSearch = new JPanel(new BorderLayout());
		pnlSearch.setBackground(Color.white);
		lblSearchIcon = new JLabel(iconSearchBox);
		lblSearchIcon.setPreferredSize(new Dimension(40, 30));
		pnlSearch.add(lblSearchIcon, BorderLayout.WEST);
		inputSearchAP = new JTextField();
		Border compoundBorder = new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.black),
				new EmptyBorder(0, 10, 0, 0));
		inputSearchAP.setBorder(compoundBorder);
		inputSearchAP.setFont(fontSet);
		inputSearchAP.setDocument(new CustomTextField(20));
		pnlSearch.add(inputSearchAP, BorderLayout.CENTER);
		pnlTop = new Bar2Top(main, strPnlName, btnSearchAP, pnlSearch);
		add(pnlTop, BorderLayout.NORTH);

		add(scrollPnlAPSortedList, BorderLayout.CENTER);

		setSearchAPButton();
		setInputSearchAP();
	}
}
