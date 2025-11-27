package com.example.proj5_android

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity

class SecondActivity : ComponentActivity() {

    private lateinit var objValBackup: SharedPreferences

    private lateinit var loMainArea: LinearLayout
    private lateinit var inputIPAddr: TextView
    private lateinit var inputPort: TextView
    private lateinit var inputDeviceID: TextView
    private lateinit var inputUserId: TextView
    private lateinit var inputUserPassword: TextView
    private lateinit var confirmBtn: Button

    private fun setBackupValue() {
        val editor: SharedPreferences.Editor = objValBackup.edit()
        editor.putString("IPAddr", inputIPAddr.text.toString())
        editor.putString("Port", inputPort.text.toString())
        editor.putString("DeviceID", inputDeviceID.text.toString())
        editor.putString("UserId", inputUserId.text.toString())
        editor.putString("UserPassword", inputUserPassword.text.toString())
        editor.apply()
    }

    private fun loadBackupValue() {
        objValBackup = getSharedPreferences("InputValueBackup", MODE_PRIVATE)
        inputIPAddr.text = objValBackup.getString("IPAddr", "")
        inputPort.text = objValBackup.getString("Port", "") 
        inputPort.text = MainActivity.getInstance().getNetPort().toString()
        inputDeviceID.text = objValBackup.getString("DeviceID", "")
        inputUserId.text = objValBackup.getString("UserId", "")
        inputUserPassword.text = objValBackup.getString("UserPassword", "")
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        loMainArea = findViewById(R.id.mainArea)
        inputIPAddr = findViewById(R.id.inputIPAddr)
        inputPort = findViewById(R.id.inputPort)
        inputDeviceID = findViewById(R.id.inputDeviceID)
        inputUserId = findViewById(R.id.inputUserId)
        inputUserPassword = findViewById(R.id.inputUserPassword)
        confirmBtn = findViewById(R.id.confirmBtn)

        loadBackupValue()

        loMainArea.setOnClickListener {
            hideKeyboard()
        }

        confirmBtn.setOnClickListener {
            setBackupValue()
            finish()
        }
    }
}