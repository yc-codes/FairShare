package in.fairshare.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import in.fairshare.R;

public class LoginFragment extends Fragment {

    private EditText emailLogin;
    private EditText passwordLogin;
    private Button loginButton;
    private Button registrationLoginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        registrationLoginButton = view.findViewById(R.id.registrationLoginButtonID);

        registrationLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.loginRegistrationActivityFragmentID, new RegistrationFragment());
                fragmentTransaction.commit();
            }
        });
        return view;
    }
}
