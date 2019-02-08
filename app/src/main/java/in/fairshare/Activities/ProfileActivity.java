package in.fairshare.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.fairshare.Data.UserInformation;
import in.fairshare.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView fullNameProfile;
    private TextView usernameProfile;
    private TextView emailProfile;
    private TextView signoutButtonProfile;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullNameProfile = findViewById(R.id.fullNameProfileID);
        usernameProfile = findViewById(R.id.usernameProfileID);
        emailProfile = findViewById(R.id.emailProfileID);
        signoutButtonProfile = findViewById(R.id.signoutButtonProfileID);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid();

        Intent i = getIntent();
        // userID = i.getStringExtra("UserID");

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String email = dataSnapshot.child("Email").getValue(String.class);
                String fullName = dataSnapshot.child("FullName").getValue(String.class);
                String userName = dataSnapshot.child("Username").getValue(String.class);

                emailProfile.setText(email);
                fullNameProfile.setText(fullName);
                usernameProfile.setText(userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Username: ", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);

        signoutButtonProfile.setOnClickListener(new View.OnClickListener() {
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
