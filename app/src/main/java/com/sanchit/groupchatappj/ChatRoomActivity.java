package com.sanchit.groupchatappj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sanchit.groupchatappj.adapters.ChatAdapter;
import com.sanchit.groupchatappj.models.Message;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import butterknife.BindView;

/**
 * This activity class represents a chat room
 * where all registered users can chat directly.
 *
 * @author Sanchit Vasdev
 * @version 1.0, 11/06/2020
 */
public class ChatRoomActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{
    @BindView(R.id.toolbar_main)
    Toolbar toolbar_main ;
    @BindView(R.id.rootView)
    RelativeLayout rootView ;
    @BindView(R.id.msgEdtv)
    EmojiEditText msgEdtv ;
    @BindView(R.id.smileBtn)
    ImageView smileBtn ;
    FirebaseAuth auth= null  ;
    FirebaseUser user=null ;
    FirebaseFirestore database=null ;
    Query query=null ;
    private FirestoreRecyclerAdapter<Message, ChatAdapter.ChatHolder> adapter ;
    EmojiEditText input=null ;
    String userId=null ;
    String userName=null ;
    DrawerLayout drawer ;
    ActionBarDrawerToggle toggle ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Installing EmojiManager.
         */
        EmojiManager.install(new GoogleEmojiProvider());
        setContentView(R.layout.activity_chat_room);
        /**
         * Setting your own Action Bar.
         */
        toolbar_main = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);

        msgEdtv = findViewById(R.id.msgEdtv);
        rootView = findViewById(R.id.rootView);
        smileBtn = findViewById(R.id.smileBtn);

        drawer=findViewById(R.id.drawer_Layout) ;
        toggle= new ActionBarDrawerToggle(
                this,drawer,toolbar_main,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        /**
         * Adding Toggle for drawer actions.
         */
        drawer.addDrawerListener(toggle);

        /**
         * Configuring Action Bar.
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);

        NavigationView navigationview=findViewById(R.id.nav_View) ;
        navigationview.setNavigationItemSelectedListener(this) ;
        /**
         * Setting actions for emoji popup.
         */
        EmojiPopup emojiPopup=EmojiPopup.Builder.fromRootView(rootView).build(msgEdtv) ;
        smileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
            }
        });
        ImageView sendBtn=findViewById(R.id.sendBtn) ;
        sendBtn.setOnClickListener(this);
        input=findViewById(R.id.msgEdtv) ;
        /**
         * Setting layoutManager to the Recycler view.
         */
        RecyclerView recyclerView=findViewById(R.id.msgRv) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        auth=FirebaseAuth.getInstance() ;
        user=auth.getCurrentUser()  ;
        /**
         * Check if user has signed in before else redirect to sign in page.
         */
        if(user==null){
            Intent intent=new Intent(this,MainActivity.class) ;
            startActivity(intent);
            finish();
            return;
        }
        View navHeaderView=navigationview.getHeaderView(0) ;
        TextView nav_header_Name=navHeaderView.findViewById(R.id.nav_header_Name) ;
        ShapeableImageView nav_header_imageView=(ShapeableImageView) navHeaderView.findViewById(R.id.nav_header_imageView) ;
        TextView nav_header_Joineddate=(TextView)navHeaderView.findViewById(R.id.nav_header_Joineddate) ;
        /**
         * Setting profile details of the user in drawer header.
         */
        database=FirebaseFirestore.getInstance() ;
        DocumentReference docRef= database.collection("users").document(auth.getUid().toString()) ;
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                       nav_header_Name.setText(document.getData().get("name").toString());
                        Glide.with(ChatRoomActivity.this).load(document.getData().get("thumbImage").toString()).into(nav_header_imageView);
                        nav_header_Joineddate.setText(document.getData().get("joinedDate").toString());
                    } else {
                        Toast.makeText(ChatRoomActivity.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
        userId=user.getUid() ;
        userName=user.getDisplayName() ;
        /**
         * Ordering messages according to their time.
         */
        query=database.collection("messages").orderBy("messageTime") ;
        adapter = new ChatAdapter(ChatRoomActivity.this,query,userId);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Specifying the tasks for a particular item of the navigation menu when it's clicked.
     *
     * @param item Represents a particular item of navigation menu.
     * @return <code>true</code> if the action is successful, or
     *         <code>false</code> if the action fails.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            auth.signOut();
            Intent intent=new Intent(this,MainActivity.class) ;
            startActivity(intent);
            finish();
            Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show();
        }

     return false ;
    }

    /**
     * Adding message into database if it is not empty.
     *
     * @param view takes a view for applying its clicking action.
     */
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.sendBtn){
            String message=input.getText().toString() ;
            if(TextUtils.isEmpty(message)){
                Toast.makeText(this, "Type Something", Toast.LENGTH_SHORT).show();
                return;
            }
            database.collection("messages").add(new com.sanchit.groupchatappj.models.Message(userName,message,userId)) ;
            input.setText("");
        }
    }

    /**
     * Instructs adapter to start on starting activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null) {
            adapter.startListening();
        }
    }

    /**
     * Instructs adapter to stop on stopping activity.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }
    /**
     * Syncing toggle when an action changes.
     *
     * @param savedInstanceState Current state of the activity.
     */
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        toggle.syncState();
    }


    /**
     * Informing toggle about the changed configuration.
     *
     * @param newConfig Changed configuration of the activity.
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    /**
     * Implementing back pressed action of the drawer layout.
     */
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
}