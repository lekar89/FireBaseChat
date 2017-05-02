package pom.lekar.firebasechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    ListView mListView;
    Button mButton;
    EditText mEditText;
    ArrayAdapter<String> mArryaAdapter;
    ArrayList<String> listDailigs;
    String name;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    // public static final int SIGN_IN_REQUEST_CODE =777;
//    private FirebaseListAdapter<ChatMessage> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listDailigs = new ArrayList<>();
        mArryaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, listDailigs);
        mButton = (Button) findViewById(R.id.add_chat);
        mListView = (ListView) findViewById(R.id.list_of_chats);
        mEditText = (EditText) findViewById(R.id.input);
        mListView.setAdapter(mArryaAdapter);

        getUserName();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ChatRoom.class);
                intent.putExtra("chatRoom", listDailigs.get(position));
                intent.putExtra("userName", name);
                startActivity(intent);
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                map.put(mEditText.getText().toString(), "");
                root.updateChildren(map);
            }
        });
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot mDataSnapshot) {
                Iterator iterator = mDataSnapshot.getChildren().iterator();
                Set<String> set = new HashSet<String>();
                while (iterator.hasNext()) {
                    set.add(((DataSnapshot) iterator.next()).getKey());
                }
                listDailigs.clear();
                listDailigs.addAll(set);
                mArryaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError mDatabaseError) {

            }
        });
    }

    private void getUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name");
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = editText.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                getUserName();
            }
        });
        builder.show();
    }
}