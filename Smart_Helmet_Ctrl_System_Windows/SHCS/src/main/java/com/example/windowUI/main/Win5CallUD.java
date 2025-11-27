package com.example.windowUI.main;

import static com.example.MainWindow.*;
import com.example.MainWindow;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

public class Win5CallUD extends JFrame {
	private MainWindow main;

	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private MediaPlayer mediaPlayer;

	private Boolean canClose = false;
	
	private final String udName; // 생성자에서 받은 이름 저장
	
    public String getUdName() { return udName; }

	
	public void playClientStreaming(String rtspURL) {
		try {
			if (!mediaPlayer.status().isPlaying()) {
				mediaPlayer.submit(new Runnable() {
					@Override
					public void run() {
						mediaPlayer.media().play(rtspURL);
					}
				});
			}
		} catch (Exception e) {
		} 
	}

	public Win5CallUD(MainWindow m, String udName, String udID) {
		this.main = m;
		this.udName= udName;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle(udName);
		setSize(500, 500);
		setLocationRelativeTo(null);

		try {
			setIconImage(ImageIO.read(MainWindow.class.getResource("/icon/call/icon_view.png")));

		} catch (IOException e) {
			setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE));
		}

		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaPlayer = mediaPlayerComponent.mediaPlayer();

		add(mediaPlayerComponent, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				main.getSockSvrWorker().stopStreaming(udID);
				main.getMapCallWindow().remove(udID);
			}
		});

		Timer timer = new Timer(5000, e -> canClose = true);
		timer.setRepeats(false);
		timer.start();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (canClose) {
					setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dispose();
				} else {
					popupMsg(Win5CallUD.this, "호출이 완료될 때까지 기다려주세요.");
				}
			}
		});
	}
}
