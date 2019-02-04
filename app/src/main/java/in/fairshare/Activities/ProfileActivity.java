package in.fairshare.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.fairshare.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView signoutText;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        signoutText = findViewById(R.id.signoutTextID);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Intent i = getIntent();
        String emailFromLogin = i.getStringExtra("EmailFromLogin");
        String emailFromRegistration = i.getStringExtra("EmailFromRegistration");

        Toast.makeText(ProfileActivity.this, emailFromLogin, Toast.LENGTH_SHORT).show();
        Toast.makeText(ProfileActivity.this, emailFromRegistration, Toast.LENGTH_SHORT).show();

        signoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mUser != null && mAuth != null) {
                    mAuth.signOut();
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finishAffinity();
                }
            }
        });
    }
}
