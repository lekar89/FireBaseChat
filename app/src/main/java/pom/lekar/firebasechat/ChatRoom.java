package pom.lekar.firebasechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatRoom extends AppCompatActivity {
EditText mEditText;
    Button mButton;
    TextView mTextView;
String userName,roomName;
    DatabaseReference root;
    DatabaseReference messageRoot;
    String tmpKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        mEditText = (EditText) findViewById(R.id.message_text);
        mButton = (Button) findViewById(R.id.send_message);
        mTextView = (TextView) findViewById(R.id.chat_conversation);
        userName= getIntent().getStringExtra("userName");
        roomName= getIntent().getStringExtra("chatRoom");
        setTitle("Chat -  "+roomName );

        root = FirebaseDatabase.getInstance().getReference().child(roomName);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<String, Object>() ;
                 tmpKey = root.push().getKey();
                root.updateChildren(map);

                messageRoot = root.child(tmpKey);
                Map<String, Object> map2 = new HashMap<String, Object>() ;
                map2.put("name",userName);
                map2.put("msg",mEditText.getText().toString());
                messageRoot.updateChildren(map2);
                mEditText.setText("");
            }
        });
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot mDataSnapshot, String mS) {
                appenedChatConversation(mDataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot mDataSnapshot, String mS) {
                appenedChatConversation(mDataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot mDataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot mDataSnapshot, String mS) {

            }

            @Override
            public void onCancelled(DatabaseError mDatabaseError) {

            }
        });
    }
String chatMsg,chatUser;
    private void appenedChatConversation(DataSnapshot mDataSnapshot) {
        Iterator iterator= mDataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            chatMsg=(String)((DataSnapshot)iterator.next()).getValue();
            chatUser=(String)((DataSnapshot)iterator.next()).getValue();

            mTextView.append( chatUser+" : "+chatMsg+"\n");

        }
    }

}
