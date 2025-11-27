package com.example.socket;

import static com.example.MainWindow.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

import com.example.MainWindow;
import com.example.database.SQLWorker;
import com.example.windowUI.main.Pnl4List_UD;
import com.example.windowUI.main.Win5CallUD;

public class SocketClientController extends Thread {
	private MainWindow main;

	private Socket sock;

	private BufferedReader objRcv;
	private BufferedWriter objSend;

	private String UD_ID = "";
	private String AP_SSID = "";
	private String AP_ID = "";
	
	//사용자 id,psw 전달 변수
	private String USER_ID = "";
	private String USER_PASSWORD = "";
	

	public String getUD_ID() {
		return UD_ID;
	}

	public String getClientInfo() {
		return sock.getRemoteSocketAddress().toString().split("/")[1];
	}

	public void releaseResources() {
		try {
			objRcv.close();
			objSend.close();
			sock.close();

		} catch (Exception e) {

		}
	}

	public void sendMsg(String msg) {
		try {
			objSend.write(msg + "\n");
			objSend.flush();

		} catch (Exception e) {
		}
	}

	public void run() {
		SQLWorker sql = main.getSqlWorker();

		try {
			String rcvMsg;
			while ((rcvMsg = objRcv.readLine()) != null) {
				// 소켓에서 받은 값 로그 출력
				System.out.println("========================================");
				System.out.println("[소켓 수신] 원본 메시지: " + rcvMsg);
				System.out.println("[소켓 수신] 클라이언트 정보: " + getClientInfo());
				System.out.println("[소켓 수신] 수신 시간: " + new java.util.Date());
				System.out.println("========================================");

				if (rcvMsg.contains("Connection Success")) {
					try {
						String[] parts = rcvMsg.split(" , ");
						if (parts.length >= 5) {
							UD_ID = parts[1];
							AP_SSID = parts[2];
							USER_ID = parts[3];
							USER_PASSWORD = parts[4];
							
							// 사용자 인증 정보를 리스트에 저장 (중복 체크)
							System.out.println("[리스트 저장 시도] USER_ID: \"" + USER_ID + "\", USER_PASSWORD: \"" + USER_PASSWORD + "\"");
							main.addUserLogin(UD_ID, USER_ID, USER_PASSWORD);
						} else {
							System.out.println("[Connection Success] 배열 길이 부족: " + parts.length);
						}
					} catch (Exception ex) {
						System.out.println("[Connection Success] 예외 발생: " + ex.getMessage());
						ex.printStackTrace();
					}

					int exist1 = sql.checkExistCnt("device_info", "ud_id", UD_ID);
					if (exist1 >= 1) {
						int exist2 = sql.checkExistCnt("ap_info", "ap_ssid", "'" + AP_SSID + "'");
						if (exist2 >= 1) {

							ResultSet rs = sql.getSpecificValue("ap_info", "ap_id", "ap_ssid", "'" + AP_SSID + "'");
							if (rs.next()) {
								AP_ID = rs.getString(1);
							}

							sql.updateDB(UD_ID, AP_ID, "Y");

						} else {
						}
					} else {
					}

				} else if (rcvMsg.contains("OK Let`s Start")) {
					String ud_id = rcvMsg.split(" , ")[1];
					String rtspURL = rcvMsg.split(" , ")[2];
					
					System.out.println("[OK Let's Start] ud_id: " + ud_id);
					System.out.println("[OK Let's Start] rtspURL: " + rtspURL);

					((Win5CallUD) main.getMapCallWindow().get(ud_id)).playClientStreaming(rtspURL);
				    Win5CallUD win = (Win5CallUD) main.getMapCallWindow().get(ud_id);
				    if (win != null) win.playClientStreaming(rtspURL);
				    String udName = win.getUdName();
				    new Thread(() -> {
				        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
				        main.getRtspWorker().startSaving(rtspURL,ud_id,udName); // ★ 여기서 시작
				    }).start();
				}
			}
		} catch (SocketTimeoutException e) {
			System.out.println("[SocketClientController] SocketTimeoutException: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("[SocketClientController] Exception: " + e.getMessage());
			e.printStackTrace();
		} finally {
			releaseResources();
			main.getSockSvrWorker().removeClient(this);

			try {
				sql.updateDB(UD_ID, AP_ID, "N");
//				 main.removeUdRtspUrl(UD_ID);
				((Win5CallUD) main.getMapCallWindow().get(UD_ID)).dispose();

			} catch (Exception e) {
			}
		}
	}

	public SocketClientController(MainWindow m, Socket s) {
		try {
			this.main = m;

			this.sock = s;
			sock.setSoTimeout(main.getSocketResponseTimeout());

			objRcv = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			objSend = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

			SocketClientController.this.start();

		} catch (Exception e) {
		}
	}
}
