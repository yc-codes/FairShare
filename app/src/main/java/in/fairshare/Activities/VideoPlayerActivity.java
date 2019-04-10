package in.fairshare.Activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.common.util.Base64Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import in.fairshare.Data.CryptoException;
import in.fairshare.Data.CryptoUtils;
import in.fairshare.R;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private Uri videoUri;
    private String keyInString;
    private String videoUriInString;
    private Key key;
    private Bundle extras;
    private byte[] encodedKey;
    private File outFile;
    private File encFileFromServer;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoViewID);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        extras = getIntent().getExtras();
        keyInString = extras.getString("STRING_KEY");
        encodedKey = Base64Utils.decode(keyInString);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        key = new SecretKeySpec(encodedKey, 0, encodedKey.length,"AES");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl(extras.getString("URL")); // Creating a storage reference
                                                                                             // For downloading the file
        try {
            encFileFromServer = File.createTempFile("FileFromServer", "enc");
            outFile = File.createTempFile("decryptedFile", "mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading...");

        //shows a progress dialog until the file from server is decrypted
        progressDialog.show();

        //retrieve the encrypted file from server
        storageReference.getFile(encFileFromServer).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                try {
                    //decrypt the file received file from server
                    CryptoUtils.decrypt(key, encFileFromServer, outFile);
                    progressDialog.dismiss();
                } catch (CryptoException e) {
                    Toast.makeText(getApplicationContext(),"Exception: " + e.toString(), Toast.LENGTH_LONG).show();
                }

                videoView.setVideoURI(Uri.parse(outFile.getPath()));
                videoView.requestFocus(); // give focus to a specific view
                videoView.start();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Can't Play Video!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
