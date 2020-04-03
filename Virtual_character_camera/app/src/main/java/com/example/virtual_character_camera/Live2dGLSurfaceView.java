package com.example.virtual_character_camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.live2d.android.Live2DModelAndroid;
import jp.live2d.android.UtOpenGL;
import framework.L2DEyeBlink;
import framework.L2DStandardID;

public class Live2dGLSurfaceView extends GLSurfaceView {

    Live2dRenderer mLive2dRenderer;
    private Context mContext;

    //construct function
    public Live2dGLSurfaceView(CharacterView activity, String model_path, String[] texture_paths) {
        super(activity);
        this.mContext = activity;

        //set the background of the model transparent so that the background can be seen. However, it must stay on the top
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setZOrderOnTop(true);

        //set Renderer
        this.mLive2dRenderer = new Live2dRenderer(activity, model_path, texture_paths);
        this.setRenderer(this.mLive2dRenderer);
    }

    class Live2dRenderer implements Renderer {

        private CharacterView Activity;
        private Live2DModelAndroid live2DModel;
        private L2DEyeBlink mEyeBlink;
        private String model_path;
        private String[] texture_paths;

        //construct function
        public Live2dRenderer(CharacterView activity, String model_path, String[] texture_paths) {
            this.Activity = activity;
            this.model_path = model_path;
            this.texture_paths = texture_paths;
            this.mEyeBlink = new L2DEyeBlink();
        }

        //the render process
        @Override
        public void onDrawFrame(GL10 gl){
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            mEyeBlink.updateParam(live2DModel);

            //adjust the scale of movement of the model
            float scale = 0.5f;
            float scalez = 0.8f;

            live2DModel.setParamFloat(L2DStandardID.PARAM_ANGLE_Z, (float) Activity.emotion[0], scalez);
            live2DModel.setParamFloat(L2DStandardID.PARAM_ANGLE_X , (float) Activity.emotion[1], scale);
            live2DModel.setParamFloat(L2DStandardID.PARAM_ANGLE_Y , (float) Activity.emotion[2], scale);
            live2DModel.setParamFloat(L2DStandardID.PARAM_MOUTH_OPEN_Y, (float) Activity.emotion[3], scale);
            live2DModel.setParamFloat(L2DStandardID.PARAM_MOUTH_FORM, (float) Activity.emotion[3], scale);

            live2DModel.setGL(gl);
            live2DModel.update();
            live2DModel.draw();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height){
            gl.glViewport((int)(-width/(3)) , (int)(-height/(1.5)) , (int)(width*1.7) , (int)(height*1.7));
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            float modelWidth = live2DModel.getCanvasWidth();
            float aspect = (float)width/height;

            gl.glOrthof(0, modelWidth, modelWidth / aspect, 20f, 0.5f, -0.5f);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config){
            try {
                InputStream in = this.Activity.getAssets().open(model_path);
                live2DModel = Live2DModelAndroid.loadModel(in);
                in.close();

                for (int i = 0; i < texture_paths.length; i++) {
                    InputStream tin = this.Activity.getAssets().open(texture_paths[i]);
                    int texNo = UtOpenGL.loadTexture(gl, tin, true);
                    live2DModel.setTexture(i, texNo);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}


