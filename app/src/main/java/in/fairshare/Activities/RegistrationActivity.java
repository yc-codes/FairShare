package in.fairshare.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import in.fairshare.R;

public class RegistrationActivity extends AppCompatActivity {

    private EditText usernameRegistration;
    private EditText fullNameRegistration;
    private EditText emailRegistration;
    private EditText passwordRegistration;
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
    }
}
