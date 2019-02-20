package in.fairshare.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

import in.fairshare.Data.CryptoUtils;
import in.fairshare.Data.RealPathUtil;
import in.fairshare.R;

public class MainActivity extends AppCompatActivity {

    private Button uploadButton;
    private Button videosButton;
    private Button sharedVideosButton;
    private Button profileButton;

    private Button chooseVideoButton;
    private TextInputLayout videoTitleEdtTxt;
    private TextInputLayout videoDescpEdtTxt;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private String filePath;
    private Uri uri;
    private static final int READ_REQUEST_CODE = 42;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadButton = findViewById(R.id.uploadButtonID);
        videosButton = findViewById(R.id.videosButtonID);
        sharedVideosButton = findViewById(R.id.sharedVideosButtonID);
        profileButton = findViewById(R.id.profileButtonID);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopup();
            }
        });

        videosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sharedVideosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mUser != null && mAuth != null) {
            mAuth.signOut();
        }
    }

    public void createPopup(){
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);


        videoTitleEdtTxt = view.findViewById(R.id.videoTitleEdtTxtID);
        videoDescpEdtTxt = view.findViewById(R.id.videoDescpEdtTxtID);
        chooseVideoButton = view.findViewById(R.id.chooseVideoButtonID);

        chooseVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE+Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat
                            .requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 42);
                }

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    performFileSearch();
                }

                //Toast.makeText(getApplicationContext(),"clicked", Toast.LENGTH_LONG).show();
            }
        });

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
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
            filePath = RealPathUtil.getRealPathFromURI_API19(MainActivity.this, uri);
        }

        //todo: encryption is done here! You don't have to do anything
            try {
                SecureRandom secureRandom = new SecureRandom();

                KeyGenerator keyGen;
                keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(secureRandom);

                Key key = keyGen.generateKey();

                File inputFile = new File(filePath);
                File encryptedFile = new File("/storage/emulated/0/enc-file.enc");

                CryptoUtils.encrypt(key, inputFile, encryptedFile);

            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"Exception" + e,Toast.LENGTH_LONG).show();
            }
    }
}