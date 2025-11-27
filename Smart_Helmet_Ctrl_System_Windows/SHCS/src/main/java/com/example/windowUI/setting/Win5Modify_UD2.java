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

public class Win5Modify_UD2 extends JDialog {
	private MainWindow main;
	private String strID;
	private String strName;

	private String strTitle = "헬멧 변경";
	private JLabel lblTitle;
	private String strPreviewUDName = "헬멧 이름 입력(1~10자리)";
	private JTextField inputUDName;
	private String strInputUDNameDes = "(헬멧 ID는 자동으로 적용됩니다.)";
	private JLabel lblInputUDNameDes;
	private JButton btnConfirm;
	private JButton btnCancel;

	public void setInputEvent() {
		inputUDName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (inputUDName.getText().equals(strPreviewUDName)) {
					inputUDName.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (inputUDName.getText().isEmpty()) {
					inputUDName.setText(strPreviewUDName);
				}
			}
		});

		inputUDName.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent ke) {
				JTextField src = (JTextField) ke.getSource();
				if (src.getText().length() >= main.getLmtUDNameLength()) {
					ke.consume();
				}
			}
		});
	}

	public void setButtonEvent() {
		btnConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!inputUDName.getText().trim().equals("") && !inputUDName.getText().equals(strPreviewUDName)) {
					SQLWorker sql = main.getSqlWorker();
					Pnl4List_UD2 udList = (Pnl4List_UD2) ((SettingWindow) main.getDlgSettingWindow()).getPnlUD();

					try {
						String[] arrInput = { inputUDName.getText(), strID };
						sql.modifyTable("UD", arrInput);

						String msg = "사용자 기기(ID:" + strID + ")의 이름이\n\'" + inputUDName.getText() + "\' 으로 변경되었습니다.";
						popupMsg(Win5Modify_UD2.this, msg);

						udList.display();

						dispose();

					} catch (Exception e2) {
						popupMsg(main.getDlgSettingWindow(), "헬멧 변경에 실패했습니다.\n" + e2.getMessage());
					}

				} else {
					inputUDName.setText(strPreviewUDName);
					popupMsg(Win5Modify_UD2.this, "사용자 기기 이름을 조건에 맞게 입력해주세요.");
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

	public Win5Modify_UD2(MainWindow m, String presetID, String presetName) {
		super(m.getDlgSettingWindow(), true);

		this.main = m;

		Font fontSetR = main.loadExternalFonts("/font/NanumSquareRoundR.ttf");
		fontSetR = fontSetR.deriveFont(20f);
		Font fontSetL = main.loadExternalFonts("/font/NanumSquareRoundL.ttf");
		fontSetL = fontSetL.deriveFont(18f);

		this.strID = presetID;
		this.strName = presetName;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setSize(300, 200);
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

		JPanel pnlInput = new JPanel();
		pnlInput.setBorder(new EmptyBorder(0, 5, 0, 5));
		pnlInput.setBackground(Color.white);
		inputUDName = new JTextField(15);
		inputUDName.setBorder(new MatteBorder(0, 0, 1, 0, Color.black));
		inputUDName.setFont(fontSetL);
		inputUDName.setText(strName);
		pnlInput.add(inputUDName);
		add(pnlInput);

		lblInputUDNameDes = new JLabel(strInputUDNameDes);
		lblInputUDNameDes.setFont(fontSetL);
		lblInputUDNameDes.setBorder(new EmptyBorder(0, 20, 0, 0));
		add(lblInputUDNameDes);

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
