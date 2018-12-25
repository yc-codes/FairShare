package in.fairshare.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import in.fairshare.R;

public class RegistrationActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private StorageReference mFirebaseStorage;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;

    private EditText usernameRegistration;
    private EditText emailRegistration;
    private EditText passwordRegistration;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameRegistration = findViewById(R.id.usernameRegistrationID);
        emailRegistration = findViewById(R.id.emailRegistrationID);
        passwordRegistration = findViewById(R.id.passwordRegistrationID);
        signupButton = findViewById(R.id.signupButtonID);

        // Link DatabaseReference with Firebase Database
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUp();
            }
        });
    }

    private void signUp() {

        final String username = usernameRegistration.getText().toString().trim();
        final String email = emailRegistration.getText().toString().trim();
        final String password = passwordRegistration.getText().toString().trim();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)) {

            mProgressDialog.setMessage("Creating Account...");
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            String userID = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = mDatabaseReference.child(userID);
                            currentUserDb.child("Username").setValue(username);
                            currentUserDb.child("Email").setValue(email);
                            currentUserDb.child("Password").setValue(password);

                            mProgressDialog.dismiss();

                            // Send User to MainActivity
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
        } else {
            Toast.makeText(RegistrationActivity.this,"Please fill all the fields!!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
