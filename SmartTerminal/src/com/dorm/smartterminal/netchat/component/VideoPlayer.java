package com.dorm.smartterminal.netchat.component;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dorm.smartterminal.global.util.LogUtil;
import com.dorm.smartterminal.netchat.activiy.NetChart;

/**
 * video player, no need start, close. show image directly.
 * 
 * @author Andy
 * 
 */
public class VideoPlayer {

    /*
     * VIDEO_PLAYER
     */
    private Messenger imageViewHandler;
    
    private Socket serverSock = null;
	DataInputStream inputStream = null;

	private static final String TAG = "VIDEO_PLAYER";

    public void initVideoPlayer(Socket s) {

    	serverSock = s;
	}
    
    public void setMessenger(Messenger h)
    {
        imageViewHandler = h;
    }
    
    public void startVideoPlayer() {

		new Thread(new Runnable() {

			public void run() {

				try {



					// ��ȡ������Ϣ������
					inputStream = new DataInputStream(
							serverSock.getInputStream());

					// ѭ�����շ�����Ϣ
					while (serverSock != null && serverSock.isConnected()) {

						// ��ȡ������Ϣ�ĳ���
						int length;
						length = inputStream.readInt();

						// ��־
						Log.i(TAG, "reveive length = " + length);

						// ����������Ϣ
						byte[] buffer = new byte[length];

						int len = 0;
						int sum = 0;

						// ��ȡ������Ϣ
						do {

							len = inputStream.read(buffer, sum, length - sum);

							if (len == -1) {

								break;

							} else {

								sum += len;

							}

						} while (sum < length);

						// ��־
						Log.i(TAG, "sum " + sum);

						// ��ʾ������Ϣ
						Message msg = new Message();
						Bundle b = new Bundle();
						b.putByteArray("buffer", buffer);
						msg.setData(b);
						msg.what = 0;
						try {
                            imageViewHandler.send(msg);
                        }
                        catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            LogUtil.log(this, "RemoteException");
                        }

					}

					if (inputStream != null) {

						inputStream.close();
						inputStream = null;

					}

					

				} catch (IOException e) {

					// ��־
					Log.i(TAG, "socket accept failure");

					try {

						if (inputStream != null) {

							inputStream.close();
							inputStream = null;

						}

					} catch (IOException e1) {

						// ��־
						Log.i(TAG, "input stream close faillure");

					}

					resetVideoPlayer();

				}
			}
		}).start();
	}
    
    public void resetVideoPlayer() {



			if (serverSock != null) {

				serverSock = null;

				// ��־
				Log.i(TAG, "reset video player success");

			}


	}
   
}
