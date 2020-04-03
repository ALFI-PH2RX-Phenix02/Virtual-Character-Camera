package com.example.virtual_character_camera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class CharacterView extends AppCompatActivity {
    CameraView cameraView;
    double[] emotion = new double[10];
    public Live2dGLSurfaceView l2dGLSurfaceView;
    RelativeLayout main;
    SeekBar scale;

    GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        main = (RelativeLayout) findViewById(R.id.main);
        cameraView=(CameraView)findViewById(R.id.camera);
        scale = (SeekBar) findViewById(R.id.scale);

        cameraView.init(this);
        scale.setOnSeekBarChangeListener(listener);

        //set the first model
        setl2dModel();

        //set Gesture detector
        setDetector();
    }

    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        Bundle b=getIntent().getExtras();
        String character = b.getString("character");
        setup(character);
    }

    //set the first model
    public void setl2dModel(){
        String model_path = null;
        String[] texture_paths = null;
        model_path = "live2d/nepgear/nepgear.moc";
        texture_paths = new String[]{
                "live2d/nepgear/nepgear.1024/texture_00.png",
                "live2d/nepgear/nepgear.1024/texture_01.png",
                "live2d/nepgear/nepgear.1024/texture_02.png",
                "live2d/nepgear/nepgear.1024/texture_03.png"
        };
        l2dGLSurfaceView = new Live2dGLSurfaceView(CharacterView.this,model_path,texture_paths);
        main.addView(l2dGLSurfaceView);
    }

    //when the switch is clicked, hide or display the camera preview
    public void click_hide(View view){
        CameraView c=findViewById(R.id.camera);
        if(c.getAlpha()!=0)
            c.setAlpha(0);
        else
            c.setAlpha(1);
    }

    //when the change button is clicked, open the listview
    public void click_change(View view){
        Intent intent = new Intent(this, CharChange.class);
        startActivity(intent);
    }

    //get character name from listView after selection, remove the original model and set the new model
    public void setup(String str){
        final String MODEL_PATH = "live2d/"+str+"/"+str+".moc";
        final String[] TEXTURE_PATHS = {
                "live2d/"+str+"/"+str+".1024/texture_00.png",
                "live2d/"+str+"/"+str+".1024/texture_01.png",
                "live2d/"+str+"/"+str+".1024/texture_02.png",
                "live2d/"+str+"/"+str+".1024/texture_03.png"
        };
        RelativeLayout container = (RelativeLayout) findViewById(R.id.main);
        container.removeView(l2dGLSurfaceView);
        l2dGLSurfaceView = new Live2dGLSurfaceView(CharacterView.this, MODEL_PATH, TEXTURE_PATHS);
        container.addView(l2dGLSurfaceView);
        setDetector();
    }

    //when dragging the seekbar, adjust the size of the model dynamically
    SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
        float temp;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            temp = scale.getProgress();
            l2dGLSurfaceView.setScaleX(temp/50);
            l2dGLSurfaceView.setScaleY(temp/50);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            temp = scale.getProgress();
            l2dGLSurfaceView.setScaleX(temp/50);
            l2dGLSurfaceView.setScaleY(temp/50);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            temp = scale.getProgress();
            l2dGLSurfaceView.setScaleX(temp/50);
            l2dGLSurfaceView.setScaleY(temp/50);
        }
    };

    //when dragging, move the position of the model
    public void setDetector(){
        l2dGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                l2dGLSurfaceView.setTranslationX(e2.getRawX()-e1.getRawX());
                l2dGLSurfaceView.setTranslationY(e2.getRawY()-e1.getRawY());
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }
}