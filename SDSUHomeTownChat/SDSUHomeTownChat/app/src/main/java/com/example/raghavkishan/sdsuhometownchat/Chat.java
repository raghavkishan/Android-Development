package com.example.raghavkishan.sdsuhometownchat;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Chat extends AppCompatActivity implements MessageDataSource.MessagesCallbacks{

    String currentLoggedinUser;
    FirebaseAuth authenticationInstance;
    String recipient,ConvoId,chatListbuttonString;
    ListView listView;
    ArrayList<Message> messages;
    MessagesAdapter adapter;
    TextView recipientNameView;
    Button sendButton;
    EditText inputMessageView;
    MessageDataSource.MessagesListener listener;
    ListView chatListView;
    ArrayList<String> firebaseUsers = new ArrayList<String>();
    FirebaseDatabase firebaseDataBase;
    ArrayAdapter<String> chatListadapter;
    TextView currentUserTextViewDisp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recipient = getIntent().getStringExtra("clickedBismarkName");
        chatListbuttonString = getIntent().getStringExtra("onClickofButton");
        if(chatListbuttonString == null){
            chatListbuttonString = "notonclickofbutton";
        }

        currentUserTextViewDisp = (TextView) findViewById(R.id.chat_current_user_value);

        authenticationInstance = FirebaseAuth.getInstance();
        currentLoggedinUser = authenticationInstance.getCurrentUser().getDisplayName();
        currentUserTextViewDisp.setText(currentLoggedinUser);

        Log.i("assign5","selected recipient "+recipient);
        recipientNameView = (TextView) findViewById(R.id.chat_recipient_name_view);
        chatListView = (ListView)findViewById(R.id.chat_list_list_view);
        inputMessageView = (EditText) findViewById(R.id.chat_edit_text_view);

        firebaseDataBase = FirebaseDatabase.getInstance();
        DatabaseReference firebaseDatabaseReference = firebaseDataBase.getReference();

        firebaseDatabaseReference.child("people").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Iterable<DataSnapshot> childNodes = dataSnapshot.getChildren();
                for (DataSnapshot child : childNodes){
                    firebaseUsers.add(child.getKey());
                }
                firebaseUsers.remove(currentLoggedinUser);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatListadapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,firebaseUsers);
        chatListView.setAdapter(chatListadapter);

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedName = parent.getItemAtPosition(position).toString();
                recipient = clickedName;
                chatListShow();

            }
        });


        //Toast.makeText(this,"Current User Name in chat "+ currentLoggedinUser, Toast.LENGTH_LONG).show();
        Log.i("assign5","logged in user"+currentLoggedinUser);

        if (!(chatListbuttonString.equalsIgnoreCase("onClickofButton"))){

            listView = (ListView) findViewById(R.id.chat_history_list_view);
            messages = new ArrayList<>();
            adapter = new MessagesAdapter(messages);
            listView.setAdapter(adapter);

            String[] ids = {currentLoggedinUser, recipient};
            Arrays.sort(ids);
            ConvoId = ids[0] + "-" + ids[1];

            listener = MessageDataSource.addMessagesListener(ConvoId, this);
            recipientNameView.setText(recipient);

            sendButton = (Button) findViewById(R.id.chat_send_button);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage(v);
                }
            });
        }

    }

    public void chatListShow(){

        listView = (ListView) findViewById(R.id.chat_history_list_view);
        messages = new ArrayList<>();
        adapter = new MessagesAdapter(messages);
        listView.setAdapter(adapter);

        String[] ids = {currentLoggedinUser, recipient};
        Arrays.sort(ids);
        ConvoId = ids[0] + "-" + ids[1];

        listener = MessageDataSource.addMessagesListener(ConvoId, this);
        recipientNameView.setText(recipient);

        sendButton = (Button) findViewById(R.id.chat_send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
            }
        });
    }
    public void sendMessage(View v){
        String message = inputMessageView.getText().toString();
        inputMessageView.setText("");

        Message msg = new Message();
        msg.setDate(new Date());
        msg.setText(message);
        msg.setSender(currentLoggedinUser);

        MessageDataSource.saveMessage(msg, ConvoId);
    }

    @Override
    public void onMessageAdded(Message message) {
        messages.add(message);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageDataSource.stop(listener);
    }

    private class MessagesAdapter extends ArrayAdapter<Message> {
        MessagesAdapter(ArrayList<Message> messages){
            super(Chat.this, R.layout.message, R.id.message, messages);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            Message message = getItem(position);

            TextView nameView = (TextView)convertView.findViewById(R.id.message);
            nameView.setText(message.getText());

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();

            int sdk = Build.VERSION.SDK_INT;
            Log.i("assign5","message sender"+message.getSender()+" current user"+currentLoggedinUser);
            if (message.getSender().equals(currentLoggedinUser)){
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackground(getDrawable(R.drawable.bubble_right_green));
                } else{
                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubble_right_green));
                }
                layoutParams.gravity = Gravity.RIGHT;
            }else{
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackground(getDrawable(R.drawable.bubble_left_gray));
                } else{
                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubble_left_gray));
                }
                layoutParams.gravity = Gravity.LEFT;
            }

            nameView.setLayoutParams(layoutParams);


            return convertView;
        }
    }
}
