package in.fairshare.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

import in.fairshare.Data.CryptoUtils;
import in.fairshare.Data.RealPathUtil;
import in.fairshare.R;

public class popup_activity extends AppCompatActivity {

    private Button chooseVideoButton;
    private TextInputLayout videoTitleEdtTxt;
    private TextInputLayout videoDescpEdtTxt;
    private TextView videoPathTxtView;

    private static final int READ_REQUEST_CODE = 42;
    private Uri uri;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);

        videoTitleEdtTxt = findViewById(R.id.videoTitleEdtTxtID);
        videoDescpEdtTxt = findViewById(R.id.videoDescpEdtTxtID);
        //videoPathTxtView = findViewById(R.id.videoPathTxtViewID);
        chooseVideoButton = findViewById(R.id.chooseVideoButtonID);

        chooseVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(popup_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE+Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat
                            .requestPermissions(popup_activity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 42);
                }

                if (ContextCompat.checkSelfPermission(popup_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    performFileSearch();
                }
            }
        });
    }

    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("video/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uri = null;
            if (resultData != null) {
                uri = resultData.getData();
            }
        }

        if (Build.VERSION.SDK_INT > 19){
            filePath = RealPathUtil.getRealPathFromURI_API19(popup_activity.this, uri);
        }

        //todo: encryption is done here! You don't have to do anything

        if(videoTitleEdtTxt!=null && videoDescpEdtTxt!=null){
            try {
                SecureRandom secureRandom = new SecureRandom();

                KeyGenerator keyGen;
                keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(secureRandom);

                Key key = keyGen.generateKey();

                File inputFile = new File(filePath);
                File encryptedFile = new File("/storage/emulated/0/enc-file.enc");

                //File decryptedFile = new File("/storage/emulated/0/dec-file.mp4");

                CryptoUtils.encrypt(key, inputFile, encryptedFile);

                //CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
                videoPathTxtView.setText(filePath + " " + key.getEncoded());
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"Exception" + e,Toast.LENGTH_LONG).show();
            }
        }else{

        }
    }
}
