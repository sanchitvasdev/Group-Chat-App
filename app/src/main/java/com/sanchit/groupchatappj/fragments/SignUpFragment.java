package com.sanchit.groupchatappj.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sanchit.groupchatappj.ChatRoomActivity;
import com.sanchit.groupchatappj.R;
import com.sanchit.groupchatappj.models.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;

import butterknife.BindView;

/**
 * This fragment subclass represents sign up page
 * where user can enter his/her details such as
 * photo,name,state and city.
 *
 * @author Sanchit Vasdev
 * @version 1.0, 11/06/2020
 */
public class SignUpFragment extends Fragment {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    String downloadUrl = null;
    @BindView(R.id.userImgView)
    ShapeableImageView userImgView;
    @BindView(R.id.nameEt)
    EditText nameEt;
    @BindView(R.id.StateEt)
    EditText StateEt;
    @BindView(R.id.CityEt)
    EditText CityEt;
    @BindView(R.id.nextBtn)
    Button nextBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        userImgView = (ShapeableImageView) rootView.findViewById(R.id.userImgView);
        nextBtn = (Button) rootView.findViewById(R.id.nextBtn);
        nameEt = rootView.findViewById(R.id.nameEt);
        StateEt = rootView.findViewById(R.id.StateEt);
        CityEt = rootView.findViewById(R.id.CityEt);
        userImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("I AM HERE");
                checkPermissionForImage();
            }
        });

        /**
         * Checks whether user has entered all the details.
         */
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEt.getText().toString();
                String state = StateEt.getText().toString();
                String city = CityEt.getText().toString();
                System.out.println(downloadUrl);
                if (downloadUrl == null) {
                    Toast.makeText(requireActivity(), "Image cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()) {
                    Toast.makeText(requireActivity(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (state.isEmpty()) {
                    Toast.makeText(requireActivity(), "State cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (city.isEmpty()) {
                    Toast.makeText(requireActivity(), "City cannot be empty", Toast.LENGTH_SHORT).show();
                } else {

                    /**
                     * If all details are filled create a new user instance.
                     */
                    User user = new User(name, state, city, downloadUrl, downloadUrl, auth.getUid(), LocalDate.now().toString());

                    /**
                     * Add the user into database and starts ChatRoomActivity.
                     */
                    database.collection("users").document(auth.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(requireContext(), ChatRoomActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Intent intent = new Intent(requireContext(), ChatRoomActivity.class);
                            startActivity(intent);
                            nextBtn.setEnabled(true);

                        }
                    });
                }
            }
        });

        return rootView;
    }

    /**
     * Checks whether user has allowed permissions for taking images from his/her storage.
     */
    private void checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED)
                    && (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED)
            ) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1002);

                /**
                 * Request permission again if users denies the permission.
                 */
            } else {
                /**
                 * Calls pickImageFromGallery() function if user allows permissions.
                 */
                pickImageFromGallery();
            }
        }
    }

    /**
     * Picks the image chosen by user from gallery.
     */
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,
                1000);
    }

    /**
     * Gives the result of the Intent.ACTION_PICK.
     *
     * @param requestCode Taking requestCode from intent.
     * @param resultCode  Code for getting the result.
     * @param data        Getting user data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            Uri uri = data.getData();
            userImgView.setImageURI(uri);
            startUpload(uri);
        }
    }

    /**
     * Uploads image into firebase storage.
     *
     * @param it Passed uri object.
     */
    private void startUpload(Uri it) {
        nextBtn.setEnabled(false);
        StorageReference ref = storage.getReference().child("uploads/" + auth.getUid());
       UploadTask uploadTask = ref.putFile(it);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {

                    /**
                     * Throws Exception if task is not successful.
                     */
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                System.out.println("Gadha");
                if (task.isSuccessful()) {
                    downloadUrl = task.getResult().toString();
                    nextBtn.setEnabled(true);
                } else {
                    nextBtn.setEnabled(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
