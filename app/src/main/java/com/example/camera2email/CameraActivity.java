package com.example.camera2email;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends BaseActivity {
    private final static String TAG = "tag";

    private TextureView mTextureView;
    private CameraControl mCC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mTextureView = findViewById(R.id.texture);
        mCC = new CameraControl(this, mTextureView);
        mCC.openCamera();
    }

    public void onBtnShot(View v) {
        preview2File();

        Intent intent = new Intent();
        intent.putExtra(NEW_IMAGE, true);
        setResult(Activity.RESULT_OK, intent);
        finish();
        // 이미지 파일을 화면에 표시
        //image2Screen();
    }

    boolean preview2File() {
        // TextureView 에서 이미지를 캡쳐한다
        Bitmap bmp = mTextureView.getBitmap();

        // 캡쳐 이미지 데이터를 JPEG 형식으로 변경
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //저장된이미지를 jpeg로 포맷 품질 95으로하여 출력
        bmp.compress(Bitmap.CompressFormat.JPEG, 95, bos);
        byte[] bytes = bos.toByteArray();

        // 이미지를 파일로 저장
        File file = getImageFile();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            Toast.makeText(getApplicationContext(), "Image File saved!",
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.d("tag", "File Write Error");
            return false;
        }
        return true;
    }

}
