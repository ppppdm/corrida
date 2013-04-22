package com.dorm.smartterminal.netchat.component;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.dorm.smartterminal.netchat.activiy.NetChart;

public class VideoPlayer {

    /*
     * VIDEO_PLAYER
     */

    private ImageView imageView = null;
    private Handler imageViewHandler;

    /*
     * MODULE
     */
    private NetChart netChart = null;

    // ���캯��
    public VideoPlayer(NetChart netChart) {

        // ��ȡ��Activity����
        this.netChart = netChart;

        imageView = netChart.imageView;

        // ��ʼ����Ƶ������
        initVideoPlayer();

    }

    private void initVideoPlayer() {

        imageViewHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);

                // ��ȡ������Ϣ
                Bundle b = msg.getData();
                byte[] image = b.getByteArray("image");

                // LogUtil.log(this, "" + image.length);

                // ��������Ϣת��ΪͼƬ
                Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);

                // ��ʾͼƬ
                imageView.setImageBitmap(bm);
                imageView.invalidate();
            }
        };
    }

    public void showImage(byte[] image) {

        // ��ʾ������Ϣ
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putByteArray("image", image);
        msg.setData(b);
        msg.what = 0;
        imageViewHandler.sendMessage(msg);
    }
}
