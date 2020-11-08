package com.sanchit.groupchatappj.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sanchit.groupchatappj.R;
import com.sanchit.groupchatappj.models.Message;
import com.squareup.picasso.Picasso;

/**
 * This class inflates and holds up layouts
 * on the ChatRoomActivity depending according
 * to various scenarios.
 *
 * @author Sanchit Vasdev
 * @version 1.0, 11/06/2020
 */


public class ChatAdapter extends FirestoreRecyclerAdapter<com.sanchit.groupchatappj.models.Message,ChatAdapter.ChatHolder> {
    Context context ;
    String userID ;
    private int CHAT_SEND_VIEW_TYPE=1 ;
    private int CHAT_RECV_VIEW_TYPE=2 ;
    public ChatAdapter(Context context, Query query, String userID) {

        super(new FirestoreRecyclerOptions.Builder<com.sanchit.groupchatappj.models.Message>()
                .setQuery(query, Message.class)
                .build());
        this.context=context ;
        this.userId=userID ;
    }
    private String userId = userID ;
    private FirebaseFirestore database ;
    private FirebaseAuth auth ;

    /**
     * If message userId matches current userid, set view type 1 else set view type 2.
     *
     * @param position Getting position of a particular message.
     * @return <code>1</code>, or
     *         <code>2</code>
     */
    @Override
    public int getItemViewType(int position) {
        if(getItem(position).getMessageUserId().equals(userId)) {
            return CHAT_SEND_VIEW_TYPE ;
        }else{
            return CHAT_RECV_VIEW_TYPE ;
        }
    }

    /**
     * Bind values from Message class to the viewHolder.
     * @param holder Holder of the adapter.
     * @param position Position of each message
     * @param model Bind the specified class model with holder.
     */
    @Override
    protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull com.sanchit.groupchatappj.models.Message model) {
        TextView text= holder.text ;
        TextView username = holder.username;
        TextView time = holder.time;
        ImageView image = holder.image;

        String id = getItem(position).getMessageUserId();
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        /**
         * Setting details of the sender user in the message.
         */
        DocumentReference docRef ;
        if(userId.equals(id)){
            docRef=database.collection("users").document(userId) ;
        }else{
            docRef=database.collection("users").document(id) ;
        }
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Glide.with(context).load(document.getData().get("thumbImage").toString()).into(image);
                       username.setText(document.getData().get("name").toString());
                    } else {
                        Toast.makeText(context, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
        text.setText(model.getMessageText());
        time.setText(DateFormat.format("dd MMM (h:mm a)",model.getMessageTime()));
    }

    /**
     *  Inflating two different layouts, one for messages from others and the other for user's messages.
     *
     *  @param parent Parent of the layouts.
     *  @param viewType viewType of the layouts.
     *  @return Returns a specific view to the ChatHolder.
     */
    @NonNull
    @Override

    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        if(viewType==CHAT_SEND_VIEW_TYPE){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_send_message,parent,false) ;
        }else{
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_recv_message,parent,false) ;
        }
        return new ChatHolder(view);
    }

    public class ChatHolder extends RecyclerView.ViewHolder{


        TextView text ;
        TextView username ;
        TextView time ;
        ImageView image ;
        /**
         * Holds the place in the recycler view for the itemView mentioned.
         *
         * @param itemView Takes the view passed.
         */
        public  ChatHolder(View itemView){
            super(itemView) ;
            text=itemView.findViewById(R.id.text);
            username=itemView.findViewById(R.id.userName) ;
            time=itemView.findViewById(R.id.time ) ;
            image=itemView.findViewById(R.id.userimgView) ;
        }
    }
}



