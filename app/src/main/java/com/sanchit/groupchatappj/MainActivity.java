package com.sanchit.groupchatappj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.os.Bundle;

import butterknife.BindView;

/**
 * This activity class contains sign in
 * and sign up fragments to be used
 * for respective purposes.
 *
 * @author Sanchit Vasdev
 * @version 1/0, 11/06/2020
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Setting your own Action Bar.
         */
        Toolbar toolbar_main = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment1);

    }
}