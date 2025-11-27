package com.example.proj5_android

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import java.io.*
import java.net.*
import kotlinx.coroutines.*
import java.util.Enumeration

class SocketComm {
    private fun popupMsg(msg: String) {
        MainActivity.getInstance().popupMsg(msg)
    }

    private lateinit var objValBackup: SharedPreferences
    private var ipAddress: String = ""
    private var port: Int = 0
    private var deviceID: Int = 0
    private var userId: String = ""
    private var userPassword: String = ""


    private fun loadValue() {
        objValBackup = MainActivity.getInstance().getSharedPreferences("InputValueBackup", Activity.MODE_PRIVATE)
        ipAddress = objValBackup.getString("IPAddr", "").toString()
        port = objValBackup.getString("Port", "")?.toIntOrNull() ?: 0
        deviceID = objValBackup.getString("DeviceID", "")?.toIntOrNull() ?: 0
        userId = objValBackup.getString("UserId", "").toString()
        userPassword = objValBackup.getString("UserPassword", "").toString()
    }

    private fun getIpAddress(): String {
        try {
            val interfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface: NetworkInterface = interfaces.nextElement()
                val addresses: Enumeration<InetAddress> = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val inetAddress: InetAddress = addresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is java.net.Inet4Address) {
                        return if (inetAddress.hostAddress == null) {
                            "null"
                        } else {
                            inetAddress.hostAddress!!
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "null"
    }

    private lateinit var objSocket: Socket

    private lateinit var objRcv: BufferedReader
    private lateinit var objSend: BufferedWriter

    private fun connectServer() {
        val timeout = MainActivity.getInstance().getConnectInterval()
        intervalTime = timeout

        loadValue()

        try {
            objSocket = Socket()

            objSocket.connect(InetSocketAddress(ipAddress, port), timeout)

            objRcv = BufferedReader(InputStreamReader(objSocket.getInputStream()))
            objSend = BufferedWriter(OutputStreamWriter(objSocket.getOutputStream()))

            popupMsg("서버 연결 성공")

            rcvServer()

        } catch (e: SocketTimeoutException) {
            disconnectServer()
            intervalTime = 0

        } catch (e: IOException) {
            disconnectServer()
            intervalTime = 0

        } catch (e: Exception) {
            disconnectServer()
            intervalTime = 0
        }
    }

    private fun disconnectServer() {
        try {
            if (::objSocket.isInitialized) {
                objSocket.close()
                objRcv.close()
                objSend.close()
                popupMsg("서버 연결 종료")

            } else {

            }

        } catch (e: Exception) {

        }
    }

    fun sendServer(msg: String) {
        Thread {
            try {
                objSend.write(msg + "\n")
                objSend.flush()

            } catch (e: IOException) {

            } catch (e: NullPointerException) {

            } catch (e: UninitializedPropertyAccessException) {

            } catch (e: Exception) {

            }

        }.start()
    }

    private fun rcvServer() {
        Thread {
            try {
                while (objSocket.isConnected) {
                    val msgFromSvr = objRcv.readLine()

                    if (msgFromSvr != null) {
                        when {
                            msgFromSvr.contains("Start Communication") -> {
                                MainActivity.getInstance().getClsObjRTSPStream().startStream()

                                val rtspURL = "rtsp://${getIpAddress()}:${MainActivity.getInstance().getRTSPPort()}/"
                                sendServer("OK Let`s Start , ${String.format("%03d", deviceID)} , $rtspURL")

                                val rtspURL2 = "rtsp://${ipAddress}:${msgFromSvr.split(" , ")[1]}/"
                                MainActivity.getInstance().getClsObjRTSPPlayer().playPlayer(rtspURL2)

                                popupMsg("스트리밍을 시작합니다.")
                            }

                            msgFromSvr.contains("Stop Communication") -> {
                                sendServer("OK Bye Bye.")

                                MainActivity.getInstance().getClsObjRTSPPlayer().stopPlayer()
                                MainActivity.getInstance().getClsObjRTSPStream().stopStream()

                                popupMsg("스트리밍을 종료합니다.")
                            }
                        }
                    }
                }

            } catch (e: IOException) {
                when (e.message) {
                    "Connection reset" -> {

                    }

                    else -> {

                    }
                }

            } catch (e: NullPointerException) {

            } catch (e: Exception) {

            } finally {
                disconnectServer()
                restartCoroutine()
            }
        }.start()
    }

    private lateinit var objInterval: Job
    private var intervalTime = 0

    fun activateCoroutine() {
        startConnecting()

        Runtime.getRuntime().addShutdownHook(Thread {
            stopConnecting()
        })

        Thread {
            while (true) {
                Thread.sleep(1000)
            }
        }.start()
    }

    fun restartCoroutine() {
        if (::objInterval.isInitialized && objInterval.isActive) {
            disconnectServer()
            objInterval.cancel()
        }
        activateCoroutine()
    }

    private fun startConnecting() {
        if (::objSocket.isInitialized && objInterval.isActive) return

        objInterval = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    if (!::objSocket.isInitialized || objSocket.isClosed) {
                        connectServer()
                    }

                    sendServer("Connection Success , ${String.format("%03d", deviceID)} , ${MainActivity.getInstance().getSSID()} , $userId , $userPassword")

                } catch (e: Exception) {
                    disconnectServer()
                }
                delay(intervalTime.toLong())
            }
        }
    }

    private fun stopConnecting() {
        objInterval.cancel()
        disconnectServer()
    }
}