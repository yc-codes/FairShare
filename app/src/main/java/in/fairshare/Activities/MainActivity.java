package in.fairshare.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.Base64Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.Serializable;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import in.fairshare.Data.CryptoUtils;
import in.fairshare.Data.RealPathUtil;
import in.fairshare.R;

public class MainActivity extends AppCompatActivity {

    private Button uploadButton;
    private Button videosButton;
    private Button sharedVideosButton;
    private Button profileButton;

    private TextInputLayout videoTitleEdtTxt;
    private TextInputLayout videoDescpEdtTxt;
    private Button chooseVideoButton;
    private Button uploadVideoButton;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private String filePath;
    private Uri uri;
    private Uri encFilePathUri;
    private static final int READ_REQUEST_CODE = 42;
    private File inputFile;
    private File encryptedFile;

    private FirebaseStorage storage; // Used for uploading file
    private FirebaseDatabase database; // Used to store URLs of uploaded file
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    public static FirebaseUser uUser;
    String userID;

    private ProgressDialog progressDialog;

    String videoTitle;
    String videoDescp;

    private SecretKey keyFromString;
    private String stringKey;
    private byte[] encodedKey;

    private static String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadButton = findViewById(R.id.uploadButtonID);
        videosButton = findViewById(R.id.videosButtonID);
        sharedVideosButton = findViewById(R.id.sharedVideosButtonID);
        profileButton = findViewById(R.id.profileButtonID);

        storage = FirebaseStorage.getInstance(); // Return object of firebase storage
        database = FirebaseDatabase.getInstance(); // Return object of firebase database
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid(); // Getting Current User userID

        uUser = FirebaseAuth.getInstance().getCurrentUser(); // Get Current Logged in User

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopup();
            }
        });

        videosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, VideosActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        sharedVideosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SharedVideosActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
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
        // User account gets automatically signout when he/she close the app
        super.onStop();
        if(mUser != null && mAuth != null) {
            mAuth.signOut();
        }
    }

    //popup to choose video along with its title and description
    public void createPopup(){
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);

        videoTitleEdtTxt = view.findViewById(R.id.videoTitleEdtTxtID);
        videoDescpEdtTxt = view.findViewById(R.id.videoDescpEdtTxtID);
        chooseVideoButton = view.findViewById(R.id.chooseVideoButtonID);
        uploadVideoButton = view.findViewById(R.id.uploadVideoButtonID);

        chooseVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ask for permissions if not granted
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE+Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat
                            .requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 42);
                } else {
                    performFileSearch();
                }
            }
        });

        uploadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                videoTitle = videoTitleEdtTxt.getEditText().getText().toString();
                videoDescp = videoDescpEdtTxt.getEditText().getText().toString();

                if (!TextUtils.isEmpty(videoTitle) && !TextUtils.isEmpty(videoDescp)) {

                    if (encFilePathUri != null) {
                        uploadVideo(encFilePathUri);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please choose a video", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide video title and description", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 42 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            performFileSearch();
        } else {

            Toast.makeText(MainActivity.this, "Please provide permission", Toast.LENGTH_SHORT).show();
        }
    }

    //opens local video  files list
    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*"); //allows to choose only the video files
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            uri = null;

            if (resultData != null) {

                uri = resultData.getData();

                //obtain the real file path from URI
                filePath = RealPathUtil.getRealPathFromURI(MainActivity.this, uri);
            }
        }

        // Encryption of the video file to upload
        try {
            KeyGenerator keyGen;
            keyGen = KeyGenerator.getInstance("AES"); // key generator to generate key for AES Algorithm
            keyGen.init(128); // initializing the key generator to 128 bit key size

            SecretKey secretKey = keyGen.generateKey(); // generating the secret key of 128 bits

            stringKey = Base64Utils.encode(secretKey.getEncoded());
            encodedKey = Base64Utils.decode(stringKey);
            keyFromString = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

            inputFile = new File(filePath);
            encryptedFile = File.createTempFile("encryptedFile","enc");
            encFilePathUri = Uri.fromFile(encryptedFile);

            CryptoUtils.encrypt(keyFromString, inputFile, encryptedFile); //encrypting the video file
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please choose a video", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVideo(Uri encFilePathUri) {

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setTitle("Uploading Video...");

        progressDialog.show();

        final String fileName = System.currentTimeMillis() + ".enc"; // "" used to cast it to String
        final String fileName1 = System.currentTimeMillis() +  "";
        final StorageReference storageReference = storage.getReference(); // Return root path

        // Uploading Encrypted Video File in FirebaseStorage
        storageReference.child("Videos").child(fileName).putFile(encFilePathUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // On Success of upload getting DownloadUrl so using that we can download file
                        String url = taskSnapshot.getDownloadUrl().toString(); // Return url of uploaded file

                        // Reference to the Users Table
                        DatabaseReference databaseReferenceForUsername = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                        // Use to get data from Users table
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                // Getting username of current user from Users table
                                userName = dataSnapshot.child("Username").getValue(String.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("Username: ", databaseError.getMessage()); //Don't ignore errors!
                            }
                        };
                        databaseReferenceForUsername.addListenerForSingleValueEvent(valueEventListener);

                        // Reference to the Videos Table
                        final DatabaseReference databaseReference = database.getReference().child("Videos").child(userID).child(fileName1); // Return root path

                        // Adding download url of uploaded video into videos table
                        databaseReference.child("URL").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if ( task.isSuccessful() ) {

                                    // On Success of this adding video title,descp,etc.. into videos table
                                    databaseReference.child("Video Title").setValue(videoTitle);
                                    databaseReference.child("Video Descp").setValue(videoDescp);
                                    databaseReference.child("Key").setValue(stringKey);
                                    databaseReference.child("Filename").setValue(fileName1);
                                    databaseReference.child("Username").setValue(userName);
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Video Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Video not successfully uploaded", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Video not successfully uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                // Track the progress of video upload
                // Count how much percentage data upload
                int currentProgress = (int) ( 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount() );
                progressDialog.setProgress(currentProgress);
            }
        });
    }
}