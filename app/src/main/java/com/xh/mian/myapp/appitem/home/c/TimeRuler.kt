package com.xh.mian.myapp.appitem.home.c

import android.Manifest
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xh.mian.myapp.R
import com.xh.mian.myapp.databinding.TimeRulerViewBinding
import com.xh.mian.myapp.tools.db.SharedPreferences
import com.xh.mian.myapp.tools.other.AudioRecorder
import com.xh.mian.myapp.tools.uitl.PermissionsActivity
import com.xh.mian.myapp.tools.uitl.PermissionsChecker
import java.text.SimpleDateFormat
import java.util.*


class TimeRuler : AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: TimeRulerViewBinding
    private var mediaPlayer:MediaPlayer? = null
    var audioRecorder: AudioRecorder? = null
    private val REQUEST_CODE = 0 // 请求码
    private var mPermissionsChecker // 权限检测器
            : PermissionsChecker? = null
    // 所需的全部权限
    val PERMISSIONS = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = TimeRulerViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root);
        audioRecorder = AudioRecorder.getInstance();
        mBinding.start.setOnClickListener(this@TimeRuler);
        mBinding.pause.setOnClickListener(this@TimeRuler);
        mBinding.pcmList.setOnClickListener(this@TimeRuler);
        mBinding.wavList.setOnClickListener(this@TimeRuler);
        //media()
        mPermissionsChecker = PermissionsChecker(this@TimeRuler);
        loadPermissions()
    }

    private fun loadPermissions() {
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker!!.lacksPermissions(*PERMISSIONS)) {
            PermissionsActivity.startActivityForResult(this@TimeRuler, REQUEST_CODE, *PERMISSIONS)
        }
    }
    private fun Recorder(){
        val fileName: String = SimpleDateFormat("yyyyMMddhhmmss").format(Date())
        audioRecorder!!.createDefaultAudio(fileName)
        audioRecorder!!.startRecord(object : AudioRecorder.RecordStreamListener {
            override fun recordOfByte(data: ByteArray?, begin: Int, end: Int) {
            }

            override fun getVolume(v: Double) {
                if (v < 10) {
                    mBinding.trLine.setWave(0)
                }
                if (v >= 10 && v < 15) {
                    mBinding.trLine.setWave(1)
                }
                if (v >= 15 && v < 20) {
                    mBinding.trLine.setWave(2)
                }
                if (v >= 20 && v < 30) {
                    mBinding.trLine.setWave(3)
                }
                if (v >= 30) {
                    mBinding.trLine.setWave(4)
                }
            }
        })
    }
    private fun media(){
        var uri="https://img.tukuppt.com/newpreview_music/08/99/49/5c897788e421b53181.mp3"
        val uri1: Uri = Uri.parse(uri)
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(this, uri1)
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnPreparedListener(OnPreparedListener { mp ->
                //Log.e("MediaPlayer ", "开始播放")
                mp.start()
            })
            mediaPlayer!!.setOnCompletionListener { // 在播放完毕被回调
                mediaPlayer?.start()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onPause() {
        super.onPause()
        if (audioRecorder!!.status == AudioRecorder.Status.STATUS_START) {
            audioRecorder!!.pauseRecord()
            mBinding.trLine.setPause()
            //pause.setText("继续录音")
        }
    }
    override fun onDestroy() {
        audioRecorder!!.release()
        mBinding.trLine.setDestroy()
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
        super.onDestroy()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish()
        }
    }
    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.start -> try {
                Recorder()
                mBinding.trLine.setStart()
            } catch (e: IllegalStateException) {
                Toast.makeText(this@TimeRuler, e.message, Toast.LENGTH_SHORT).show()
            }
            R.id.pause -> try {
                audioRecorder!!.pauseRecord()
                mBinding.trLine.setPause()
                var ls = mBinding.trLine.wavs
                audioRecorder!!.release()
                SharedPreferences.save("wave",ls.toString());
            } catch (e: IllegalStateException) {
                Toast.makeText(this@TimeRuler, e.message, Toast.LENGTH_SHORT).show()
            }
            R.id.pcmList -> {
                val showPcmList = Intent(this@TimeRuler, ListActivity::class.java)
                showPcmList.putExtra("type", "pcm")
                startActivity(showPcmList)
            }
            R.id.wavList -> {
                val showWavList = Intent(this@TimeRuler, ListActivity::class.java)
                showWavList.putExtra("type", "wav")
                startActivity(showWavList)
            }
        }
    }
}