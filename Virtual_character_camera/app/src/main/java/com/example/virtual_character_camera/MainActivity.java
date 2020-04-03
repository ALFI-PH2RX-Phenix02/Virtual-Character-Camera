package com.example.virtual_character_camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.tzutalin.dlib.Constants;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String targetPath = Constants.getFaceShapeModelPath();
        if (!new File(targetPath).exists()) {
            FileUtils.copyFileFromRawToOthers(getApplicationContext(), R.raw.shape_predictor_68_face_landmarks, targetPath);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }
    //start the CharacterView activity
    public void click_try(View view)
    {
        Intent intent = new Intent(this, CharacterView.class);
        startActivity(intent);
    }
}
