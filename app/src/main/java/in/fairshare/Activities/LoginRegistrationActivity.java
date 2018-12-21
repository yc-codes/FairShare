package in.fairshare.Activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import in.fairshare.Fragments.RegistrationFragment;
import in.fairshare.R;

public class LoginRegistrationActivity extends AppCompatActivity {

    private EditText emailSignIn;
    private EditText passwordSignIn;
    private Button signInButton;
    private Button registrationSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);

        emailSignIn = findViewById(R.id.emailSignInID);
        passwordSignIn = findViewById(R.id.passwordSignInID);
        signInButton = findViewById(R.id.signInButtonID);
        registrationSignInButton = findViewById(R.id.registrationSignInButtonID);

        registrationSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment registrationFragment = new RegistrationFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        registrationFragment).commit();
            }
        });
    }
}
