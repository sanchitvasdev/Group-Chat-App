package com.sanchit.groupchatappj;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This activity class opens up other app activities.
 *
 * @author Sanchit Vasdev
 * @version 11/06/2020
 */

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (auth.getCurrentUser() == null) {
            /**
             * If the user does not exists, open MainActivity for sign in and sign up.
             */
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            /**
             * If the user exists, directly open ChatRoomActivity.
             */
            startActivity(new Intent(getApplicationContext(), ChatRoomActivity.class));
        }
        finish();
    }
}