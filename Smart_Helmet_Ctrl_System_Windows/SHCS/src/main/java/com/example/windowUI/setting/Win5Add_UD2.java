package com.example.windowUI.setting;

import static com.example.MainWindow.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.example.CustomTextField;
import com.example.MainWindow;
import com.example.SettingWindow;
import com.example.database.SQLWorker;

public class Win5Add_UD2 extends JDialog {
	private MainWindow main;

	private String strTitle = "헬멧 등록";
	private JLabel lblTitle;
	private String strPreviewUDName = "헬멧 이름 입력(1~10자리)";
	private JTextField inputUDName;
	private String strInputUDNameDes = "(헬멧 ID는 자동으로 추가됩니다.)";
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
						String[] arrInput = { inputUDName.getText(), "Y" };
						String latestID = sql.insertTable("UD", arrInput);

						String msg = "사용자 \'" + inputUDName.getText() + "\'의\n기기 ID는 \'" + latestID + "\' 입니다.";
						popupMsg(Win5Add_UD2.this, msg);

						udList.display();

						dispose();

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								JScrollBar b = udList.getScrollPnlUDList().getVerticalScrollBar();
								b.setValue(b.getMaximum());
							}
						});

					} catch (Exception e2) {
						popupMsg(main.getDlgSettingWindow(), "헬멧 등록에 실패했습니다.\n" + e2.getMessage());
					}

				} else {
					inputUDName.setText(strPreviewUDName);
					popupMsg(Win5Add_UD2.this, "사용자 기기 이름을 조건에 맞게 입력해주세요.");
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

	public Win5Add_UD2(MainWindow m) {
		super(m.getDlgSettingWindow(), true);

		this.main = m;

		Font fontSetR = main.loadExternalFonts("/font/NanumSquareRoundR.ttf");
		fontSetR = fontSetR.deriveFont(20f);
		Font fontSetL = main.loadExternalFonts("/font/NanumSquareRoundL.ttf");
		fontSetL = fontSetL.deriveFont(18f);

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
		inputUDName.setText(strPreviewUDName);
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
