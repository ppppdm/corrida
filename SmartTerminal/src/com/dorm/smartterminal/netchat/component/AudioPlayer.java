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

    // �����˿ڣ��ȴ�����
    private Socket serverSock;
    private ServerSocket mServerSocket = null;

    // ����������
    private DataInputStream inputStream;

    /*
     * AUDIO_PLAYER
     */

    // ������Ƶ������
    protected AudioTrack m_out_trk;

    // ��������
    protected int m_out_buf_size;
    protected byte[] m_out_bytes;

    /*
     * MODULE
     */

    // ������������¼�Ƿ񱣳�����
    protected boolean m_keep_running;

    public void run() {

        // ��ʼ�����硢�������������
        //initAudioPlayer();

        try {
            // ��־
            //Log.i(TAG, "serverSock accept ");


            // �ȴ��Է�����
            //mServerSocket = new ServerSocket(5331);
            //serverSock = mServerSocket.accept();

            // ��־
            //Log.i(TAG, "socket accept failure");

            // ��ȡ������
            inputStream = new DataInputStream(serverSock.getInputStream());

            // ��ʼ����
            m_out_trk.play();

            // �����ǰ��������
            while (serverSock != null && serverSock.isConnected()) {

                // ��ȡ��Ƶ
                int len = inputStream.read(m_out_bytes, 0, m_out_buf_size);
                Log.i(TAG, "ply socket read " + len);
                
                if(len != -1){
                	 // ������Ƶ
                    m_out_trk.write(m_out_bytes, 0, len);
                    Log.i(TAG, "ply write ");
                    
                }
                else{
                    /*
                	try {
						sleep(1000);
					} 	catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					*/
                }

            }
        } catch (IllegalStateException e1) {

            // ��־
            Log.i(TAG, "start audio player failure");

        } catch (IOException e1) {

            // ��־
            Log.i(TAG, "ply socket  failure");

        } finally {

            try {

                if (inputStream != null) {

                    inputStream.close();
                    inputStream = null;

                }

            } catch (IOException e1) {

                // ��־
                Log.i(TAG, "input stream close faillure");

            }
            
            resetAudioPlayer();

            destoryAudioPlayer();
            
        }
    }

    public void initAudioPlayer(Socket socket) {

        // ��ʼ����Ƶ������
        m_out_buf_size = AudioTrack.getMinBufferSize(8000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, m_out_buf_size,
                AudioTrack.MODE_STREAM);

        // ��ʼ������
        m_out_bytes = new byte[m_out_buf_size];

        // ���ñ�������������
        m_keep_running = true;
        
        serverSock = socket;

    }

    public void resetAudioPlayer() {

        

            if (serverSock != null) {

                //serverSock.close();
                serverSock = null;

                // ��־
                Log.i(TAG, "reset audio player success");

            }

            if (mServerSocket != null) {

                //mServerSocket.close();
                mServerSocket = null;

            }
    

    }

    public void destoryAudioPlayer() {

        if (m_out_trk != null) {

            // ��ͣ����
            m_out_trk.release();

        }

    }

}