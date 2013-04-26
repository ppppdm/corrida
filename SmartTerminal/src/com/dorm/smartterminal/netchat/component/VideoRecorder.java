package com.dorm.smartterminal.netchat.component;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.dorm.smartterminal.global.util.LogUtil;
import com.dorm.smartterminal.netchat.activiy.NetChart;

/**
 * video player:
 * 1. must call 'start preview' to start preview function,no need stop preview.
 * 2. must call 'start recorder' to start recorder,need stop .
 * 
 * @author Andy
 * 
 */
public class VideoRecorder {

    /*
     * recorder
     */

    // 设置预览界面的大小
    private int previewWidth = 320;
    private int previewHeight = 240;
    private int pictureWidth = 640;
    private int pictureHeight = 480;

    private SurfaceView surfaceView = null;
    private SurfaceHolder surfaceViewHolder = null;
    private Camera camera = null;
    // private boolean bIfPreview;

    
    Socket videoDataSocket = null;
    /*
     * logic
     */

    private NetChart netChart;

    public VideoRecorder(NetChart netChart) {

        super();

        this.netChart = netChart;
        surfaceView = netChart.surfaceView;

        getCamera();
        configCamera();

    }

    private void getCamera() {

        // 设置摄像头
        for (int i = Camera.getNumberOfCameras() - 1; i >= 0; i--) {

            camera = null;

            // 启动摄像头
            // 2.3版本后支持多摄像头,需传入参数
            camera = Camera.open(i);

            if (null != camera) {

                break;

            }
        }

        if (null == camera) {

            LogUtil.log(this, "camera is not found on this device.");

        }
        else {

            LogUtil.log(this, "get camera success.");

        }
    }

    private void configCamera() {

        // 如果摄像头不为空
        if (null != camera) {

            // 获取Camera settings
            Camera.Parameters parameters = camera.getParameters();

            /*
             * 【ImageFormat】JPEG/NV16(YCrCb format，used for
             * Video)/NV21(YCrCb format，used for Image)/RGB_565/YUY2/YU12
             */

            // 设置获取的图片格式
            parameters.setPictureFormat(PixelFormat.JPEG);

            // 设置预览图片格式
            parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);

            // 设置拍照和预览图片大小
            parameters.setPictureSize(pictureWidth, pictureHeight);
            parameters.setPreviewSize(previewWidth, previewHeight);

            // 设定配置参数
            camera.setParameters(parameters);

            // 日志
            LogUtil.log(this, "config camera success");
        }
    }

    /*
     * VIDEO_PREVIEW
     */

    /**
     * start preview
     */
    public void startPreview() {

        startSurfacePreview();
    }

    // 启动预览界面
    private void startSurfacePreview() {

        initSurfaceView();
    }

    private void initSurfaceView() {

        if (null != camera && null != surfaceView) {

            // 设置预览监听
            surfaceViewHolder = surfaceView.getHolder();
            surfaceViewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            surfaceViewHolder.addCallback(new SurfacePreviewHolderCallBack());

            // 日志
            LogUtil.log(this, "init surface view success.");

        }
    }

    /*
     * local video player
     */

    class SurfacePreviewHolderCallBack implements Callback {

        private boolean isPlaying = false;

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            setPreviewHolderIntoCamera();
        }

        private void setPreviewHolderIntoCamera() {

            if (null != camera) {

                try {

                    // set camera as source of surface view
                    camera.setPreviewDisplay(surfaceViewHolder);

                    // 日志
                    LogUtil.log(this, "set preview holder into camera success");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            LogUtil.log(this, "surface Changed");

            stopCameraProvidePreviewDataIfPlaying();

            startCameraProvidePreviewData();

        }

        private void stopCameraProvidePreviewDataIfPlaying() {

            if (null != camera & isPlaying) {

                // 停止视像头
                camera.stopPreview();
                isPlaying = false;

                // 日志
                LogUtil.log(this, "stop preview success");
            }
        }

        private void startCameraProvidePreviewData() {

            if (null != camera) {

                // 开启预览
                camera.startPreview();
                isPlaying = true;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            LogUtil.log(this, "surface destroyed");

            stopCameraProvidePreviewDataIfPlaying();
        }

    }

    /*
     * VIDEO_RECORDER
     */

    public void startRecorder() {

        if (camera != null) {

            // 初始化视频发送事件
            camera.setPreviewCallback(new CameraPreViewCallback());

            // 日志
            LogUtil.log(this, "start video recorder success");

        }
    }
    
    public void setDataSocket(Socket s){
        videoDataSocket = s;
    }

    class CameraPreViewCallback implements PreviewCallback {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            // LogUtil.log(this, "onPreviewFrame");

            byte[] image = convertDataToImage(data);

            //netChart.sendImageToService(image);
            if (videoDataSocket != null){
                try {
                    DataOutputStream out = new DataOutputStream(videoDataSocket.getOutputStream());
                    
                    out.write(image, 0, image.length);
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    videoDataSocket = null;
                }
            }

        }
    }

    private byte[] convertDataToImage(byte[] data) {
        // 获取摄像头参数
        Camera.Parameters params = camera.getParameters();

        int w = params.getPreviewSize().width;
        int h = params.getPreviewSize().height;
        int format = params.getPreviewFormat();

        // 获取预览图片YUV
        YuvImage image = new YuvImage(data, format, w, h, null);

        // YUV转换为JPEG
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Rect area = new Rect(0, 0, w, h);
        image.compressToJpeg(area, 50, out);

        return out.toByteArray();
    }

    public void stopRecorder() {

        stopPreview();
        stopRecord();
        releaseCamera();
    }

    private void stopPreview() {

        LogUtil.log(this, "stop preview success");
    }

    private void stopRecord() {

        if (null != camera) {

            camera.setPreviewCallback(null);

            LogUtil.log(this, "stop record success");
        }
    }

    private void releaseCamera() {

        if (null != camera) {

            camera.release();
            camera = null;

            // 日志
            LogUtil.log(this, "release camera success");
        }
    }

}
