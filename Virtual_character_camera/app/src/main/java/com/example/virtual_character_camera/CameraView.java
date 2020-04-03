package com.example.virtual_character_camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

    //attributes only used to be passed to FaceDetector
    Context orig_context;
    CharacterView orig_characterView;

    Camera camera;
    Camera.Parameters params;
    SurfaceHolder surfaceHolder;
    SurfaceTexture surfaceTexture;
    FaceDetector faceDetector;

    //size and buffer for camera setting
    int dynamic_width = 480, dynamic_height = 320;
    int bufferSize;
    byte[] buffer;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        orig_context = context;
    }

    //called by CharacterView to pass the original activity
    public void init(CharacterView activity) {
        orig_characterView = activity;
        surfaceHolder = getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(this);

        surfaceTexture = new SurfaceTexture(6);
        faceDetector = new FaceDetector(orig_context, orig_characterView);
    }

    //open the camera
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera == null)
                camera = Camera.open(1);
            camera.setPreviewTexture(surfaceTexture);
            params = camera.getParameters();

            //get supported preview size, with height between 320 and 400
            List<Camera.Size> list_sizes = params.getSupportedPreviewSizes();
            for (int i = 0; i < list_sizes.size(); i++) {
                Camera.Size temp_size = list_sizes.get(i);
                if(temp_size.height>=320&& temp_size.height<=400){
                    dynamic_width= temp_size.width;
                    dynamic_height= temp_size.height;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //set preview
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) {
            try {
                params.setPreviewSize(dynamic_width, dynamic_height);
                params.setPreviewFormat(ImageFormat.NV21);
                camera.setParameters(params);

                bufferSize = dynamic_width * dynamic_height * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
                buffer = new byte[bufferSize];
                camera.addCallbackBuffer(buffer);
                camera.setPreviewCallbackWithBuffer(new FrameCallback());
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.setPreviewCallbackWithBuffer(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    //process each frame and draw it on the screen
    class FrameCallback implements Camera.PreviewCallback{
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            camera.addCallbackBuffer(buffer);
            YuvImage image = new YuvImage(data, ImageFormat.NV21, dynamic_width, dynamic_height, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);

            if (image.compressToJpeg(new Rect(0, 0, dynamic_width, dynamic_height), 100, os)) {
                byte[] tmp = os.toByteArray();
                Bitmap src = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);

                //mirror flip the image, because of using the front camera
                /*
                Matrix matrix = new Matrix();
                matrix.setRotate(-90);
                matrix.postTranslate(0, src.getWidth());

                matrix.postScale(-1, 1);
                matrix.postTranslate(src.getWidth(), 0);//right，up

                 */

                //mirror flip the image, because of using the front camera
                Matrix matrix = new Matrix();
                matrix.postScale(-1, 1);
                matrix.postTranslate(src.getWidth(), 0);

                //adjust the position and direction of image
                matrix.setRotate(-90);
                matrix.postTranslate(0, src.getHeight());

                matrix.postScale(-1, 1);
                matrix.postTranslate(src.getWidth(), 0);

                matrix.postTranslate(-src.getWidth()+src.getHeight(), 0);
                final Bitmap dst = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
                new Canvas(dst).drawBitmap(src, matrix, new Paint());

                synchronized (surfaceHolder) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    canvas.drawBitmap(dst, 0, 0, null);
                    faceDetector.getLandmarks(canvas, dst);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
