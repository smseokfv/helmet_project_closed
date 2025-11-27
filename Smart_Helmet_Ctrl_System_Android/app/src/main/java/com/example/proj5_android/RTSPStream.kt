package com.example.proj5_android

import android.app.Activity
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import com.pedro.common.ConnectChecker
import com.pedro.rtspserver.RtspServerCamera2

class RTSPStream {
    private fun popupMsg(msg: String) {
        MainActivity.getInstance().popupMsg(msg)
    }

    private var blnIsStream: Boolean = false
    fun getIsStream(): Boolean {
        return blnIsStream
    }

    private lateinit var arrCameraIdList: Array<String>
    private var intCurrentCameraIdIndex: Int = 0

    private fun getActiveCameraIdList(): Array<String> {
        val cameraManager = MainActivity.getInstance().getSystemService(Activity.CAMERA_SERVICE) as CameraManager
        return try {
            val camIdList = cameraManager.cameraIdList
            if (camIdList.isEmpty()) {
                camIdList
            } else {
                camIdList
            }
        } catch (e: Exception) {
            arrayOf()
        }
    }

    private fun checkCameraType(cameraIdList: Array<String>, cameraIdIndex: Int): String {
        val cameraManager = MainActivity.getInstance().getSystemService(Activity.CAMERA_SERVICE) as CameraManager
        val characteristics = cameraManager.getCameraCharacteristics(cameraIdList[cameraIdIndex])
        val facing = characteristics.get(CameraCharacteristics.LENS_FACING)

        val cameraDirection = when (facing) {
            CameraCharacteristics.LENS_FACING_FRONT -> "Front"
            CameraCharacteristics.LENS_FACING_BACK -> "Back"
            CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
            else -> "Unknown"
        }
        return "Index: ${cameraIdList.indexOf(cameraIdList[cameraIdIndex])} , Direction: $cameraDirection"
    }

    private lateinit var objConnectChecker: ConnectChecker
    private lateinit var objRTSPServer: RtspServerCamera2

    fun createRTSPServer() {
        try {
            intCurrentCameraIdIndex = 0
            blnIsStream = false
            val viewCamera = MainActivity.getInstance().getViewMyCam()
            val rtspPort = MainActivity.getInstance().getRTSPPort()

            arrCameraIdList = getActiveCameraIdList()
            if (arrCameraIdList.isNotEmpty()) {
                checkCameraType(arrCameraIdList, intCurrentCameraIdIndex)
            } else {
                throw Exception("카메라 장치 없음")
            }

            objConnectChecker = object : ConnectChecker {
                override fun onAuthError() {

                }

                override fun onAuthSuccess() {

                }

                override fun onConnectionFailed(reason: String) {

                }

                override fun onConnectionStarted(url: String) {

                }

                override fun onConnectionSuccess() {

                }

                override fun onDisconnect() {

                }

                override fun onNewBitrate(bitrate: Long) {

                }
            }
            objRTSPServer = RtspServerCamera2(viewCamera, objConnectChecker, rtspPort)
            objRTSPServer.switchCamera(arrCameraIdList[intCurrentCameraIdIndex])
            objRTSPServer.glInterface.setStreamRotation(0)

        } catch (e: Exception) {

        }
    }

    fun startStream() {
        try {
            if (objRTSPServer.prepareVideo(480, 640, 30, 1200 * 1024, 90) && objRTSPServer.prepareAudio(64 * 1024, 32000, true, true, true)) {
                objRTSPServer.startStream()
                blnIsStream = true

            } else {
                blnIsStream = false

            }
        } catch (e: Exception) {
            blnIsStream = false

        }
    }

    fun stopStream() {
        try {
            if (objRTSPServer.isStreaming) {
                objRTSPServer.stopStream()
                objRTSPServer.stopPreview()
            }
        } catch (e: Exception) {

        } finally {
            blnIsStream = false
        }
    }

    fun switchCamera() {
        val numCameraIdList = arrCameraIdList.size - 1

        if (intCurrentCameraIdIndex < numCameraIdList) {
            intCurrentCameraIdIndex += 1
        } else {
            intCurrentCameraIdIndex = 0
        }

        objRTSPServer.switchCamera(arrCameraIdList[intCurrentCameraIdIndex])

        val msg = "카메라를 전환합니다. (Index : ${intCurrentCameraIdIndex})"
        popupMsg(msg)
    }
}