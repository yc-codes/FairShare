package in.fairshare.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.fairshare.R;

public class ProfileActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    private TextView fullNameProfile;
    private TextView usernameProfile;
    private TextView emailProfile;
    private TextView changePasswordProfile;
    private TextView signoutButtonProfile;
    private TextView deleteAccountProfile;
    private ImageButton fullNameImageButton;
    private ImageButton usernameImageButton;
    private ImageButton emailImageButton;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference userAndIDDatabaseReference;
    private DatabaseReference videosDatabaseReference;
    private DatabaseReference sharedVideosDatabaseReference;
    private FirebaseUser user;
    private String userID;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private ProgressDialog mProgressDialog;

    private static TextInputLayout fullNamePopup;
    private Button changeFullNameButton;

    private static TextInputLayout usernamePopup;
    private Button changeUsernameButton;

    private static TextInputLayout emailPopup;
    private Button changeEmailButton;

    private static TextInputLayout passwordPopup;
    private static TextInputLayout confirmPasswordPopup;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        fullNameProfile = findViewById(R.id.fullNameProfileID);
        usernameProfile = findViewById(R.id.usernameProfileID);
        emailProfile = findViewById(R.id.emailProfileID);
        changePasswordProfile = findViewById(R.id.changePasswordProfileID);
        signoutButtonProfile = findViewById(R.id.signoutButtonProfileID);
        deleteAccountProfile = findViewById(R.id.deleteAccountProfileID);
        fullNameImageButton = findViewById(R.id.fullNameImageButtonID);
        usernameImageButton = findViewById(R.id.usernameImageButtonID);
        emailImageButton = findViewById(R.id.emailImageButtonID);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Intent i = getIntent();
        userID = i.getStringExtra("userID");

        user = MainActivity.uUser; // Getting current user

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        userAndIDDatabaseReference = FirebaseDatabase.getInstance().getReference().child("UserAndID").child(userID);
        videosDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Videos").child(userID);
        sharedVideosDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Shared Video").child(userID);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Getting current user data from Users Table
                String email = dataSnapshot.child("Email").getValue(String.class);
                String fullName = dataSnapshot.child("FullName").getValue(String.class);
                String userName = dataSnapshot.child("Username").getValue(String.class);

                // Putting them into there respected TextView
                emailProfile.setText(email);
                fullNameProfile.setText(fullName);
                usernameProfile.setText(userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Username: ", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);

        fullNameImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFullNamePopup();
            }
        });

        usernameImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUsernamePopup();
            }
        });

        emailImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEmailPopup();
            }
        });

        changePasswordProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPasswordPopup();
            }
        });

        signoutButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(ProfileActivity.this, R.style.AppCompatAlertDialogStyle);

                dialogBuilder.setCancelable(false);
                dialogBuilder.setMessage("Are you sure you want to Sign Out?");
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //sign out of the account
                        if(mAuth != null) {
                            mAuth.signOut();
                            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                            finishAffinity();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Unable to SignOut", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                dialog = dialogBuilder.create();
                dialog.show();

            }
        });

        deleteAccountProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Deleting Account...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                dialogBuilder = new AlertDialog.Builder(ProfileActivity.this, R.style.AppCompatAlertDialogStyle);

                dialogBuilder.setCancelable(false);
                dialogBuilder.setMessage("Are you sure you want to delete your account?");
                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mProgressDialog.show();

                        // Remove Users Account From Database
                        mDatabaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                userAndIDDatabaseReference.removeValue();
                                videosDatabaseReference.removeValue();
                                sharedVideosDatabaseReference.removeValue();
                                user.delete();

                                Toast.makeText(getApplicationContext(), "Account Deleted!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finishAffinity();
                                mProgressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(getApplicationContext(), "Account not Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                dialog = dialogBuilder.create();
                dialog.show();
            }
        });
    }

    public void createFullNamePopup() {
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.fullname_popup, null);

        mProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Changing Full Name...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        fullNamePopup = view.findViewById(R.id.fullNamePopupID);
        changeFullNameButton = view.findViewById(R.id.changeFullNameButtonID);

        changeFullNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateFullName()) {
                    return;
                }
                mProgressDialog.show();
                if(!fullNamePopup.getEditText().getText().toString().isEmpty()){
                    mDatabaseReference.child("FullName").setValue(fullNamePopup.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Full Name Changed!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Full Name not Changed!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }else {
                    fullNamePopup.setError("Enter Full Name First");
                }
            }
        });

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private boolean validateFullName() {

        if (fullNamePopup.getEditText().getText().toString().trim().isEmpty()) {
            fullNamePopup.setError("Field can't be empty");
            return false;
        } else if (!validateLetters(fullNamePopup.getEditText().getText().toString().trim())) { // Self Made
            fullNamePopup.setError("Full Name can only contains letters");
            return false;
        } else {
            fullNamePopup.setError(null);
            return true;
        }
    }

    public static boolean validateLetters(String txt) {

        String regx = "^[a-zA-Z\\s]*$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(txt);
        return matcher.find();
    }

    public void createUsernamePopup() {
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.username_popup, null);

        mProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Changing Username...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        usernamePopup = view.findViewById(R.id.usernamePopupID);
        changeUsernameButton = view.findViewById(R.id.changeUsernameButtonID);

        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateUsername()) {
                    return;
                }
                mProgressDialog.show();
                if(!usernamePopup.getEditText().getText().toString().isEmpty()){
                    mDatabaseReference.child("Username").setValue(usernamePopup.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            userAndIDDatabaseReference.setValue(usernamePopup.getEditText().getText().toString());
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Username Changed!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Username not Changed!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }else {
                    fullNamePopup.setError("Enter Username First");
                }
            }
        });

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private boolean validateUsername() {

        if (usernamePopup.getEditText().getText().toString().trim().isEmpty()) {
            usernamePopup.setError("Field can't be empty");
            return false;
        } else if (usernamePopup.getEditText().getText().toString().trim().length() > 15) {
            usernamePopup.setError("Username too long");
            return false;
        } else {
            usernamePopup.setError(null);
            return true;
        }
    }

    public void createEmailPopup() {
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.email_popup, null);

        mProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Changing Email...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        emailPopup = view.findViewById(R.id.emailPopupID);
        changeEmailButton = view.findViewById(R.id.changeEmailButtonID);

        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateEmail()) {
                    return;
                }
                mProgressDialog.show();
                if(!emailPopup.getEditText().getText().toString().isEmpty()){
                    user.updateEmail(emailPopup.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseReference.child("Email").setValue(emailPopup.getEditText().getText().toString());
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Email Changed!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Email not Changed!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }else {
                    emailPopup.setError("Enter Email First");
                }
            }
        });

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private boolean validateEmail() {

        if (emailPopup.getEditText().getText().toString().trim().isEmpty()) {
            emailPopup.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailPopup.getEditText().getText().toString().trim()).matches()) { // Inbuild Function
            emailPopup.setError("Please enter a valid email address");
            return false;
        } else {
            emailPopup.setError(null);
            return true;
        }
    }

    public void createPasswordPopup() {
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.password_popup, null);

        mProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Changing Password...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        passwordPopup = view.findViewById(R.id.passwordPopupID);
        confirmPasswordPopup = view.findViewById(R.id.confirmPasswordPopupID);
        changePasswordButton = view.findViewById(R.id.changePasswordButtonID);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validatePassword()) {
                    return;
                }

                if (passwordPopup.getEditText().getText().toString().trim()
                        .equals(confirmPasswordPopup.getEditText().getText().toString().trim())) {

                    mProgressDialog.show();
                    if (!passwordPopup.getEditText().getText().toString().isEmpty()) {
                        user.updatePassword(passwordPopup.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mDatabaseReference.child("Password").setValue(passwordPopup.getEditText().getText().toString());
                                mProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Password Changed!", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Password not Changed!", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    } else {
                        emailPopup.setError("Enter Password First");
                    }
                } else {
                    confirmPasswordPopup.setError("Password does not match!");
                }
            }
        });

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private boolean validatePassword() {

        if (passwordPopup.getEditText().getText().toString().trim().isEmpty()) {
            passwordPopup.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordPopup.getEditText().getText().toString().trim()).matches()) { // Self Made
            passwordPopup.setError("Password too weak. It must contain letters, symbol, digits and must be a 4 character");
            return false;
        } else {
            passwordPopup.setError(null);
            return true;
        }
    }
}
