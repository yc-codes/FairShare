package in.fairshare.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import in.fairshare.Data.Adapter;
import in.fairshare.Data.VideoInformation;
import in.fairshare.R;

public class VideosActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    public String userID;

    private RecyclerView recyclerView;

    public String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        recyclerView = findViewById(R.id.recyclerViewID);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Videos").child(userID);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                filename = dataSnapshot.getKey();
                Log.d("Filename", filename);

                DatabaseReference databaseReferenceMain = FirebaseDatabase.getInstance().getReference().child("Videos").child(userID).child(filename);

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String videoTitle = dataSnapshot.child("Video Title").getValue(String.class);
                        String videoDescp = dataSnapshot.child("Video Descp").getValue(String.class);
                        String videoUrl = dataSnapshot.child("URL").getValue(String.class);
                        String key = dataSnapshot.child("Key").getValue(String.class);

                        ((Adapter)recyclerView.getAdapter()).update(videoTitle,videoDescp,videoUrl,key,filename);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.d("Video: ", databaseError.getMessage()); //Don't ignore errors!
                    }
                };
                databaseReferenceMain.addListenerForSingleValueEvent(valueEventListener);
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

        recyclerView.setLayoutManager(new LinearLayoutManager(VideosActivity.this));
        Adapter adapter = new Adapter(recyclerView, VideosActivity.this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
        recyclerView.setAdapter(adapter);
    }

    public void delete(String filename) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(userID).child(filename);
        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(VideosActivity.this, "Video Successfully Deleted!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(VideosActivity.this, "Can't Delete Video!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
