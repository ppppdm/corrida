package com.dorm.smartterminal.netchat.component;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorder extends Thread {

    /*
     * LOG
     */

    private static final String TAG = "AUDIO_RECORDER";

    /*
     * NETWORK
     */

    // �����˿ڼ��������߳�
    protected Socket s;

    // ���������
    protected DataOutputStream outputStream;

    // �����Է�IP
    private String ServerSocketIp;

    /*
     * AUDIO_RECORDER
     */

    // ����¼����
    protected AudioRecord m_in_rec;

    // ����¼������
    protected int m_in_buf_size;
    protected byte[] m_in_bytes;

    // ����������������
    private int m_in_shorts_size;
    private short[] m_in_shorts;

    /*
     * MODULE
     */
    public void run() {

        // ��ʼ��¼���������桢�����˿ڡ��������
        initAudioRecorder();

        try {
            if (s != null) {

                s.close();
                s = null;

            }

            // ��ʼ�������˿�
            s = new Socket(ServerSocketIp, 5331);

            // ��־
            Log.v("AUDIO_GET_DEMO", "server socket accept");

            // ��ȡ�����
            outputStream = new DataOutputStream(s.getOutputStream());

            // ��ʼ¼��
            m_in_rec.startRecording();

            // ���ʼ�ձ���¼��
            while (s != null && s.isConnected()) {

                // ��ȡһ����Ƶ������ȡ��ȡ�ĳ���
                int len = m_in_rec.read(m_in_shorts, 0, m_in_shorts_size);

                // ��������
                //calc1(m_in_shorts, 0, len);

                // ת��Ϊbyte����
                m_in_bytes = my_short_to_byte(m_in_shorts);

                // �����Ƶ
                outputStream.write(m_in_bytes, 0, len * 2);

            }
        } catch (UnknownHostException e1) {

            // ��־
            Log.i(TAG, "connect to audio player failure");

        } catch (IllegalStateException e1) {

            // ��־
            Log.i(TAG, "start audio recorder failure");

        } catch (IOException e1) {

            // ��־
            Log.i(TAG, "socket accept failure");

        } finally {

            try {

                if (outputStream != null) {

                    outputStream.close();
                    outputStream = null;

                }

                if (s != null) {

                    s.close();
                    s = null;

                }

            } catch (IOException e) {

                // ��־
                Log.i(TAG, "output stream close faillure");

            }

            if (m_in_rec != null) {

                // ֹͣ¼��
                m_in_rec.stop();
                m_in_rec = null;

            }
            m_in_bytes = null;

        }
    }

    public void initAudioRecorder() {

        // ��ʼ��¼����
        m_in_buf_size = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, m_in_buf_size);

        // ��������
        m_in_bytes = new byte[m_in_buf_size];

        m_in_shorts_size = m_in_buf_size >> 1;
        m_in_shorts = new short[m_in_shorts_size];

    }

    public void resetAudioRecorder() {

        try {

            if (s != null) {

                s.close();
                s = null;

                // ��־
                Log.i(TAG, "reset audio recorder success");

            }
        } catch (IOException e) {

            // ��־
            Log.i(TAG, "reset audio recorder failure");

        }

    }

    public void destoryAudioRecorder() {
        
        resetAudioRecorder();

    }
    
    /*
     * AUDIO_CACL
     */

    // ��������
    /*
    private void calc1(short[] lin, int off, int len) {
        int i, j;

        for (i = 0; i < len; i++) {
            j = lin[i + off];
            lin[i + off] = (short) (j >> 2);
        }
    }
    */
    // ��������ת��
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