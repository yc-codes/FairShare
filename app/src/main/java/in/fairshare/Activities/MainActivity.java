package in.fairshare.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.fairshare.R;

public class MainActivity extends AppCompatActivity {

    private Button uploadButton;
    private Button videosButton;
    private Button sharedVideosButton;
    private Button profileButton;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

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

        Intent i = getIntent();
        final String emailFromLogin = i.getStringExtra("EmailFromLogin");
        final String emailFromRegistration = i.getStringExtra("EmailFromRegistration");

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                intent.putExtra("EmailFromLogin", emailFromLogin);
                intent.putExtra("EmailFromRegistration", emailFromRegistration);
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
}