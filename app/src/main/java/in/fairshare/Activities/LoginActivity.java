package in.fairshare.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.fairshare.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private TextInputLayout emailLogin;
    private TextInputLayout passwordLogin;
    private Button loginButton;
    private TextView registrationTextLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.emailLoginID);
        passwordLogin = findViewById(R.id.passwordLoginID);
        loginButton = findViewById(R.id.loginButtonID);
        registrationTextLogin = findViewById(R.id.registrationTextLoginID);

        registrationTextLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mUser = firebaseAuth.getCurrentUser();

                if (mUser != null) {
                    Toast.makeText(LoginActivity.this, "Signed In!!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Not Signed In!!", Toast.LENGTH_LONG).show();
                }
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(emailLogin.getEditText().getText().toString()) &&
                        !TextUtils.isEmpty(passwordLogin.getEditText().getText().toString())) {

                    String email = emailLogin.getEditText().getText().toString();
                    String pwd = passwordLogin.getEditText().getText().toString();

                    login(email, pwd);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter valid Email and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login(String email, String pwd) {

        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            //  We are in!!!
                            Toast.makeText(LoginActivity.this, "Login!!", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect Email & Password!!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener); // To See User State whether it is login or not
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
