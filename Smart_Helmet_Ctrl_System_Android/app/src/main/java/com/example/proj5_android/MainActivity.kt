package com.example.proj5_android

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.NoiseSuppressor
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.pedro.library.view.OpenGlView
import org.videolan.libvlc.util.VLCVideoLayout


class MainActivity : ComponentActivity() {
    private var objPopupMsg: Toast? = null

    fun popupMsg(msg: String) {
        runOnUiThread {
            objPopupMsg?.cancel()
            objPopupMsg = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
            objPopupMsg?.show()
        }
    }

    private lateinit var objValBackup: SharedPreferences

    companion object {
        private var instance: MainActivity? = null

        fun getInstance(): MainActivity {
            if (instance == null) {
                instance = MainActivity()
            }
            return instance!!
        }
    }

    private val objPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
        Manifest.permission.SYSTEM_ALERT_WINDOW
    )

    private var blnEnterOptPage: Boolean = false

    private val NET_PORT = 5555
    fun getNetPort(): Int {
        return NET_PORT
    }

    private val RTSP_PORT = 55555
    fun getRTSPPort(): Int {
        return RTSP_PORT
    }

    private val CONNECT_INTERVAL = 5000
    fun getConnectInterval(): Int {
        return CONNECT_INTERVAL
    }

    @SuppressLint("SetTextI18n")
    private fun loadBackupValue() {
        objValBackup = getSharedPreferences("InputValueBackup", MODE_PRIVATE)
        viewOptRes.text = "서버 IP주소 : ${objValBackup.getString("IPAddr", "")}\n" +
//                "서버 Port번호 : ${objValBackup.getString("Port", "")}\n" +
                "등록 ID : ${objValBackup.getString("DeviceID", "")}"
        viewUserId.text = "사용자 아이디 : ${objValBackup.getString("UserId", "")}"
        viewUserPassword.text = "사용자 비밀번호 : ${objValBackup.getString("UserPassword", "")}"
    }

    fun getSSID(): String {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        return if (info.supplicantState == SupplicantState.COMPLETED) {
            info.ssid.removePrefix("\"").removeSuffix("\"")
        } else "(Wi-FI 정보를 찾을 수 없음)"
    }

    private var clsObjSocketComm: SocketComm = SocketComm()

    private var clsObjRTSPStream: RTSPStream = RTSPStream()
    fun getClsObjRTSPStream(): RTSPStream {
        return clsObjRTSPStream
    }

    private var clsObjRTSPPlayer: RTSPPlayer = RTSPPlayer()
    fun getClsObjRTSPPlayer(): RTSPPlayer {
        return clsObjRTSPPlayer
    }

    private lateinit var btnOptSet: Button
    private lateinit var viewOptRes: TextView
    private lateinit var viewSSID: TextView
    private lateinit var viewUserId: TextView
    private lateinit var viewUserPassword: TextView

    private lateinit var viewSvrCam: VLCVideoLayout
    fun getViewSvrCam(): VLCVideoLayout {
        return viewSvrCam
    }

    private lateinit var viewMyCam: OpenGlView
    fun getViewMyCam(): OpenGlView {
        return viewMyCam
    }

    private lateinit var btnStream: Button

    @SuppressLint("SetTextI18n")
    private fun setComponents() {
        btnOptSet = findViewById(R.id.btnOptSet)
        viewOptRes = findViewById(R.id.viewOptRes)
        viewSvrCam = findViewById(R.id.serverCamera)
        viewMyCam = findViewById(R.id.myCamera)
        viewSSID = findViewById(R.id.viewSSID)
        viewUserId = findViewById(R.id.viewUserId)
        viewUserPassword = findViewById(R.id.viewUserPassword)

        clsObjRTSPStream.createRTSPServer()

        loadBackupValue()
        viewSSID.text = "SSID : ${getSSID()}"

        setButtons()
    }

    @SuppressLint("SetTextI18n")
    fun updateComponents() {
        viewSSID.text = "SSID : ${getSSID()}"
    }

    private fun setButtons() {
        btnOptSet.setOnClickListener {
            blnEnterOptPage = true
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            val intent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, pendingIntent)
            System.exit(2)
        }

        setContentView(R.layout.activity_main)

        instance = this

        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                setComponents()

                val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION)
                audioManager.setSpeakerphoneOn(true)

                clsObjSocketComm.activateCoroutine()

            }

            override fun onPermissionDenied(p0: MutableList<String>?) {

            }
        }

        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("권한을 허용해주세요. ([설정] > [앱 및 알림] > [고급] > [앱 권한])")
            .setPermissions(*objPermissions)
            .check()
    }

    override fun onResume() {
        super.onResume()

        if (blnEnterOptPage) {
            loadBackupValue()
            blnEnterOptPage = false

            clsObjSocketComm.restartCoroutine()
        }
    }
}