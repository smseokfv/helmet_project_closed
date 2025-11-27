package com.example.socket;

import static com.example.MainWindow.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.MainWindow;

public class SocketSvrWorker extends Thread {
	private MainWindow main;

	private ServerSocket sockSvr;

	private String comName = "";

	public void setComName(String input) {
		comName = input;
	}

	public String getComName() {
		return comName;
	}

	private String ipAddress = "";

	public void setIpAddress(String input) {
		ipAddress = input;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	private CopyOnWriteArrayList<SocketClientController> arrSockCliCtrl = new CopyOnWriteArrayList<>();

	public synchronized void addClient(SocketClientController scc) {
		arrSockCliCtrl.add(scc);
		
	}

	public synchronized void removeClient(SocketClientController scc) {
		arrSockCliCtrl.remove(scc);
	}

	public void run() {
		try {
			sockSvr = new ServerSocket(main.getSocketPort());

			setComName(InetAddress.getLocalHost().getHostName());
			setIpAddress(InetAddress.getLocalHost().getHostAddress());

			while (!sockSvr.isClosed()) {
				Socket sockCli = sockSvr.accept();
				
				// ★ 원격(클라이언트) IP 가져오기
				String remoteIp = ((InetSocketAddress) sockCli.getRemoteSocketAddress())
				                    .getAddress().getHostAddress();

				SocketClientController trdSockCliCtrl = new SocketClientController(main, sockCli);

				addClient(trdSockCliCtrl);
			}

		} catch (Exception e) {
			popupMsg(main, "서버를 시작할 수 없습니다.\n" + e.getMessage());
			System.exit(1);
		}
	}

	public void startStreaming(String input) {
		try {
			for (SocketClientController scc : arrSockCliCtrl) {
				if (input.equals(scc.getUD_ID())) {
					scc.sendMsg("Start Communication , " + main.getRTSPPort());
					break;
				}
			}
		} catch (Exception e) {
		}
	}

	public void stopStreaming(String input) {
		try {
			for (SocketClientController scc : arrSockCliCtrl) {
				if (input.equals(scc.getUD_ID())) {
					scc.sendMsg("Stop Communication");
					break;
				}
			}
		} catch (Exception e) {
		}

	}

	public void stopClientStreamingAll() {
		try {
			for (SocketClientController scc : arrSockCliCtrl) {
				scc.sendMsg("Stop Communication");
			}
		} catch (Exception e) {
		}
	}

	public SocketSvrWorker(MainWindow m) {
		this.main = m;
		SocketSvrWorker.this.start();
	}
}
