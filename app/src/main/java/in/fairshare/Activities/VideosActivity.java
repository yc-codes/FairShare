package in.fairshare.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import in.fairshare.R;

public class VideosActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private static String userID;

    private static DatabaseReference mDatabaseReference;

    private RecyclerView recyclerView;

    public String filename;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        recyclerView = findViewById(R.id.recyclerViewID);

        context = getApplicationContext();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Shared Video");

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
                        String fileName =  dataSnapshot.child("Filename").getValue(String.class);

                        ((MyAdapter)recyclerView.getAdapter()).update(videoTitle,videoDescp,videoUrl,key,fileName);
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
        MyAdapter adapter = new MyAdapter(recyclerView, VideosActivity.this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
        recyclerView.setAdapter(adapter);
    }

    public void delete(String filename) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Videos").child(userID).child(filename);
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

    public void share(String title, final String descp, final String url, final String key, final String filename) {

        final DatabaseReference shareDatabaseReference = mDatabaseReference.child(filename);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(descp) &&
                !TextUtils.isEmpty(url) && !TextUtils.isEmpty(key)
                    && !TextUtils.isEmpty(filename)) {

            shareDatabaseReference.child("Video Title").setValue(title).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if ( task.isSuccessful() ) {

                        shareDatabaseReference.child("Video Descp").setValue(descp);
                        shareDatabaseReference.child("URL").setValue(url);
                        shareDatabaseReference.child("Key").setValue(key);
                        shareDatabaseReference.child("Filename").setValue(filename);

                        Toast.makeText(context, "Video Successfully Shared!", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(context, "Video not Successfully Shared!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Video not Successfully Shared!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, "Video not Successfully Shared!", Toast.LENGTH_SHORT).show();
        }
    }
}
