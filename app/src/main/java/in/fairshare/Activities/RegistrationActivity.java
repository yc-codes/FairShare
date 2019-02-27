package in.fairshare.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import in.fairshare.R;

public class RegistrationActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;

    private TextInputLayout usernameRegistration;
    private TextInputLayout fullNameRegistration;
    private TextInputLayout emailRegistration;
    private TextInputLayout passwordRegistration;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameRegistration = findViewById(R.id.usernameRegistrationID);
        fullNameRegistration = findViewById(R.id.fullNameRegistrationID);
        emailRegistration = findViewById(R.id.emailRegistrationID);
        passwordRegistration = findViewById(R.id.passwordRegistrationID);
        signupButton = findViewById(R.id.signupButtonID);

        // Link DatabaseReference with Firebase Database
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validateFullName() | !validateEmail() | !validatePassword()) {
                    return;
                }
                signUp();
            }
        });
    }

    public void signUp() {

        final String username = usernameRegistration.getEditText().getText().toString().trim();
        final String fullName = fullNameRegistration.getEditText().getText().toString();
        final String email = emailRegistration.getEditText().getText().toString().trim();
        final String password = passwordRegistration.getEditText().getText().toString().trim();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(fullName) &&
                !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Creating Account...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            if (authResult != null) {

                                String userID = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDb = mDatabaseReference.child(userID);
                                currentUserDb.child("Username").setValue(username);
                                currentUserDb.child("FullName").setValue(fullName);
                                currentUserDb.child("Email").setValue(email);
                                currentUserDb.child("Password").setValue(password);

                                mProgressDialog.dismiss();

                                // Send User to MainActivity

                                Intent intent = new Intent(RegistrationActivity.this,
                                        MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finishAffinity();
                            }
                        }
                    });
        } else {
            Toast.makeText(RegistrationActivity.this,"Please fill all the fields!!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateUsername() {
        String usernameInput = usernameRegistration.getEditText().getText().toString().trim();

        if (usernameInput.isEmpty()) {
            usernameRegistration.setError("Field can't be empty");
            return false;
        } else if (usernameInput.length() > 15) {
            usernameRegistration.setError("Username too long");
            return false;
        } else {
            usernameRegistration.setError(null);
            return true;
        }
    }

    private boolean validateFullName() {
        String usernameInput = fullNameRegistration.getEditText().getText().toString().trim();

        if (usernameInput.isEmpty()) {
            fullNameRegistration.setError("Field can't be empty");
            return false;
        } else {
            fullNameRegistration.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = emailRegistration.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            emailRegistration.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) { // Inbuild Function
            emailRegistration.setError("Please enter a valid email address");
            return false;
        } else {
            emailRegistration.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = passwordRegistration.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            passwordRegistration.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) { // Self Made
            passwordRegistration.setError("Password too weak. It must contain letters, symbol, digits and must be a 4 character");
            return false;
        } else {
            passwordRegistration.setError(null);
            return true;
        }
    }
}
