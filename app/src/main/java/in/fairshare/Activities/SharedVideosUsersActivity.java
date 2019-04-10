package in.fairshare.Activities;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import in.fairshare.Data.MyAdapter;
import in.fairshare.Data.SharedVideosUsersAdapter;
import in.fairshare.R;

import static in.fairshare.Data.SharedVideosUsersAdapter.username;

public class SharedVideosUsersActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    private RecyclerView sharedVideosUsersRecyclerView;

    private Context context;

    private Button shareVideosSaveButton;
    private EditText searchUsername;

    private SharedVideosUsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_videos_users);

        sharedVideosUsersRecyclerView = findViewById(R.id.sharedVideosUsersRecyclerViewID);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        DatabaseReference sharedVideosUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("UserAndID");

        sharedVideosUsersDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String userID = dataSnapshot.getKey(); // Return the userID
                String username = dataSnapshot.getValue(String.class); // Return username

                ((SharedVideosUsersAdapter)sharedVideosUsersRecyclerView.getAdapter()).updateUser(username, userID);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sharedVideosUsersRecyclerView.setLayoutManager(new LinearLayoutManager(SharedVideosUsersActivity.this));
        adapter = new SharedVideosUsersAdapter(sharedVideosUsersRecyclerView, SharedVideosUsersActivity.this, new ArrayList<String>(), new ArrayList<String>());
        sharedVideosUsersRecyclerView.setAdapter(adapter);
    }
}
