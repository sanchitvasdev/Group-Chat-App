package com.sanchit.groupchatappj.fragments;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sanchit.groupchatappj.ChatRoomActivity;
import com.sanchit.groupchatappj.R;

import butterknife.BindView;

/**
 * This fragment subclass represents a sign in page
 * through which user can login or create an account
 * using google account or input email address directly.
 *
 * @author Sanchit Vasdev
 * @version 1.0, 11/06/2020
 */
public class SignInFragment extends Fragment {
    GoogleSignInClient googleSignInClient ;
    @BindView(R.id.ortv)
    TextView ortv;

    /**
     * Declaring specific variables to be used multiple times.
     */
    private String TAG = "GoogleActivity";
    private int RC_SIGN_IN = 9001;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public SignInFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        /**
         * Configures sign-in options through google account.
         */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("371927571681-ss2gohn726rdojso14s1mrfmn5ng4of8.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(),gso);

        EditText emailIdEt = rootView.findViewById(R.id.emailIdEt);
        EditText passwordEt = rootView.findViewById(R.id.passwordEt);
        ImageView visibilityimg = rootView.findViewById(R.id.visibilityimg);
        Button nextBtnsignin = rootView.findViewById(R.id.nextBtnsignin);
        SignInButton signInBtn = rootView.findViewById(R.id.signInBtn);

        final boolean[] visibility = {false};

        /**
         * Controls visibility of the user password.
         */
        visibilityimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visibility[0] = !visibility[0];
                if(visibility[0]){
                    visibilityimg.setImageResource(R.drawable.ic_visible);
                    passwordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    visibilityimg.setImageResource(R.drawable.ic_invisible);
                    passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        final boolean[] check1 = {false};

        /**
         * Checking correct order of email address entered by user.
         */
        emailIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                check1[0] = editable.toString().contains("@gmail.com");
            }
        });

        final boolean[] check2 = {false};

        /**
         * Checking correct order of password entered by user
         * to enable next button.
         */
        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                check2[0] = editable.length() >= 8 && !editable.equals("");
                nextBtnsignin.setEnabled(check1[0] && check2[0]);
            }
        });

        /**
         * Implements sign in or creating user account
         * through email address and password entered by user.
         */
        nextBtnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signInWithEmailAndPassword(emailIdEt.getText().toString(),passwordEt.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Intent intent = new Intent(requireContext(), ChatRoomActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        auth.createUserWithEmailAndPassword(emailIdEt.getText().toString(),passwordEt.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Navigation.findNavController(requireActivity(),R.id.nav_host_fragment1).navigate(R.id.action_signInFragment_to_signUpFragment);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(),"Check your email address and password",Toast.LENGTH_SHORT).show();;
                            }
                        });
                    }
                });
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        return rootView;
    }

    /**
     * Starting the intent for sign in activity through google account.
     */
    private void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    /**
     * Gives the result of the signInIntent from googleSignInClient.
     *
     * @param requestCode Taking requestCode from intent.
     * @param resultCode Code for getting the result.
     * @param data Getting user data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
         */
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                /**
                 * If google Sign In was successful, authenticate with Firebase.
                 */
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG,"firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                /**
                 * If google Sign In failed, update UI appropriately.
                 */
                Log.w(TAG,"Google sign in failed",e);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /**
         * Check if user is signed in (non-null) and update UI accordingly.
         */
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    /**
     * Getting credentials of the signed in user.
     *
     * @param idToken Getting idToken of the user.
     */
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(),new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            /**
                             * If sign in is successful, update UI with the signed-in user's information
                             */
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        }else{
                            /**
                             * If sign in fails, display a message to the user.
                             */
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(requireView(), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * Updates UI with the current status of the user.
     *
     * @param user Getting current user.
     */
    private void updateUI(FirebaseUser user){
        if(user != null){
            check();
        }else{
        }
    }

    /**
     * Checking whether user has signed up already or not.
     */
    private void check(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference doc = database.collection("users").document(auth.getUid());
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot != null){
                    System.out.println(documentSnapshot.get("name"));
                    if(documentSnapshot.getData() == null){
                        Navigation.findNavController(requireActivity(),R.id.nav_host_fragment1).navigate(R.id.action_signInFragment_to_signUpFragment);
                    }else{
                        Intent intent = new Intent(requireContext(),ChatRoomActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}
