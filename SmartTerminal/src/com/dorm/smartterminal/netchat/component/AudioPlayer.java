package com.dorm.smartterminal.netchat.component;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioPlayer extends Thread {

    /*
     * LOG
     */

    private static final String TAG = "AUDIO_PLAYER";

    /*
     * NETWORK
     */

    // 创建端口，等待连接
    private Socket serverSock;
    private ServerSocket mServerSocket = null;

    // 创建输入流
    private DataInputStream inputStream;

    /*
     * AUDIO_PLAYER
     */

    // 创建音频播放器
    protected AudioTrack m_out_trk;

    // 创建缓存
    protected int m_out_buf_size;
    protected byte[] m_out_bytes;

    /*
     * MODULE
     */

    // 创建变量，记录是否保持运行
    protected boolean m_keep_running;

    public void run() {

        // 初始化网络、播放器、缓存等
        initAudioPlayer();

        try {
            // 日志
            Log.i(TAG, "serverSock accept ");

            if (mServerSocket != null) {

                mServerSocket.close();
                mServerSocket = null;

            }

            if (serverSock != null) {

                serverSock.close();
                serverSock = null;

            }

            // 等待对方连接
            mServerSocket = new ServerSocket(5331);
            serverSock = mServerSocket.accept();

            // 日志
            Log.i(TAG, "socket accept failure");

            // 获取输入流
            inputStream = new DataInputStream(serverSock.getInputStream());

            // 开始播放
            m_out_trk.play();

            // 如果当前保持运行
            while (serverSock != null && serverSock.isConnected()) {

                // 读取音频
                int len = inputStream.read(m_out_bytes, 0, m_out_buf_size);

                // 播放音频
                m_out_trk.write(m_out_bytes, 0, len);

            }
        } catch (IllegalStateException e1) {

            // 日志
            Log.i(TAG, "start audio player failure");

        } catch (IOException e1) {

            // 日志
            Log.i(TAG, "socket accept failure");

        } finally {

            try {

                if (inputStream != null) {

                    inputStream.close();
                    inputStream = null;

                }

            } catch (IOException e1) {

                // 日志
                Log.i(TAG, "input stream close faillure");

            }
            
            resetAudioPlayer();

            destoryAudioPlayer();
            
        }
    }

    public void initAudioPlayer() {

        // 初始化音频播放器
        m_out_buf_size = AudioTrack.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, m_out_buf_size,
                AudioTrack.MODE_STREAM);

        // 初始化缓存
        m_out_bytes = new byte[m_out_buf_size];

        // 设置变量，保持运行
        m_keep_running = true;

    }

    public void resetAudioPlayer() {

        try {

            if (serverSock != null) {

                serverSock.close();
                serverSock = null;

                // 日志
                Log.i(TAG, "reset audio player success");

            }

            if (mServerSocket != null) {

                mServerSocket.close();
                mServerSocket = null;

            }
        } catch (IOException e) {

            // 日志
            Log.i(TAG, "reset audio player failure");

        }

    }

    public void destoryAudioPlayer() {

        if (m_out_trk != null) {

            // 暂停播放
            m_out_trk.release();

        }

    }

}