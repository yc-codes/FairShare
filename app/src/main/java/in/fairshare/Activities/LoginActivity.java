package in.fairshare.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import in.fairshare.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin;
    private EditText passwordLogin;
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
    }
}
