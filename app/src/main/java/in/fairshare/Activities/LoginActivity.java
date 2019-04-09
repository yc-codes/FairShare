package in.fairshare.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.fairshare.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private TextInputLayout emailForgotPass;
    private Button sendEmailButton;
    private TextInputLayout emailLogin;
    private TextInputLayout passwordLogin;
    private Button loginButton;
    private TextView registrationTextLogin;
    private TextView forgotpassButton;

    private ProgressDialog mProgressDialog;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.emailLoginID);
        passwordLogin = findViewById(R.id.passwordLoginID);
        loginButton = findViewById(R.id.loginButtonID);
        registrationTextLogin = findViewById(R.id.registrationTextLoginID);
        forgotpassButton = findViewById(R.id.forgotPassTextViewID);
        forgotpassButton.setPaintFlags(forgotpassButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        mProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        registrationTextLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//                mUser = firebaseAuth.getCurrentUser();
//                String userID = mUser.getUid();
//
//                if (mUser != null) {
//                    // Toast.makeText(LoginActivity.this, "Signed In!!", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtra("UserID", userID);
//                    startActivity(intent);
//                    finishAffinity();
//                } else {
//                    // Toast.makeText(LoginActivity.this, "Not Signed In!!", Toast.LENGTH_LONG).show();
//                }
//            }
//        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(emailLogin.getEditText().getText().toString()) &&
                        !TextUtils.isEmpty(passwordLogin.getEditText().getText().toString())) {

                    String email = emailLogin.getEditText().getText().toString();
                    String pwd = passwordLogin.getEditText().getText().toString();

                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setMessage("Logging in...");
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.show();

                    login(email, pwd);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter valid Email and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });


        forgotpassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopup();
            }
        });
    }

    // popup dialog to reset the password
    public void createPopup() {
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.forgot_password_popup, null);

        emailForgotPass = view.findViewById(R.id.emailEdtTxtID);
        sendEmailButton = view.findViewById(R.id.sendButtonID);

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!emailForgotPass.getEditText().getText().toString().isEmpty()){
                    //sends an email to reset the password to the email address associated with the accounts
                    mAuth.sendPasswordResetEmail(emailForgotPass.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Email Sent !", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Enter a valid email address !", Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    emailForgotPass.setError("Enter email first");
                }
            }
        });

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void login(final String email, String pwd) {

        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            //  We are in!!!
                            // Toast.makeText(LoginActivity.this, "Login!!", Toast.LENGTH_LONG).show();
                            Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent1);
                            finishAffinity();

                            mProgressDialog.dismiss();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect Email & Password!!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener); // To See User State whether it is login or not
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }
}
