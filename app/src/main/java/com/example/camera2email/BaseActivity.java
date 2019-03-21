package com.example.camera2email;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;

public class BaseActivity extends AppCompatActivity {
    final int REQUEST_CAMERA_RESULT = 1;
    final String NEW_IMAGE = "NewImage";
    SharedPreferences mPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = getSharedPreferences("setup", MODE_PRIVATE);
    }

    public void putLocalStringIfEmpty(String strKey, String strValue) {
        String str = getLocalString(strKey);
        if( isStringValid(str) )
            return;
        putLocalString(strKey, strValue);
    }

    public void putLocalString(String strKey, String strValue) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(strKey, strValue);
        editor.commit();
    }

    public String getLocalString(String strKey) {
        String strValue = mPref.getString(strKey, "");
        return strValue;
    }

    public boolean isStringValid(String str) {
        if( str != null && str.length() > 0 )
            return true;
        return false;
    }

    // 사용자 권한 체크
    public void checkPermission() {
        // 안드로이드 마시멜로우 이후 버전이라면 사용자에게 Permission 을 획득한다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // 사용자가 Permission 을 부여하지 않았다면 권한을 요청하는 팝업창을 표시
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED){
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
                    Toast.makeText(this,"No Permission to use the Camera services", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {android.Manifest.permission.CAMERA},REQUEST_CAMERA_RESULT);
                return;
            }
        }
    }

    // 권한 요청 결과 이벤트 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            // 카메라 사용 권한 요청 결과 일때
            case REQUEST_CAMERA_RESULT:
                // 사용자가 권한 부여를 거절 했을때
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Cannot run application because camera service permission have not been granted", Toast.LENGTH_SHORT).show();
                }
                // 사용자가 권한을 부여했을때
                else {
                    //mCC.openCamera();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    public File getImageFile() {
        File file = getFileStreamPath("camerashot.jpg");
        return file;
    }

    public void makeToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

}
