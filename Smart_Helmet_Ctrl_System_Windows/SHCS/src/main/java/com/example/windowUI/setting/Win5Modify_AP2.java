package com.example.windowUI.setting;

import static com.example.MainWindow.*;

import com.example.CustomTextField;
import com.example.MainWindow;
import com.example.SettingWindow;
import com.example.database.SQLWorker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import java.sql.*;

public class Win5Modify_AP2 extends JDialog {
	private MainWindow main;
	private String strID;
	private String strName;
	private String strSSID;

	private String strTitle = "AP 변경";
	private JLabel lblTitle;
	private String strPreviewAPName = "장치 이름 입력(1~10자리)";
	private JTextField inputAPName;
	private String strPreviewAPSSID = "SSID 입력(영문/숫자)";
	private JTextField inputAPSSID;
	private JButton btnConfirm;
	private JButton btnCancel;

	public void setInputEvent() {
		inputAPName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (inputAPName.getText().equals(strPreviewAPName)) {
					inputAPName.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (inputAPName.getText().isEmpty()) {
					inputAPName.setText(strPreviewAPName);
				}
			}
		});

		inputAPName.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent ke) {
				JTextField src = (JTextField) ke.getSource();
				if (src.getText().length() >= main.getLmtAPNameLength()) {
					ke.consume();
				}
			}
		});

		inputAPSSID.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (inputAPSSID.getText().equals(strPreviewAPSSID)) {
					inputAPSSID.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (inputAPSSID.getText().isEmpty()) {
					inputAPSSID.setText(strPreviewAPSSID);
				}
			}
		});

		inputAPSSID.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent ke) {
				JTextField src = (JTextField) ke.getSource();
				if (src.getText().length() >= main.getLmtAPSSIDLength()) {
					ke.consume();
				}
			}
		});
	}

	public void setButtonEvent() {
		btnConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!inputAPName.getText().trim().equals("") && !inputAPName.getText().equals(strPreviewAPName)
						&& !inputAPSSID.getText().trim().equals("")
						&& !inputAPSSID.getText().equals(strPreviewAPSSID)) {

					SQLWorker sql = main.getSqlWorker();
					Pnl4List_AP2 apList = (Pnl4List_AP2) ((SettingWindow) main.getDlgSettingWindow()).getPnlAP();

					try {
						String[] arrInput = { inputAPName.getText(), inputAPSSID.getText(), strID };
						sql.modifyTable("AP", arrInput);

						String msg = "\'" + inputAPName.getText() + "(" + inputAPSSID.getText()
								+ ")\'\n으로 AP정보가 변경되었습니다.";
						popupMsg(Win5Modify_AP2.this, msg);

						apList.display();

						dispose();

					} catch (Exception e2) {
						popupMsg(main.getDlgSettingWindow(), "AP 변경에 실패했습니다.\n" + e2.getMessage());
					}

				} else {
					inputAPName.setText(strPreviewAPName);
					inputAPSSID.setText(strPreviewAPSSID);
					popupMsg(Win5Modify_AP2.this, "AP의 명칭 또는 SSID를 조건에 맞게 입력해주세요.");
				}
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	public Win5Modify_AP2(MainWindow m, String presetID, String presetName, String presetSSID) {
		super(m.getDlgSettingWindow(), true);

		this.main = m;

		Font fontSetR = main.loadExternalFonts("/font/NanumSquareRoundR.ttf");
		fontSetR = fontSetR.deriveFont(20f);
		Font fontSetL = main.loadExternalFonts("/font/NanumSquareRoundL.ttf");
		fontSetL = fontSetL.deriveFont(18f);

		this.strID = presetID;
		this.strName = presetName;
		this.strSSID = presetSSID;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setSize(300, 220);
		setResizable(false);

		getContentPane().setBackground(Color.white);

		setLocationRelativeTo(main.getDlgSettingWindow());

		setLayout(new GridLayout(5, 1));

		lblTitle = new JLabel(strTitle);
		lblTitle.setFocusable(true);
		lblTitle.requestFocusInWindow();
		lblTitle.setFont(fontSetR);
		lblTitle.setForeground(new Color(0, 0, 0));
		lblTitle.setBorder(new EmptyBorder(0, 20, 0, 0));
		add(lblTitle);

		inputAPName = new JTextField(15);
		inputAPName.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
		inputAPName.setFont(fontSetL);
		inputAPName.setText(strName);
		JPanel pnlInput = new JPanel();
		pnlInput.setBorder(new EmptyBorder(0, 5, 0, 5));
		pnlInput.setBackground(Color.white);
		pnlInput.add(inputAPName);
		add(pnlInput);

		inputAPSSID = new JTextField(15);
		inputAPSSID.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
		inputAPSSID.setFont(fontSetL);
		inputAPSSID.setText(strSSID);
		JPanel pnlInput2 = new JPanel();
		pnlInput2.setBorder(new EmptyBorder(0, 5, 0, 5));
		pnlInput2.setBackground(Color.white);
		pnlInput2.add(inputAPSSID);
		add(pnlInput2);

		add(new JLabel());

		JPanel pnlButton = new JPanel(new GridLayout(1, 2));
		btnConfirm = new JButton("확인");
		btnConfirm.setBackground(new Color(240, 240, 240));
		btnConfirm.setFont(fontSetR);
		btnConfirm.setFocusPainted(false);
		btnCancel = new JButton("취소");
		btnCancel.setBackground(new Color(240, 240, 240));
		btnCancel.setFont(fontSetR);
		btnCancel.setFocusPainted(false);
		pnlButton.add(btnConfirm);
		pnlButton.add(btnCancel);
		add(pnlButton);

		setInputEvent();
		setButtonEvent();
	}
}
