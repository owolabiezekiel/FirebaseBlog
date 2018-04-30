package com.fitn.blog.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fitn.blog.Model.Blog;
import com.fitn.blog.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class AddPostActivity extends AppCompatActivity {
    private ImageButton mPostImage;
    private TextView mPostTitle;
    private TextView mPostDesc;
    private Button mSubmitButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private ProgressDialog mProgressDialog;
    private Uri mImageURI;
    private static final int GALLERY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mDatabaseReference = mDatabase.getReference().child("Blog");
        mDatabaseReference.keepSynced(true);
        mProgressDialog = new ProgressDialog(this);


        mPostImage = (ImageButton) findViewById(R.id.imageButtonED);
        mPostTitle = (TextView) findViewById(R.id.postTitleED);
        mPostDesc = (TextView) findViewById(R.id.postDescED);
        mSubmitButton = (Button) findViewById(R.id.submitPost);

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            mImageURI = data.getData();
            mPostImage.setImageURI(mImageURI);
        }
    }

    private void startPosting() {


        final String titleString = mPostTitle.getText().toString().trim();
        final String descString = mPostDesc.getText().toString().trim();
        if (!TextUtils.isEmpty(titleString) && !TextUtils.isEmpty(descString) && mImageURI != null){
            mProgressDialog.setMessage("Saving Post...");
            mProgressDialog.show();
            //Start Uploading to RealTime Database
            StorageReference filepath = mStorageReference.child("Blog_Images").child(mImageURI.getLastPathSegment());
            filepath.putFile(mImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadURL = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost = mDatabaseReference.push();
                    Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("title", titleString);
                    dataToSave.put("description", descString);
                    dataToSave.put("image", downloadURL.toString());
                    dataToSave.put("timeStamp", String.valueOf(java.lang.System.currentTimeMillis()));
                    dataToSave.put("userID", mUser.getUid());
                    newPost.setValue(dataToSave);
                    mProgressDialog.dismiss();
                    startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "Make sure all fields are filled and an image is selected", Toast.LENGTH_LONG).show();
        }
    }


}
