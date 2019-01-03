package in.fairshare.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.fairshare.Activities.LoginActivity;
import in.fairshare.R;

public class ProfileFragment extends Fragment {

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    private TextView signoutText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        signoutText = view.findViewById(R.id.signoutTextID);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        signoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mUser != null && mAuth != null) {
                    mAuth.signOut();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finishAffinity();
                }
            }
        });
        return view;
    }
}
