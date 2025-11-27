package com.example.proj5_android

import android.net.Uri
import android.util.Log
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

class RTSPPlayer {
    private fun popupMsg(msg: String) {
        MainActivity.getInstance().popupMsg(msg)
    }

    private var blnIsPlay: Boolean = false
    fun getIsPlay(): Boolean {
        return blnIsPlay
    }

    private var objLibVLC: LibVLC? = null
    private var objMediaPlayer: MediaPlayer? = null

    private var arrRtspRcvOptions: ArrayList<String> = arrayListOf(
        "--rtsp-timeout=0"
    )

    private fun setPlayer() {
        try {
            MainActivity.getInstance().runOnUiThread {
                objLibVLC = LibVLC(MainActivity.getInstance(), arrRtspRcvOptions)
                objMediaPlayer = MediaPlayer(objLibVLC)

                objMediaPlayer!!.attachViews(MainActivity.getInstance().getViewSvrCam(), null, false, false)
            }
        } catch (e: Exception) {
        }

    }

    private fun removePlayer() {
        try {
            if (objMediaPlayer != null) {
                objMediaPlayer!!.release()
            }

            if (objLibVLC != null) {
                objLibVLC!!.release()
            }
        } catch (e: Exception) {
        }
    }

    fun playPlayer(rtspUrl: String) {
        try {
            setPlayer()
            Thread.sleep(1000)

            val media = Media(objLibVLC, Uri.parse(rtspUrl))
            objMediaPlayer!!.media = media
            objMediaPlayer!!.setVolume(200)
            objMediaPlayer!!.play()

            blnIsPlay = true
        } catch (e: Exception) {
        }
    }

    fun stopPlayer() {
        try {
            if (objMediaPlayer!!.isPlaying) {
                objMediaPlayer!!.stop()

                blnIsPlay = false

                Thread.sleep(1000)
                removePlayer()
            }
        } catch (e: Exception) {
        }
    }
}