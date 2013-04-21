package com.dorm.smartterminal.netchat.component;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import com.dorm.smartterminal.global.util.LogUtil;
import com.dorm.smartterminal.netchat.activiy.NetChart;

public class VideoRecorder {

    /*
     * recorder
     */

    // ����Ԥ������Ĵ�С
    private int previewWidth = 320;
    private int previewHeight = 240;
    private int pictureWidth = 640;
    private int pictureHeight = 480;

    private SurfaceView surfaceView = null;
    private SurfaceHolder surfaceViewHolder = null;
    private Camera camera = null;
    // private boolean bIfPreview;

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

        // ��������ͷ
        for (int i = Camera.getNumberOfCameras() - 1; i >= 0; i--) {

            camera = null;

            // ��������ͷ
            // 2.3�汾��֧�ֶ�����ͷ,�贫�����
            camera = Camera.open(i);

            if (null != camera) {

                break;

            }
        }

        if (null == camera) {

            LogUtil.log(this, "camera is not found on this device.");

        }
    }

    private void configCamera() {

        // �������ͷ��Ϊ��
        if (null != camera) {

            // ��ȡCamera settings
            Camera.Parameters parameters = camera.getParameters();

            /*
             * ��ImageFormat��JPEG/NV16(YCrCb format��used for
             * Video)/NV21(YCrCb format��used for Image)/RGB_565/YUY2/YU12
             */

            // ���û�ȡ��ͼƬ��ʽ
            parameters.setPictureFormat(PixelFormat.JPEG);

            // ����Ԥ��ͼƬ��ʽ
            parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);

            // �������պ�Ԥ��ͼƬ��С
            parameters.setPictureSize(pictureWidth, pictureHeight);
            parameters.setPreviewSize(previewWidth, previewHeight);

            // �趨���ò���
            camera.setParameters(parameters);

            // ��־
            LogUtil.log(this, "start camera preview success");
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

    // ����Ԥ������
    private void startSurfacePreview() {

        initSurfaceView();
    }

    private void initSurfaceView() {

        if (null != camera && null != surfaceView) {

            // ����Ԥ������
            surfaceViewHolder = surfaceView.getHolder();
            surfaceViewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            surfaceViewHolder.addCallback(new LocalVideoPlayerHolderCallBack());

            // ��־
            LogUtil.log(this, "start video preview success.");

        }
    }

    /*
     * local video player
     */

    class LocalVideoPlayerHolderCallBack implements Callback {

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

                    // ��־
                    LogUtil.log(this, "set preview holder into camera success");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            LogUtil.log(this, "Surface Changed");

            stopCameraProvidePreviewDataIfPlaying();

            startCameraProvidePreviewData();

        }

        private void stopCameraProvidePreviewDataIfPlaying() {

            if (null != camera & isPlaying) {

                // ֹͣ����ͷ
                camera.stopPreview();
                isPlaying = false;

                // ��־
                LogUtil.log(this, "stop camera preview");
            }
        }

        private void startCameraProvidePreviewData() {

            if (null != camera) {

                // ����Ԥ��
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

            // ��ʼ����Ƶ�����¼�
            camera.setPreviewCallback(new CameraPreViewCallback());

            // ��־
            LogUtil.log(this, "init video recorder success");

        }
    }

    class CameraPreViewCallback implements PreviewCallback {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            LogUtil.log(this, "onPreviewFrame");

            convertDataToImage(data);

            netChart.sendImageToService(data);

        }
    }

    private byte[] convertDataToImage(byte[] data) {
        // ��ȡ����ͷ����
        Camera.Parameters params = camera.getParameters();

        int w = params.getPreviewSize().width;
        int h = params.getPreviewSize().height;
        int format = params.getPreviewFormat();

        // ��ȡԤ��ͼƬYUV
        YuvImage image = new YuvImage(data, format, w, h, null);

        // YUVת��ΪJPEG
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

            // ��־
            LogUtil.log(this, "release camera success");
        }
    }

}
