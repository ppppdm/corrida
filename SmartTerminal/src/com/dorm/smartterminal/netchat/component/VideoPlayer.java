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

    // 构造函数
    public VideoPlayer(NetChart netChart) {

        // 获取主Activity对象
        this.netChart = netChart;

        imageView = netChart.imageView;

        // 初始化视频播放器
        initVideoPlayer();

    }

    private void initVideoPlayer() {

        imageViewHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);

                // 获取返回消息
                Bundle b = msg.getData();
                byte[] image = b.getByteArray("image");

                // LogUtil.log(this, "" + image.length);

                // 将返回消息转换为图片
                Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);

                // 显示图片
                imageView.setImageBitmap(bm);
                imageView.invalidate();
            }
        };
    }

    public void showImage(byte[] image) {

        // 显示返回消息
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putByteArray("image", image);
        msg.setData(b);
        msg.what = 0;
        imageViewHandler.sendMessage(msg);
    }
}
