package com.dorm.smartterminal.netchat.component;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

public class AudioRecorder extends Thread {

    /*
     * LOG
     */

    private static final String TAG = "AUDIO_RECORDER";

    /*
     * NETWORK
     */

    // 创建端口监听接收线程
    protected Socket s;

    // 创建输出流
    protected DataOutputStream outputStream;

    // 创建对方IP
    private String ServerSocketIp;

    /*
     * AUDIO_RECORDER
     */

    // 创建录音器
    protected AudioRecord m_in_rec;

    // 创建录音缓存
    protected int m_in_buf_size;
    protected byte[] m_in_bytes;

    // 创建噪音消除缓存
    private int m_in_shorts_size;
    private short[] m_in_shorts;

    /*
     * MODULE
     */
    public void run() {

        // 初始化录音器、缓存、监听端口、输出流等
        // initAudioRecorder();

        try {
   

            // 初始化监听端口
            //s = new Socket(ServerSocketIp, 5331);

            // 日志
            //Log.v("AUDIO_GET_DEMO", "server socket accept");

            // 获取输出流
            outputStream = new DataOutputStream(s.getOutputStream());

            // 开始录音
            m_in_rec.startRecording();

            // 如果始终保持录音
            while (s != null && s.isConnected()) {

                // 读取一段音频，并获取读取的长度
                int len = m_in_rec.read(m_in_bytes, 0, m_in_buf_size);

                Log.i(TAG, "rec read " + len);
                // 削弱回音
                //calc1(m_in_shorts, 0, len);

                // 转换为byte数组
                //m_in_bytes = my_short_to_byte(m_in_shorts);

                // 输出音频
                outputStream.write(m_in_bytes, 0, len);
                Log.i(TAG, "rec socket write ");
            }
        }  catch (IllegalStateException e1) {

            // 日志
            Log.i(TAG, "start audio recorder failure");

        } catch (IOException e1) {

            // 日志
            Log.i(TAG, "rec socket failure");

        } finally {

            try {

                if (outputStream != null) {

                    outputStream.close();
                    outputStream = null;

                }

            } catch (IOException e) {

                // 日志
                Log.i(TAG, "output stream close faillure");

            }
            
            resetAudioRecorder();

            destoryAudioRecorder();

        }
    }

    public int  initAudioRecorder(Socket socket) {

        // 初始化录音机
        m_in_buf_size = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, m_in_buf_size);

        // 创建缓存
        m_in_bytes = new byte[m_in_buf_size];

        //m_in_shorts_size = m_in_buf_size >> 1;
        //m_in_shorts = new short[m_in_shorts_size];
        
        //Toast.makeText(getApplicationContext(), text, duration)

        s = socket;
        
        return m_in_buf_size;
    }

    public void resetAudioRecorder() {


            if (s != null) {

                //s.close();
                s = null;

                // 日志
                Log.i(TAG, "reset audio recorder success");}

    }

    public void destoryAudioRecorder() {
        
        if (m_in_rec != null) {

            // 暂停播放
            m_in_rec.release();

        }
        m_in_bytes = null;

    }
    
    /*
     * AUDIO_CACL
     */

    // 回音消除
    /*
    private void calc1(short[] lin, int off, int len) {
        int i, j;

        for (i = 0; i < len; i++) {
            j = lin[i + off];
            lin[i + off] = (short) (j >> 2);
        }
    }
    */
    // 缓存类型转换
    private byte[] my_short_to_byte(short[] s) {
        int offset, j;
        int bytes_len = s.length * 2;
        byte[] newbyte = new byte[bytes_len];

        for (int i = 0; i < newbyte.length; i++) {
            offset = ((i) & 0x1) * 8;
            j = i >> 1;
            newbyte[i] = (byte) ((s[j] >>> offset) & 0xff);
        }
        return newbyte;
    }

    /*
     * MODULE
     */

    public void setServerSocketIp(String IP) {

        ServerSocketIp = IP;

    }
}
