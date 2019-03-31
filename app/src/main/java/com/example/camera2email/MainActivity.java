package com.example.camera2email;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

/*
 * Camera2Email : Take a picture by camera and send image file to Email
 * Author : DONGGEUN JUNG (Dennis)
 * Email : topsan72@gmail.com / topofsan@naver.com
 */

public class MainActivity extends BaseActivity {
    final String KEY_MY_NAME = "MyName";
    final String KEY_RECEIVE_EMAIL = "ReceiveEmail";
    final int ACT_CAMERA = 0;
    final String SENDER_ID = "topsan72@gmail.com";
    final String SENDER_PW = "wjlpepxmfhmmudvj";

    EditText editSenderName;
    EditText editMailText;
    EditText editReceiveEmail;
    ImageView ivPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editSenderName = findViewById(R.id.editSenderName);
        editMailText = findViewById(R.id.editMailText);
        editMailText.setText("Email text");
        editReceiveEmail = findViewById(R.id.editReceiveEmail);
        ivPicture = findViewById(R.id.ivPicture);

        putDefaultValue2Local();
        getValueInLocal();

        // 이미지 파일을 화면에 표시
        image2Screen();
        // 사용자 권한 체크
        checkPermission();

        // Email send init
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        putNewValue2Local();
    }

    void getValueInLocal() {
        String str = getLocalString(KEY_MY_NAME);
        editSenderName.setText(str);

        str = getLocalString(KEY_RECEIVE_EMAIL);
        editReceiveEmail.setText(str);
    }

    void putNewValue2Local() {
        String str = editSenderName.getText().toString();
        putLocalString(KEY_MY_NAME, str);

        str = editReceiveEmail.getText().toString();
        putLocalString(KEY_RECEIVE_EMAIL, str);
    }

    void putDefaultValue2Local() {
        putLocalStringIfEmpty(KEY_RECEIVE_EMAIL, "topsan72@gmail.com");
        //putLocalString(KEY_RECEIVE_EMAIL, "topsan72@gmail.com");

    }

    public void onBtnCamera(View v) {
        Intent intent = new Intent(this, CameraActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, ACT_CAMERA);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACT_CAMERA :
                if (resultCode == Activity.RESULT_OK) {
                    boolean newImage = data.getBooleanExtra(NEW_IMAGE, false);
                    if( newImage ) {
                        // 이미지 파일을 화면에 표시
                        image2Screen();
                    }
                }
                break;
        }
    }

    // 이미지 파일을 화면에 표시
    void image2Screen() {
        File file = getImageFile();
        if( file.exists() ) {
            Bitmap bmpFile = BitmapFactory.decodeFile(file.getPath());
            ivPicture.setImageBitmap(bmpFile);
        }
    }

    public void onBtnSend(View v) {
        String strSenderName = editSenderName.getText().toString();
        if( isStringValid(strSenderName) == false ) {
            makeToast("'Sender Name' is empty");
            return;
        }

        String strMailText = editMailText.getText().toString();

        String strReceiveEmail = editReceiveEmail.getText().toString();
        if( isStringValid(strReceiveEmail) == false ) {
            makeToast("'To(Email address)' is empty");
            return;
        }

        String strTitle = "[" + strSenderName + "] ";
        int pos = strMailText.indexOf("\n");
        if( pos < 0 ) pos = strMailText.length();
        strTitle += strMailText.substring(0, pos);

        File file = getImageFile();
        String filePath = "";
        if( file.exists() )
            filePath = file.getPath();
        sendEmail(strTitle, strMailText,
                strReceiveEmail, filePath);
    }

    void sendEmail(String strTitle, String strMessage,
                   String strAddress, String filePath) {
        try {
            GMailSender gMailSender = new GMailSender(SENDER_ID, SENDER_PW);
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            gMailSender.sendMail(strTitle, strMessage,
                    strAddress, filePath);
            Toast.makeText(getApplicationContext(), "Sending email succeeded.",
                    Toast.LENGTH_SHORT).show();
        } catch (SendFailedException e) {
            Toast.makeText(getApplicationContext(), "Wrong email address.",
                    Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
