package in.fairshare.Activities;

import android.app.ProgressDialog;
import android.content.Context;
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

import in.fairshare.Data.MyAdapter;
import in.fairshare.Data.SharedVideosAdapter;
import in.fairshare.R;

public class SharedVideosActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private static String userID;

    private RecyclerView sharedVideoRecyclerView;

    public String filename;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_videos);

        sharedVideoRecyclerView = findViewById(R.id.sharedVideoRecyclerViewID);

        context = getApplicationContext();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Shared Video").child(userID);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // Get Filename from Shared Videos table
                filename = dataSnapshot.getKey();
                Log.d("Filename", filename);

                DatabaseReference databaseReferenceMain = FirebaseDatabase.getInstance().getReference().child("Shared Video").child(userID).child(filename);

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Get information of video
                        String sharedVideoTitle = dataSnapshot.child("Video Title").getValue(String.class);
                        String sharedVideoDescp = dataSnapshot.child("Video Descp").getValue(String.class);
                        String sharedVideoUrl = dataSnapshot.child("URL").getValue(String.class);
                        String sharedVideoKey = dataSnapshot.child("Key").getValue(String.class);
                        String sharedVideoFileName = dataSnapshot.child("Filename").getValue(String.class);
                        String sharedVideoUsername = dataSnapshot.child("Username").getValue(String.class);
                        String sharedVideoDate = dataSnapshot.child("Video Shared Date").getValue(String.class);

                        // Update Adapter with the information of video
                        // So using that we can show shared video
                        ((SharedVideosAdapter)sharedVideoRecyclerView.getAdapter()).updateShare(sharedVideoTitle, sharedVideoDescp, sharedVideoUrl, sharedVideoKey, sharedVideoFileName, sharedVideoUsername, sharedVideoDate);
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

        // Bind RecyclerView and Activity with adapter
        sharedVideoRecyclerView.setLayoutManager(new LinearLayoutManager(SharedVideosActivity.this));
        SharedVideosAdapter adapter = new SharedVideosAdapter(sharedVideoRecyclerView, SharedVideosActivity.this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
        sharedVideoRecyclerView.setAdapter(adapter);
    }

    // Delete Shared Video
    public void deleteShareVideo(String filename) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Shared Video").child(userID).child(filename);
        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(context, "Video Successfully Deleted!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(context, "Can't Delete Video!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
