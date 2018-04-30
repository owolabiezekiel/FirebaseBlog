package com.fitn.blog.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fitn.blog.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CreateAccountActivity extends AppCompatActivity {
    private ImageButton profilePicture;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Button createAccountButton;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;
    private Uri resultUri = null;
    private static final int GALLERY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference().child("Profile_Images");

        mProgressDialog =  new ProgressDialog(this);
        profilePicture = (ImageButton) findViewById(R.id.profilePic);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.createEmail);
        password = (EditText) findViewById(R.id.createPassword);
        createAccountButton = (Button) findViewById(R.id.createAccount);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri mImageUri = data.getData();
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            CropImage.activity(mImageUri)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                profilePicture.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void createNewAccount() {
        final String fname = firstName.getText().toString().trim();
        final String lname = lastName.getText().toString().trim();
        String em = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname)
                && !TextUtils.isEmpty(em) && !TextUtils.isEmpty(pass)){
            mProgressDialog.setMessage("Creating Account");
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(em, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (authResult != null){
                        StorageReference profileImagePath = mStorage.child("Profile_Images").child(resultUri.getLastPathSegment());
                        profileImagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String userID = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUser = mDatabaseReference.child(userID);
                                currentUser.child("firstname").setValue(fname);
                                currentUser.child("lastname").setValue(lname);
                                currentUser.child("image").setValue(resultUri.toString());

                                mProgressDialog.dismiss();
                                //Go straight to PostList
                                Intent intent = new Intent(CreateAccountActivity.this, PostListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });

                    }
                }
            });
        } else {
            Toast.makeText(this, "Some fields are empty...", Toast.LENGTH_LONG).show();
        }
    }
}
