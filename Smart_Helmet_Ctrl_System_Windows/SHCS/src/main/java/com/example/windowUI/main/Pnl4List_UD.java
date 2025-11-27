package com.example.windowUI.main;

import static com.example.MainWindow.*;

import com.example.CustomScrollBar;
import com.example.CustomTextField;
import com.example.MainWindow;
import com.example.windowUI.setting.Pnl3Cell_UD2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Pnl4List_UD extends JPanel {
	private MainWindow main;

	private ImageIcon iconSearchUD = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_Search.png"));
	private ImageIcon iconSearchBox = new ImageIcon(MainWindow.class.getResource("/icon/main/icon_search_box.png"));

	private String strPnlName = "사용자 기기 상태 목록";
	private int bakScrollPosition;

	private GridBagConstraints gbc = new GridBagConstraints();

	private JPanel pnlTop;
	private JButton btnSearchUD;
	private JPanel pnlSearch;
	private JLabel lblSearchIcon;
	private JTextField inputSearchUD;

	private JPanel pnlUDStatusList = new JPanel();
	private JScrollPane scrollPnlUDStatusList = new JScrollPane(pnlUDStatusList);

	private JPanel pnlSelected = null;

	private String selectedUD_ID = null;

	public void setPnlSelected(JPanel p) {
		pnlSelected = p;
	}

	public void setSelectedUD_ID(String ud_id) {
		selectedUD_ID = ud_id;
	}

	public JTextField getInputSearchUD() {
		return inputSearchUD;
	}

	public JScrollPane getScrollPnlUDList() {
		return scrollPnlUDStatusList;
	}

	public JPanel getPnlSelected() {
		return pnlSelected;
	}

	public String getSelectedUD_ID() {
		return selectedUD_ID;
	}

	public void setSearchUDButton() {
		btnSearchUD.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!pnlSearch.isVisible()) {
					pnlSearch.setVisible(true);
					inputSearchUD.requestFocus();

				} else {
					inputSearchUD.setText("");
					pnlSearch.setVisible(false);
				}
				repaint();
				revalidate();
			}
		});
	}

	public void setInputSearchUD() {
		inputSearchUD.getDocument().addDocumentListener(new DocumentListener() {
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
				displayConnStatus();
			}
		});
	}

	public synchronized void displayConnStatus() {
		try {
			ResultSet rs;
			if (inputSearchUD.getText().equals("")) {
				rs = main.getSqlWorker().displayStatusTable();
			} else {
				rs = main.getSqlWorker().searchStatusTable("ud_name", inputSearchUD.getText());
			}

			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.weightx = 1.0;
			gbc.weighty = 0;

			bakScrollPosition = scrollPnlUDStatusList.getVerticalScrollBar().getValue();

			remove(scrollPnlUDStatusList);

			pnlUDStatusList = new JPanel(new GridBagLayout());

			pnlUDStatusList.setBorder(new LineBorder(Color.black));
			pnlUDStatusList.setBackground(Color.white);
			pnlUDStatusList.setBorder(null);

			while (rs.next()) {
				Pnl3Cell_UD cell = new Pnl3Cell_UD(main, rs);
				pnlUDStatusList.add(cell, gbc);
			}

			gbc.weighty = 1.0;
			pnlUDStatusList.add(new JLabel(), gbc);

			scrollPnlUDStatusList = new JScrollPane(pnlUDStatusList);
			scrollPnlUDStatusList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPnlUDStatusList.setBorder(null);
			scrollPnlUDStatusList.getVerticalScrollBar().setUI(new CustomScrollBar());
			scrollPnlUDStatusList.getVerticalScrollBar().setPreferredSize(new Dimension(10, 10));

			JScrollBar scrollMovement = scrollPnlUDStatusList.getVerticalScrollBar();
			scrollMovement.setUnitIncrement(main.getScrollMovement());

			add(scrollPnlUDStatusList, BorderLayout.CENTER);

			scrollPnlUDStatusList.revalidate();
			scrollPnlUDStatusList.repaint();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					scrollPnlUDStatusList.getVerticalScrollBar().setValue(bakScrollPosition);

					repaint();
					revalidate();
				}
			});

		} catch (Exception e) {
			main.getScheduledWorker().shutdown();
			popupMsg(main.getDlgSettingWindow(), "목록 표시에 실패했습니다.\n실행환경 확인 후, 프로그램을 다시 실행시켜주세요.\n" + e.getMessage());
		}
	}

	public Pnl4List_UD(MainWindow m) {
		this.main = m;

		Font fontSet = main.loadExternalFonts("/font/NanumSquareRoundEB.ttf");
		fontSet = fontSet.deriveFont(20f);

		setBackground(Color.white);

		setLayout(new BorderLayout());

		btnSearchUD = main.createImgButton(35, iconSearchUD);
		pnlSearch = new JPanel(new BorderLayout());
		pnlSearch.setBackground(Color.white);
		lblSearchIcon = new JLabel(iconSearchBox);
		lblSearchIcon.setPreferredSize(new Dimension(40, 30));
		pnlSearch.add(lblSearchIcon, BorderLayout.WEST);
		inputSearchUD = new JTextField();
		Border compoundBorder = new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.black),
				new EmptyBorder(0, 10, 0, 0));
		inputSearchUD.setBorder(compoundBorder);
		inputSearchUD.setFont(fontSet);
		inputSearchUD.setDocument(new CustomTextField(20));
		pnlSearch.add(inputSearchUD, BorderLayout.CENTER);
		pnlTop = new Bar2Top(main, strPnlName, btnSearchUD, pnlSearch);
		add(pnlTop, BorderLayout.NORTH);

		add(scrollPnlUDStatusList, BorderLayout.CENTER);

		setSearchUDButton();
		setInputSearchUD();
	}
}
